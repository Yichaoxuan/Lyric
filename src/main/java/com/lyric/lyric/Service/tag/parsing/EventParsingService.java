package com.lyric.lyric.Service.tag.parsing;

import com.lyric.lyric.Mapper.relation.*;
import com.lyric.lyric.Mapper.tag.entity.ActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.AI.EventDeduplicationData;
import com.lyric.lyric.POJO.AI.EventDeduplicationResult;
import com.lyric.lyric.POJO.relation.ActivityLocationPojo;
import com.lyric.lyric.POJO.relation.ActivityPersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.EventPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件活动标签解析服务
 *
 * @author Yichaoxuan
 * @since 2026/04/02
 */
@Slf4j
@Service
public class EventParsingService {

    // 默认事件标签颜色
    private static final String DEFAULT_EVENT_COLOR = "#FFA500";

    private final EventMapper eventMapper;
    private final ActivityMapper activityMapper;
    private final ActivityPersonMapper activityPersonMapper;
    private final ActivityLocationMapper activityLocationMapper;
    private final PersonMapper personMapper;
    private final LocationMapper locationMapper;
    private final AIAnalysisService aiAnalysisService;
    private final DiaryActivityMapper diaryActivityMapper;

    public EventParsingService(EventMapper eventMapper, ActivityMapper activityMapper, ActivityPersonMapper activityPersonMapper,
                               ActivityLocationMapper activityLocationMapper, PersonMapper personMapper, LocationMapper locationMapper, AIAnalysisService aiAnalysisService, DiaryActivityMapper diaryActivityMapper) {
        this.eventMapper = eventMapper;
        this.activityMapper = activityMapper;
        this.activityPersonMapper = activityPersonMapper;
        this.activityLocationMapper = activityLocationMapper;
        this.personMapper = personMapper;
        this.locationMapper = locationMapper;
        this.aiAnalysisService = aiAnalysisService;
        this.diaryActivityMapper = diaryActivityMapper;
    }


    /**
     * 处理活动标签解析
     * <p>
     * 主要功能：
     * 1. 保存活动信息到数据库
     * 2. 处理活动与日记、人物、地点的关联关系
     * 3. 自动创建或检查事件是否存在
     * </p>
     *
     * @param diaryId 日记 ID，用于关联活动和日记
     * @param activityInfoMap 活动信息 Map，Key 为活动名称，Value 为活动详细信息
     * @param personIdIndexMap 人物 ID 和索引的映射关系，Key 为人物 ID，Value 为人物索引
     * @param locationIdIndexMap 地点 ID 和索引的映射关系，Key 为地点 ID，Value 为地点索引
     */
    @Transactional(rollbackFor = Exception.class)
    public void processActivities(Integer diaryId, Map<String, AITagJson.ActivityInfo> activityInfoMap,
            Map<Integer, Integer> personIdIndexMap, Map<Integer, Integer> locationIdIndexMap) {

        log.info("处理活动标签解析");

        // 遍历活动信息
        for (Map.Entry<String, AITagJson.ActivityInfo> entry : activityInfoMap.entrySet()) {
            // 检查活动信息是否为空
            if (entry.getValue() == null) {
                log.warn("活动{}为空，跳过", entry.getKey());
                continue;
            }

            // 创建活动并关联关系
            ActivityPojo activity = createActivityWithRelations(diaryId, entry.getKey(), entry.getValue(), personIdIndexMap, locationIdIndexMap);


            // 进行事件去重
            EventDeduplicationResult eventDeduplication = checkOrCreateEvent(activity);

            if (eventDeduplication == null) {
                createEventFromActivities(activity);
            } else if (eventDeduplication.getEventId() == null || eventDeduplication.getEventId() == -1) {
                createEventFromActivities(activity);
            } else {
                updateActivity(activity.getId(), eventDeduplication.getEventId());
                updateEvent(eventDeduplication, activity);
            }
        }
    }

