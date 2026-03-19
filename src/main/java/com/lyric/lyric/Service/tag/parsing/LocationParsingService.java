package com.lyric.lyric.Service.tag.parsing;

import ch.hsr.geohash.GeoHash;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyric.lyric.Mapper.relation.DiaryLocationMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.lyric.lyric.Utils.stringProcessing.stringUtils.listToString;
import static com.lyric.lyric.Utils.stringProcessing.stringUtils.stringToList;

/**
 * 地点标签处理服务类
 *
 * @author Yichaoxuan
 * @since 2026-02-01
 */
@Slf4j
@Service
public class LocationParsingService {

    private final LocationMapper locationMapper;
    private final DiaryLocationMapper diaryLocationMapper;
    private final AIAnalysisService aiAnalysisService;
    private final UserSettingsService userSettingsService;
    private final ObjectMapper objectMapper;

    public LocationParsingService(LocationMapper locationMapper,
                                  DiaryLocationMapper diaryLocationMapper,
                                  AIAnalysisService aiAnalysisService,
                                  UserSettingsService userSettingsService) {
        this.locationMapper = locationMapper;
        this.diaryLocationMapper = diaryLocationMapper;
        this.aiAnalysisService = aiAnalysisService;
        this.userSettingsService = userSettingsService;
        this.objectMapper = new ObjectMapper();
    }


    /**
     * 地点标签去重器
     * 通过多级匹配策略判断是否为同一地点，更新或插入数据库
     * 匹配顺序：国家 -> 省份 -> 城市 -> 区县 -> 地点名称 -> AI匹配
     *
     * @param diaryId         日记id
     * @param locationInfoMap 新地点信息映射
     */
    @Transactional
    public void locationDeduplication(Integer diaryId,
                                      Map<String, AITagJson.LocationInfo> locationInfoMap) {

        for (Map.Entry<String, AITagJson.LocationInfo> entry : locationInfoMap.entrySet()) {
            String newLocationName = entry.getKey();
            AITagJson.LocationInfo newLocationInfo = entry.getValue();

            if (newLocationInfo == null) {
                log.warn("地点信息为空，跳过处理: {}", newLocationName);
                continue;
            }

            logMatchingProcess(newLocationName, null, "开始处理");
            log.info("开始一级匹配：国家：{}", newLocationInfo.getCountry());

            try {
                // 1. 按国家筛选
                List<LocationPojo> countryCandidates = findByCountry(newLocationInfo.getCountry());
                logMatchingProcess(newLocationName, countryCandidates, "国家匹配");

                if (countryCandidates.isEmpty()) {
                    // 无匹配国家，直接创建新地点
                    addNewLocation(diaryId, newLocationName, newLocationInfo);
                    continue;
                }

                // 2. 按省份匹配
                log.info("开始二级匹配：省份：{}", newLocationInfo.getProvince());
                List<LocationPojo> provinceCandidates = filterByField(
                        newLocationInfo.getProvince(), countryCandidates, LocationPojo::getProvince);
                logMatchingProcess(newLocationName, provinceCandidates, "省份匹配");

                if (provinceCandidates.isEmpty()) {
                    addNewLocation(diaryId, newLocationName, newLocationInfo);
                    continue;
                }

                // 3. 按城市匹配
                log.info("开始三级匹配：城市：{}", newLocationInfo.getCity());
                List<LocationPojo> cityCandidates = filterByField(
                        newLocationInfo.getCity(), provinceCandidates, LocationPojo::getCity);
                logMatchingProcess(newLocationName, cityCandidates, "城市匹配");

                if (cityCandidates.isEmpty()) {
                    addNewLocation(diaryId, newLocationName, newLocationInfo);
                    continue;
                }

                // 4. 按区县匹配
                log.info("开始四级匹配：区县：{}", newLocationInfo.getDistrict());
                List<LocationPojo> districtCandidates = filterByField(
                        newLocationInfo.getDistrict(), cityCandidates, LocationPojo::getDistrict);
                logMatchingProcess(newLocationName, districtCandidates, "区县匹配");

                if (districtCandidates.isEmpty()) {
                    addNewLocation(diaryId, newLocationName, newLocationInfo);
                    continue;
                }

                // 5. 按地点名称精确匹配
                log.info("开始五级匹配：地点名称：{}", newLocationName);
                List<LocationPojo> nameCandidates = findExactMatch(newLocationName, districtCandidates);
                logMatchingProcess(newLocationName, nameCandidates, "名称匹配");

                // 处理匹配结果
                handleMatchResult(diaryId, newLocationName, newLocationInfo, nameCandidates);

            } catch (Exception e) {
                log.error("处理地点去重时发生异常，地点名称: {}", newLocationName, e);
                // 异常情况下按新地点处理，避免丢失数据
                addNewLocation(diaryId, newLocationName, newLocationInfo);
            }
        }
    }

