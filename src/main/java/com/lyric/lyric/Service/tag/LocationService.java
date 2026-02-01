package com.lyric.lyric.Service.tag;

import com.lyric.lyric.Mapper.relation.DiaryLocationMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 地点标签处理服务类
 *
 * @author Yichaoxuan
 * @serial 2026/02/01
 */
@Slf4j
@Service
public class LocationService {

    private final LocationMapper locationMapper;

    private final DiaryLocationMapper diaryLocationMapper;

    private final AIAnalysisService aiAnalysisService;

    public LocationService(LocationMapper locationMapper, DiaryLocationMapper diaryLocationMapper, AIAnalysisService aiAnalysisService) {
        this.locationMapper = locationMapper;
        this.diaryLocationMapper = diaryLocationMapper;
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * 地点标签去重器
     * 通过多级匹配策略判断是否为同一地点，更新或插入数据库
     * 匹配顺序：国家匹配 -> 省份匹配 -> 城市匹配 -> 地点匹配
     *
     * @param diaryId 日记id
     * @param newLocationName 新地点名字
     * @param newLocationInfo 新的地点信息
     * @since 2026-02-04
     */
    public void locationDeduplication(Integer diaryId, String newLocationName, AITagJson.LocationInfo newLocationInfo) {

        // 一级匹配，匹配国家
        log.info("开始一级匹配：国家：{}", newLocationInfo.getCountry());
        List<LocationPojo> candidateLocations = locationMapper.selectByCountry(newLocationInfo.getCountry());
        log.info("一级匹配结束，候选地点列表：{}", candidateLocations);
        if(candidateLocations != null && !candidateLocations.isEmpty()) {

            // 二级匹配，匹配省份
            log.info("开始二级匹配：省份：{}", newLocationInfo.getProvince());
            candidateLocations = filterByField(newLocationInfo.getProvince(), candidateLocations, LocationPojo::getProvince);
            log.info("二级匹配结束，候选地点列表：{}", candidateLocations);
            if (!candidateLocations.isEmpty()) {

                // 三级匹配，匹配城市
                log.info("开始三级匹配：城市：{}", newLocationInfo.getCity());
                candidateLocations = filterByField(newLocationInfo.getCity(), candidateLocations, LocationPojo::getCity);
                log.info("三级匹配结束，候选地点列表：{}", candidateLocations);
                if (!candidateLocations.isEmpty()) {

                    // 四级匹配，匹配区县
                    log.info("开始四级匹配：区县：{}", newLocationInfo.getDistrict());
                    candidateLocations = filterByField(newLocationInfo.getDistrict(), candidateLocations, LocationPojo::getDistrict);
                    log.info("四级匹配结束，候选地点列表：{}", candidateLocations);
                    if (!candidateLocations.isEmpty()) {

                        // 五级匹配，匹配地点名称
                        log.info("开始五级匹配：地点名称：{}", newLocationName);
                        candidateLocations = findExactMatch(newLocationName, candidateLocations);
                        log.info("五级匹配结束，候选地点列表：{}", candidateLocations);
                        if (!candidateLocations.isEmpty()) {

                            // 如果只剩下一个候选地点，则更新数据库
                            if (candidateLocations.size() == 1) {
                                log.info("候选列表只剩下一处地点，判定为同一地点，开始更新数据库：{}", candidateLocations.getFirst());
                                // 为同一地点，更新地点信息
                                locationUpdater(candidateLocations.getFirst(), newLocationName, newLocationInfo);
                                // 添加日记-地点关联
                                checkConstraint(diaryId, candidateLocations.getFirst().getId(),
                                        DateTimeUtils.parseDate(newLocationInfo.getAppearanceDate()),
                                        DiaryLocationPojo.MentionType.valueOf(newLocationInfo.getMentionType().name()));
                                return;
                            }

                            // 如果候选列表大于1，则进行六级匹配
                            // 六级匹配 AI匹配
                            log.info("开始六级匹配：AI 地点名称：{}", newLocationName);
                            Integer matchTheLocationIndex = aiAnalysisService.locationTagDeduplicationAnalysis(
                                    newLocationName, newLocationInfo, candidateLocations);

                            if (matchTheLocationIndex == -1) {
                                log.info("未开启AI分析功能，默认为新地点，添加数据库");
                                for (LocationPojo location : candidateLocations) {
                                    locationMapper.insert(location);
                                }
                            } else if (matchTheLocationIndex == 0) {
                                // 判定为新地点
                                LocationPojo location = new LocationPojo(newLocationName, newLocationInfo);
                                log.info("AI评定为新地点，添加数据库：{}", location);

                                Integer locationId = locationMapper.insert(location);

                                // 添加日记-地点关联
                                checkConstraint(diaryId, locationId,
                                        DateTimeUtils.parseDate(newLocationInfo.getAppearanceDate()),
                                        DiaryLocationPojo.MentionType.valueOf(newLocationInfo.getMentionType().name()));
                            } else {
                                // 为同一地点，更新地点信息
                                LocationPojo locationPojo = candidateLocations.get(matchTheLocationIndex);
                                locationUpdater(locationPojo, newLocationName, newLocationInfo);

                                // 添加日记-地点关联
                                checkConstraint(diaryId, locationPojo.getId(),
                                        DateTimeUtils.parseDate(newLocationInfo.getAppearanceDate()),
                                        DiaryLocationPojo.MentionType.valueOf(newLocationInfo.getMentionType().name()));
                            }
                        }
                    }
                }
            }
        } else {
            // 未找到匹配的国家，创建新地点
            LocationPojo location = new LocationPojo(newLocationName, newLocationInfo);
            log.info("未找到匹配的国家，创建新地点：{}", location);

            Integer locationId = locationMapper.insert(location);

            // 添加日记-地点关联
            checkConstraint(diaryId, locationId,
                    DateTimeUtils.parseDate(newLocationInfo.getAppearanceDate()),
                    DiaryLocationPojo.MentionType.valueOf(newLocationInfo.getMentionType().name()));
        }
    }

    /**
     * 根据指定字段筛选候选地点列表
     * 通用的地点匹配方法，支持按省份、城市、区县等字段进行筛选
     *
     * @param fieldValue 字段值
     * @param candidates 候选地点列表
     * @param fieldExtractor 字段提取函数
     * @return 筛选后的候选地点列表
     * @since 2026-02-04
     */
    private List<LocationPojo> filterByField(String fieldValue, List<LocationPojo> candidates,
                                           java.util.function.Function<LocationPojo, String> fieldExtractor) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            return candidates;
        }