    /**
     * AI事件去重
     * <p>
     * 将活动消息与已存在的事件活动进行匹配，并返回匹配的或新创建的事件ID。
     * </p>
     *
     * @param newActivityPojo 新活动实体类
     * @return 匹配的事件ID与对应的事件描述；如果没有匹配则返回 null
     */
    private EventDeduplicationResult checkOrCreateEvent(ActivityPojo newActivityPojo) {
        log.info("进行AI事件去重");

        // 获取所有现有事件
        List<EventPojo> events = eventMapper.selectAllEvents();
        if (events == null || events.isEmpty()) {
            log.info("没有已存在的事件，判定为新事件");
            return null;
        }

        // 构建候选事件列表
        List<EventDeduplicationData.CandidateEventInfo> candidateEvents = buildCandidateEvents(events);

        // 构建新活动信息
        EventDeduplicationData.NewActivityInfo newActivity = buildNewActivityInfo(newActivityPojo);

        // 创建完整的 EventDeduplicationData 对象
        EventDeduplicationData eventDeduplicationData = new EventDeduplicationData(newActivity, candidateEvents);

        log.info("构建事件去重数据：新活动={}, 候选事件数={}", newActivity.getName(), candidateEvents.size());

        // 调用 AI 进行事件匹配
        return aiAnalysisService.eventTagDeduplicationAnalysis(eventDeduplicationData);
    }

    /**
     * 构建候选事件列表
     *
     * @param events 现有事件列表
     * @return 候选事件信息列表
     */
    private List<EventDeduplicationData.CandidateEventInfo> buildCandidateEvents(List<EventPojo> events) {
        return events.stream().map(event -> {
            List<ActivityPojo> activityList = activityMapper.selectByEventId(event.getId());
            List<EventDeduplicationData.ActivityWithRelations> activitiesWithRelations = activityList.stream()
                    .map(activity -> new EventDeduplicationData.ActivityWithRelations(
                            activity.getId(),
                            activity.getName(),
                            activity.getDescription(),
                            getPersonRelations(activity.getId()),
                            getLocationRelations(activity.getId())
                    ))
                    .collect(Collectors.toList());

            return new EventDeduplicationData.CandidateEventInfo(
                    event.getId(),
                    event.getName(),
                    event.getStartDate(),
                    event.getEndDate(),
                    event.getDescription(),
                    activitiesWithRelations
            );
        }).collect(Collectors.toList());
    }

    /**
     * 构建新活动信息
     *
     * @param activity 活动对象
     * @return 新活动信息
     */
    private EventDeduplicationData.NewActivityInfo buildNewActivityInfo(ActivityPojo activity) {
        return new EventDeduplicationData.NewActivityInfo(
                activity.getName(),
                DateTimeUtils.format(activity.getActivityDate()),
                activity.getTimePeriod().toString(),
                activity.getDescription(),
                getPersonRelations(activity.getId()),
                getLocationRelations(activity.getId())
        );
    }

    /**
     * 根据活动ID查询关联的人物关系
     *
     * @param activityId 活动ID
     * @return 人物关系列表
     */
    private List<EventDeduplicationData.PersonRelation> getPersonRelations(Integer activityId) {
        List<ActivityPersonPojo> activityPersons = activityPersonMapper.selectByActivityId(activityId);
        return activityPersons.stream()
                .filter(ap -> ap.getMentionType() == AITagJson.MentionType.ACTUAL)
                .map(ap -> new EventDeduplicationData.PersonRelation(ap.getPersonId(), ap.getRole()))
                .collect(Collectors.toList());
    }

    /**
     * 根据活动ID查询关联的地点关系
     *
     * @param activityId 活动ID
     * @return 地点关系列表
     */
    private List<EventDeduplicationData.LocationRelation> getLocationRelations(Integer activityId) {
        List<ActivityLocationPojo> activityLocationList = activityLocationMapper.selectByActivityId(activityId);
        return activityLocationList.stream()
                .filter(al -> al.getMentionType() == AITagJson.MentionType.ACTUAL)
                .map(al -> new EventDeduplicationData.LocationRelation(al.getLocationId()))
                .collect(Collectors.toList());
    }