    /**
     * 根据国家查找地点
     */
    private List<LocationPojo> findByCountry(String country) {
        if (country == null) {
            return Collections.emptyList();
        }
        List<LocationPojo> result = locationMapper.selectByCountry(country);
        return result != null ? result : Collections.emptyList();
    }

    /**
     * 根据指定字段筛选候选地点列表
     */
    private List<LocationPojo> filterByField(String fieldValue,
                                             List<LocationPojo> candidates,
                                             Function<LocationPojo, String> fieldExtractor) {
        if (fieldValue == null || fieldValue.isEmpty() || candidates == null || candidates.isEmpty()) {
            return candidates != null ? candidates : Collections.emptyList();
        }

        return candidates.stream()
                .filter(loc -> fieldValue.equals(fieldExtractor.apply(loc)))
                .collect(Collectors.toList());
    }

    /**
     * 根据地点名称精确匹配
     */
    private List<LocationPojo> findExactMatch(String locationName, List<LocationPojo> candidates) {
        if (locationName == null || locationName.isEmpty() || candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }

        return candidates.stream()
                .filter(loc -> locationName.equals(loc.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 处理名称匹配后的结果
     */
    private void handleMatchResult(Integer diaryId,
                                   String newName,
                                   AITagJson.LocationInfo newInfo,
                                   List<LocationPojo> candidates) {

        if (candidates.isEmpty()) {
            log.info("无精确匹配地点，判定为新地点: {}", newName);
            addNewLocation(diaryId, newName, newInfo);
            return;
        }

        if (candidates.size() == 1) {
            LocationPojo matchedLocation = candidates.getFirst();
            log.info("候选列表只剩下一处地点，判定为同一地点，开始更新数据库：{}", matchedLocation.getName());
            updateAndReturn(diaryId, newName, newInfo, matchedLocation);
            return;
        }

        // 多候选，使用AI匹配
        handleAiMatching(diaryId, newName, newInfo, candidates);
    }

    /**
     * AI匹配处理
     */
    private void handleAiMatching(Integer diaryId,
                                  String newName,
                                  AITagJson.LocationInfo newInfo,
                                  List<LocationPojo> candidates) {

        log.info("开始六级匹配：AI 地点名称：{}", newName);
        Integer aiMatchIndex = aiAnalysisService.locationTagDeduplicationAnalysis(
                newName, newInfo, candidates);

        // 根据原逻辑处理AI返回结果
        if (aiMatchIndex == null) {
            log.warn("AI返回null，使用保守策略：视为新地点");
            addNewLocation(diaryId, newName, newInfo);
            return;
        }

        if (aiMatchIndex == -1) {
            log.info("AI未启用，按原逻辑处理：将候选列表中的所有地点插入数据库");
            // 注意：原代码此处逻辑可能有问题，但为保持行为一致，保留原样
            for (LocationPojo location : candidates) {
                locationMapper.insert(location);
                createDiaryLocationRelation(diaryId, location.getId(),
                        DiaryLocationPojo.MentionType.valueOf(newInfo.getMentionType().name()));
            }
            return;
        }

        if (aiMatchIndex == 0) {
            log.info("AI评定为新地点，添加数据库：{}", newName);
            addNewLocation(diaryId, newName, newInfo);
            return;
        }

        // aiMatchIndex > 0 且小于candidates.size()
        if (aiMatchIndex > 0 && aiMatchIndex <= candidates.size()) {
            LocationPojo matchedLocation = candidates.get(aiMatchIndex - 1); // 假设索引从1开始
            log.info("AI匹配成功，匹配到地点: {}", matchedLocation.getName());
            updateAndReturn(diaryId, newName, newInfo, matchedLocation);
        } else {
            log.warn("AI返回索引超出范围: {}，视为新地点", aiMatchIndex);
            addNewLocation(diaryId, newName, newInfo);
        }
    }

    /**
     * 更新已有地点信息并建立关联
     */
    private void updateAndReturn(Integer diaryId,
                                 String newName,
                                 AITagJson.LocationInfo newInfo,
                                 LocationPojo existingLocation) {

        locationUpdater(existingLocation, newName, newInfo);
        createDiaryLocationRelation(diaryId, existingLocation.getId(),
                DiaryLocationPojo.MentionType.valueOf(newInfo.getMentionType().name()));
    }

    /**
     * 添加新地点
     */
    private void addNewLocation(Integer diaryId, String name, AITagJson.LocationInfo info) {

        log.info("判定为新地点：{}", name);
        LocationPojo location = new LocationPojo(name, info);

        log.info("更新经纬度与geohash: {}", name);
        enrichLocationsWithCoordinates(location);

        log.info("插入数据库：{}", name);
        locationMapper.insert(location);
        createDiaryLocationRelation(diaryId, location.getId(),
                DiaryLocationPojo.MentionType.valueOf(info.getMentionType().name()));
    }

    /**
     * 更新地点的经纬度和 geohash
     * @param location 待处理的地点（直接修改原对象）
     */
    public void enrichLocationsWithCoordinates(LocationPojo location) {
        if (location == null) {
            log.warn("地点为空，无需处理");
            return;
        }

        try {
            // 1. 构建完整地址字符串（按国家/省/市/县/名称顺序组合）
            Map<String, String> addressResult = buildFullAddress(location);
            String fullAddress = addressResult.get("address");
            
            if (fullAddress == null || fullAddress.trim().isEmpty()) {
                log.warn("地点 [{}] 的地址信息不完整，跳过", location.getName());
                return;
            }

            // 2. 调用百度地图 API 获取经纬度
            GeoPoint geoPoint = geocodeAddress(addressResult);
            if (geoPoint == null) {
                log.warn("地点 [{}] 地理编码失败，地址：{}", location.getName(), addressResult.get("address"));
                return;
            }

            // 3. 计算 geohash（精度取 6 位，约 ±0.6km，可根据需要调整）
            String geohash = GeoHash.geoHashStringWithCharacterPrecision(
                    geoPoint.latitude(), geoPoint.longitude(), 6);

            // 4. 更新实体对象
            location.setLongitude(geoPoint.longitude());
            location.setLatitude(geoPoint.latitude());
            location.setGeoHash(geohash);

            log.info("地点 [{}] 更新成功：经纬度({}, {}), geohash={}",
                    location.getName(), geoPoint.longitude(), geoPoint.latitude(), geohash);

        } catch (Exception e) {
            log.error("处理地点 [{}] 时发生异常", location.getName(), e);
        }
    }

    /**
     * 构建完整的地址字符串
     * 格式：国家 省份 城市 区县 地点名称（如果某字段为空则跳过）
     * 
     * @param location 地点信息对象
     * @return 包含城市和完整地址的映射，键为"city"和"address"
     *         如果所有地址部分都为空，address 值为 null
     */
    private Map<String, String> buildFullAddress(LocationPojo location) {
        List<String> parts = new ArrayList<>();
        if (location.getCountry() != null && !location.getCountry().trim().isEmpty()) {
            parts.add(location.getCountry().trim());
        }
        if (location.getProvince() != null && !location.getProvince().trim().isEmpty()) {
            parts.add(location.getProvince().trim());
        }
        if (location.getCity() != null && !location.getCity().trim().isEmpty()) {
            parts.add(location.getCity().trim());
        }
        if (location.getDistrict() != null && !location.getDistrict().trim().isEmpty()) {
            parts.add(location.getDistrict().trim());
        }
        if (location.getName() != null && !location.getName().trim().isEmpty()) {
            parts.add(location.getName().trim());
        }
        
        // 构建返回结果，确保 city 不为 null（使用空字符串代替）
        Map<String, String> result = new HashMap<>();
        result.put("city", location.getCity() != null ? location.getCity().trim() : "");
        result.put("address", parts.isEmpty() ? null : String.join(" ", parts));
        return result;
    }

    /**
     * 调用百度地图 API 获取经纬度
     * 通过百度地图地理编码服务，将地址信息转换为精确的经纬度坐标
     *
     * <p>处理流程:</p>
     * <ol>
     *     <li>从用户配置中获取百度地图 API 密钥和主机地址</li>
     *     <li>对地址进行 URL 编码</li>
     *     <li>构建完整的请求 URL</li>
     *     <li>使用 HttpClient 发送 GET 请求</li>
     *     <li>解析返回的 JSON 响应，提取候选地点列表</li>
     *     <li>获取最佳匹配结果的经纬度坐标</li>
     * </ol>
     *
     * <p>注意事项:</p>
     * <ul>
     *     <li>使用 fullAddress.remove("city") 会修改原 Map 对象，移除 city 键值对</li>
     *     <li>只返回第一个候选地点（最佳匹配）的坐标</li>
     *     <li>如果 API 调用失败或无匹配结果，返回 null</li>
     * </ul>
     *
     * @param fullAddress 包含地址信息的映射，必须包含以下键：
     *                    - "address": 完整的地址字符串（必填）
     *                    - "city": 城市名称（用于辅助定位，会被移除）
     * @return GeoPoint 对象，包含经度（longitude）和纬度（latitude）
     *         如果 API 调用失败、解析错误或无匹配结果，则返回 null
     */
    private GeoPoint geocodeAddress(Map<String, String> fullAddress) {
        // 从用户配置中获取百度地图 API 密钥和主机地址
        String baiduMapApiKey = userSettingsService.getLatestApiConfig().getBaiduMapApiKey();
        String baiduMapApiHost = userSettingsService.getLatestApiConfig().getBaiduMapApiHost();
        
        // 获取地址和城市信息
        String address = fullAddress.get("address");
        String city = fullAddress.remove("city"); // 注意：remove 操作会修改原 Map 对象

        // 对地址进行 URL 编码
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        
        // 构建完整的请求 URL
        String url = baiduMapApiHost
                + "/geocoding/v3/"
                + "?address=" + encodedAddress
                + "&ak=" + baiduMapApiKey
                + "&ret_coordtype=" + "gcj02ll"
                + "&output=json";

        // 如果有城市信息，添加到请求中
        if (city != null && !city.trim().isEmpty()) {
            url += "&city=" + URLEncoder.encode(city, StandardCharsets.UTF_8);
        }
        
        // 创建 HttpGet 请求
        HttpGet httpGet = new HttpGet(url);
        
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            // 执行 HTTP 请求
            try (ClassicHttpResponse response = httpClient.executeOpen(null, httpGet, null)) {
                // 检查 HTTP 状态码
                int statusCode = response.getCode();
                if (statusCode < 200 || statusCode >= 300) {
                    log.error("百度地图 API 返回错误状态码：{}, 地址：{}", statusCode, address);
                    return null;
                }
                
                // 获取响应结果
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    log.error("百度地图 API 返回空响应，地址：{}", address);
                    return null;
                }

                // 获取响应内容
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                
                // 解析 JSON 响应
                JsonNode root = objectMapper.readTree(result);
                
                // 检查 API 调用是否成功
                JsonNode statusNode = root.path("status");
                if (!statusNode.isInt() || statusNode.asInt() != 0) {
                    String message = root.path("message").asText("未知错误");
                    log.error("百度地图 API 调用失败，状态码：{}, 消息：{}, 地址：{}", 
                            statusNode.asText("未知"), message, address);
                    return null;
                }
                
                // 提取结果中的位置信息
                JsonNode resultNode = root.path("result");
                if (resultNode.isMissingNode() || resultNode.isNull()) {
                    log.debug("百度地图 API 返回结果为空，地址：{}", address);
                    return null;
                }
                
                JsonNode locationNode = resultNode.path("location");
                if (locationNode.isMissingNode() || locationNode.isNull()) {
                    log.debug("百度地图 API 返回结果中没有位置信息，地址：{}", address);
                    return null;
                }
                
                // 提取经度和纬度（百度坐标系）
                double lon = locationNode.path("lng").asDouble();
                double lat = locationNode.path("lat").asDouble();
                
                log.info("百度地图地理编码成功，地址：{}, 坐标：({}, {})", address, lat, lon);
                
                return new GeoPoint(lon, lat);
            }
        } catch (Exception e) {
            log.error("调用百度地图 API 失败，地址：{}", fullAddress.get("address"), e);
            return null;
        }
    }

    /**
     * 更新地点信息
     */
    private void locationUpdater(LocationPojo location,
                                 String newLocationName,
                                 AITagJson.LocationInfo newLocationInfo) {

        // 更新别名
        updateField(location::getAlias, location::setAlias,
                newLocationName, "别名");
        // 更新描述
        updateField(location::getDescription, location::setDescription,
                newLocationInfo.getDescription(), "描述");

        // 更新最后出现时间
        location.setLastAppearance(DateTimeUtils.now());

        // 更新出现次数
        int currentCount = location.getAppearanceCount() != null ? location.getAppearanceCount() : 0;
        location.setAppearanceCount(currentCount + 1);

        locationMapper.update(location);
    }

    /**
     * 通用字段更新方法
     * 处理支持多值的字符串字段的更新逻辑
     *
     * <p>处理逻辑:</p>
     * <ol>
     *     <li>检查新值：如果为 null，则跳过更新</li>
     *     <li>检查当前值：如果为 null，直接设置新值</li>
     *     <li>如果当前值不为 null，将其解析为列表（使用 stringToList）</li>
     *     <li>检查新值是否已存在于列表中</li>
     *         <ul>
     *             <li>如果不存在，添加到列表并使用 listToString 转回字符串</li>
     *             <li>如果已存在，不做任何操作（避免重复）</li>
     *         </ul>
     * </ol>
     *
     * <p>设计优势:</p>
     * <ul>
     *     <li>使用函数式接口（Supplier 和 Consumer），使方法具有通用性，可处理各种字段</li>
     *     <li>统一的去重逻辑，避免相同值重复添加</li>
     *     <li>支持多值存储，字段值以逗号分隔的字符串形式保存</li>
     *     <li>灵活的空值处理策略，通过 allowEmpty 参数控制</li>
     * </ul>
     *
     * <p>使用示例:</p>
     * <pre>{@code
     * // 更新别名
     * updateField(person::getAlias, person::setAlias, newPersonName, "别名", false);
     * }</pre>
     *
     * @param getter 取值函数，用于获取字段的当前值
     * @param setter 设值函数，用于设置字段的新值
     * @param newValue 新值，待添加到字段中的值
     * @param fieldName 字段名称，用于日志记录
     */
    private void updateField(java.util.function.Supplier<String> getter,
                             java.util.function.Consumer<String> setter,
                             String newValue, String fieldName) {

        if (newValue == null || (newValue.trim().isEmpty())) {
            return;
        }

        String currentValue = getter.get();
        if (currentValue == null) {
            setter.accept(newValue);
            log.debug("设置{}: {}", fieldName, newValue);
            return;
        }

        List<String> values = stringToList(currentValue);
        boolean exists = values.stream()
                .anyMatch(value -> value.equals(newValue));

        if (!exists) {
            values.add(newValue);
            setter.accept(listToString(values));
            log.debug("添加新{}: {}", fieldName, newValue);
        }
    }

    /**
     * 创建日记-地点关联关系
     */
    private void createDiaryLocationRelation(Integer diaryId,
                                             Integer locationId,
                                             DiaryLocationPojo.MentionType mentionType) {
        try {
            if (diaryLocationMapper.selectByDiaryIdAndLocationId(diaryId, locationId) == null) {
                DiaryLocationPojo relation = new DiaryLocationPojo(diaryId, locationId);
                relation.setMentionType(mentionType);
                diaryLocationMapper.insert(relation);
                log.debug("创建日记-地点关联: diaryId={}, locationId={}", diaryId, locationId);
            }
        } catch (Exception e) {
            log.error("创建日记-地点关联失败，diaryId={}, locationId={}", diaryId, locationId, e);
        }
    }

    /**
     * 记录匹配过程日志
     */
    private void logMatchingProcess(String name, List<LocationPojo> candidates, String stage) {
        if (log.isDebugEnabled()) {
            int count = candidates != null ? candidates.size() : 0;
            String names = candidates != null && !candidates.isEmpty()
                    ? candidates.stream().map(LocationPojo::getName).collect(Collectors.joining(", "))
                    : "无";
            log.debug("{} - {}: 候选人数量={}, 列表=[{}]", name, stage, count, names);
        }
    }

    /**
     * 简单的经纬度内部类
     */
    private record GeoPoint(double longitude, double latitude) {

    }
}