        List<LocationPojo> result = new ArrayList<>();
        for (LocationPojo location : candidates) {
            String locationFieldValue = fieldExtractor.apply(location);
            if (fieldValue.equals(locationFieldValue)) {
                result.add(location);
            }
        }
        return result;
    }

    /**
     * 根据地点名称精确匹配
     *
     * @param locationName 地点名称
     * @param candidates 候选地点列表
     * @return 匹配的地点列表
     * @since 2026-02-04
     */
    private List<LocationPojo> findExactMatch(String locationName, List<LocationPojo> candidates) {
        if (locationName == null || locationName.isEmpty()) {
            return new ArrayList<>();
        }

        List<LocationPojo> result = new ArrayList<>();
        for (LocationPojo location : candidates) {
            if (locationName.equals(location.getName())) {
                result.add(location);
            }
        }
        return result;
    }

    /**
     * 更新地点信息
     *
     * @param location 待更新的地点实体
     * @param newLocationName 新的地点名称
     * @param newLocationInfo 新的地点信息
     * @since 2026-02-04
     */
    private void locationUpdater(LocationPojo location, String newLocationName, AITagJson.LocationInfo newLocationInfo) {
        // 更新地点描述
        if (newLocationInfo.getDescription() != null && !newLocationInfo.getDescription().isEmpty()) {
            location.setDescription(newLocationInfo.getDescription());
        }

        // 更新地点颜色
        if (newLocationInfo.getColor() != null && !newLocationInfo.getColor().isEmpty()) {
            location.setColor(newLocationInfo.getColor());
        }

        // 更新出现次数
        Integer currentAppearanceCount = location.getAppearanceCount();
        if (currentAppearanceCount == null) {
            currentAppearanceCount = 0;
        }
        location.setAppearanceCount(currentAppearanceCount + 1);

        // 更新地点信息
        locationMapper.update(location);
    }

    /**
     * 检查是否已存在关联，避免违反唯一约束
     *
     * @param diaryId 日记ID
     * @param locationId 地点ID
     * @param appearanceDate 出现日期
     * @param mentionType 提及类型
     * @since 2026-02-04
     */
    private void checkConstraint(Integer diaryId, Integer locationId, LocalDate appearanceDate, DiaryLocationPojo.MentionType mentionType) {
        // 检查是否已存在该关联
        DiaryLocationPojo existingRelation = diaryLocationMapper.selectByDiaryIdAndLocationId(diaryId, locationId);
        if (existingRelation == null) {
            // 不存在则插入新关联
            DiaryLocationPojo diaryLocation = new DiaryLocationPojo(diaryId, locationId);
            diaryLocation.setAppearanceDate(appearanceDate.atStartOfDay());
            diaryLocation.setMentionType(mentionType);
            diaryLocationMapper.insert(diaryLocation);
        }
    }

}