    /**
     * 为活动创建事件对象并关联
     *
     * @param activity 活动对象
     */
    private void createEventFromActivities(ActivityPojo activity) {
        log.info("创建活动：activityName={}", activity.getName());

        LocalDate localDate = DateTimeUtils.toLocalDate(activity.getActivityDate());

        // 创建事件对象
        EventPojo newEvent = new EventPojo(
                activity.getName(),
                localDate,
                localDate,
                activity.getDescription(),
                EventPojo.ImportanceLevel.MEDIUM,
                DEFAULT_EVENT_COLOR
        );

        eventMapper.insert(newEvent);
        activity.setEventId(newEvent.getId());
        activityMapper.update(activity);

        log.info("事件创建成功：eventId={}, activityId={}", newEvent.getId(), activity.getId());
    }

    /**
     * 更新活动的事件关联
     *
     * @param activityId 活动ID
     * @param eventId 事件ID
     */
    private void updateActivity(Integer activityId, Integer eventId) {
        ActivityPojo activity = activityMapper.selectById(activityId);
        if (activity == null) {
            log.error("活动不存在，无法更新：activityId={}", activityId);
            return;
        }

        activity.setEventId(eventId);
        activityMapper.update(activity);
        log.debug("活动事件关联更新成功：activityId={}, eventId={}", activityId, eventId);
    }

    /**
     * 更新事件信息
     *
     * @param deduplicationResult 事件去重结果
     * @param newActivity 新活动
     */
    private void updateEvent(EventDeduplicationResult deduplicationResult, ActivityPojo newActivity) {
        log.info("更新事件：eventId={}", deduplicationResult.getEventId());

        EventPojo event = eventMapper.selectEventById(deduplicationResult.getEventId());
        if (event == null) {
            log.error("事件不存在，无法更新：eventId={}", deduplicationResult.getEventId());
            return;
        }

        // 更新事件名称，描述，结束日期
        event.setName(deduplicationResult.getUpdatedName());
        event.setDescription(deduplicationResult.getUpdatedDescription());
        event.setEndDate(DateTimeUtils.toLocalDate(newActivity.getActivityDate()));
        eventMapper.updateEvent(event);

        log.info("事件更新成功：eventId={}", event.getId());
    }

    /**
     * 创建活动及其关联关系
     * <p>
     * 创建的关系如下：
     * 1. 创建活动与日记的关联关系
     * 2. 创建活动与人物的关联关系
     * 3. 创建活动与地点的关联关系
     * </p>
     *
     * @param diaryId 日记 ID
     * @param activityName 活动名称
     * @param activityInfo 活动详细信息
     * @param personIdIndexMap 人物 ID 和索引的映射关系
     * @param locationIdIndexMap 地点 ID 和索引的映射关系
     */
    private ActivityPojo createActivityWithRelations(Integer diaryId, String activityName, AITagJson.ActivityInfo activityInfo,
            Map<Integer, Integer> personIdIndexMap, Map<Integer, Integer> locationIdIndexMap) {

        ActivityPojo activity = new ActivityPojo(activityName, activityInfo);
        activityMapper.insert(activity);
        log.info("创建活动成功: {}", activityName);
        diaryActivityMapper.insert(diaryId, activity.getId());
        createActivityPersonRelations(activity.getId(), activityInfo, personIdIndexMap);
        createActivityLocationRelations(activity.getId(), activityInfo, locationIdIndexMap);
        return activity;
    }

