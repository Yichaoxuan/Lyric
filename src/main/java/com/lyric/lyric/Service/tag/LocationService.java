package com.lyric.lyric.Service.tag;

import com.lyric.lyric.Mapper.relation.DiaryLocationMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 地点标签处理服务类
 *
 * @author Yichaoxuan
 * @since 2026-02-01
 */
@Slf4j
@Service
public class LocationService {

    private final LocationMapper locationMapper;
    private final DiaryLocationMapper diaryLocationMapper;
    private final AIAnalysisService aiAnalysisService;

    public LocationService(LocationMapper locationMapper,
                           DiaryLocationMapper diaryLocationMapper,
                           AIAnalysisService aiAnalysisService) {
        this.locationMapper = locationMapper;
        this.diaryLocationMapper = diaryLocationMapper;
        this.aiAnalysisService = aiAnalysisService;
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
            LocationPojo matchedLocation = candidates.get(0);
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
    private void addNewLocation(Integer diaryId,
                                String name,
                                AITagJson.LocationInfo info) {

        log.info("判定为新地点，添加数据库：{}", name);
        LocationPojo location = new LocationPojo(name, info);
        locationMapper.insert(location);
        createDiaryLocationRelation(diaryId, location.getId(),
                DiaryLocationPojo.MentionType.valueOf(info.getMentionType().name()));
    }

    /**
     * 更新地点信息
     */
    private void locationUpdater(LocationPojo location,
                                 String newLocationName,
                                 AITagJson.LocationInfo newLocationInfo) {

        // 更新描述
        if (newLocationInfo.getDescription() != null && !newLocationInfo.getDescription().isEmpty()) {
            location.setDescription(newLocationInfo.getDescription());
        }

        // 更新颜色
        if (newLocationInfo.getColor() != null && !newLocationInfo.getColor().isEmpty()) {
            location.setColor(newLocationInfo.getColor());
        }

        // 更新出现次数
        int currentCount = location.getAppearanceCount() != null ? location.getAppearanceCount() : 0;
        location.setAppearanceCount(currentCount + 1);

        locationMapper.update(location);
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
}