    /**
     * 创建活动与人物的关联关系
     * <p>
     * 遍历活动信息中的所有人物，根据人物索引查找对应的人物 ID，
     * 然后创建活动 - 人物关联记录，包含角色和提及类型信息。
     * </p>
     *
     * @param activityId 活动 ID
     * @param activityInfo 活动详细信息，包含人物列表
     * @param personIdIndexMap 人物 ID 和索引的映射关系
     */
    private void createActivityPersonRelations(Integer activityId, AITagJson.ActivityInfo activityInfo,
            Map<Integer, Integer> personIdIndexMap) {
        if (activityInfo.getPersons() == null || personIdIndexMap == null) {
            return;
        }
        for (Map.Entry<String, AITagJson.ActivityInfo.PersonRole> entry : activityInfo.getPersons().entrySet()) {
            String personIndex = entry.getKey();
            AITagJson.ActivityInfo.PersonRole personRole = entry.getValue();
            Integer personId = findIdByIndex(personIdIndexMap, personIndex);
            if (personId == null) {
                log.warn("未找到人物索引{}对应的ID", personIndex);
                continue;
            }
            PersonPojo person = personMapper.selectById(personId);
            if (person == null || person.getId() == null) {
                log.warn("人物ID{}不存在", personId);
                continue;
            }
            ActivityPersonPojo relation = new ActivityPersonPojo(
                    activityId, personId, personRole.getRole(), personRole.getMentionType());
            activityPersonMapper.insert(relation);
            log.info("创建活动-人物关联成功: 活动ID={}, 人物ID={}, 角色={}", activityId, personId, personRole.getRole());
        }
    }

    /**
     * 创建活动与地点的关联关系
     * <p>
     * 遍历活动信息中的所有地点，根据地点索引查找对应的地点 ID，
     * 然后创建活动 - 地点关联记录，包含提及类型信息。
     * </p>
     *
     * @param activityId 活动 ID
     * @param activityInfo 活动详细信息，包含地点列表
     * @param locationIdIndexMap 地点 ID 和索引的映射关系
     */
    private void createActivityLocationRelations(Integer activityId, AITagJson.ActivityInfo activityInfo,
            Map<Integer, Integer> locationIdIndexMap) {
        if (activityInfo.getLocations() == null || locationIdIndexMap == null) {
            return;
        }
        for (Map.Entry<String, AITagJson.ActivityInfo.LocationMention> entry : activityInfo.getLocations().entrySet()) {
            String locationIndex = entry.getKey();
            AITagJson.ActivityInfo.LocationMention locationMention = entry.getValue();
            Integer locationId = findIdByIndex(locationIdIndexMap, locationIndex);
            if (locationId == null) {
                log.warn("未找到地点索引{}对应的ID", locationIndex);
                continue;
            }
            LocationPojo location = locationMapper.selectById(locationId);
            if (location == null || location.getId() == null) {
                log.warn("地点ID{}不存在", locationId);
                continue;
            }
            ActivityLocationPojo relation = new ActivityLocationPojo(
                    activityId, locationId, locationMention.getMentionType());
            activityLocationMapper.insert(relation);
            log.info("创建活动-地点关联成功: 活动ID={}, 地点ID={}", activityId, locationId);
        }
    }

    /**
     * 根据索引查找对应的 ID
     * <p>
     * 在 ID-索引映射表中查找指定索引对应的实体 ID。
     * 支持人物索引和地点索引的查找。
     * </p>
     *
     * @param idIndexMap ID 和索引的映射关系，Key 为实体 ID，Value 为索引
     * @param indexStr 要查找的索引（字符串形式）
     * @return 找到的实体 ID；如果未找到或索引格式错误则返回 null
     */
    private Integer findIdByIndex(Map<Integer, Integer> idIndexMap, String indexStr) {
        if (idIndexMap == null || indexStr == null) {
            return null;
        }
        try {
            int index = Integer.parseInt(indexStr);
            for (Map.Entry<Integer, Integer> entry : idIndexMap.entrySet()) {
                if (entry.getValue().equals(index)) {
                    return entry.getKey();
                }
            }
        } catch (NumberFormatException e) {
            log.warn("索引格式错误: {}", indexStr);
        }
        return null;
    }
}
