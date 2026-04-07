package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.POJO.tag.BaseTagPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.EventPojo;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签服务层，提供三种类型的标签创建，修改，删除，查询功能对外的唯一入口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class TagService {

    private final BaseTagService baseTagService;
    private final PersonTagService personTagService;
    private final LocationTagService locationTagService;
    private final EventTagService eventTagService;

    public TagService(BaseTagService baseTagService, PersonTagService personTagService,
            LocationTagService locationTagService, EventTagService eventTagService) {
        this.baseTagService = baseTagService;
        this.personTagService = personTagService;
        this.locationTagService = locationTagService;
        this.eventTagService = eventTagService;
    }

    // ==================== BaseTag 相关方法 ====================

    /**
     * 创建基本标签
     * 
     * @param baseTagPojo 标签实体对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createBaseTag(BaseTagPojo baseTagPojo) {
        log.info("创建基本标签：name={}, tagType={}", baseTagPojo.getName(), baseTagPojo.getTagType());

        // 检查标签名称是否为空
        if (baseTagPojo.getName() == null || baseTagPojo.getName().trim().isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_NAME_EMPTY);
        }

        baseTagService.createTag(baseTagPojo);
        return ResultBuilder.success(SuccessMsgEnums.BASE_TAG_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询基本标签
     * 
     * @param id 标签 ID
     * @return Result<BaseTagPojo> 包含标签实体对象
     */
    public Result<BaseTagPojo> getBaseTagById(Integer id) {
        log.debug("查询基本标签：id={}", id);
        BaseTagPojo tag = baseTagService.getTagById(id);

        if (tag == null) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.BASE_TAG_QUERY_SUCCESS, tag);
    }

    /**
     * 根据日记 ID 查询基本标签
     * 
     * @param diaryId 日记 ID
     * @return Result<List<BaseTagPojo>> 列表包含标签实体对象
     */
    public Result<List<BaseTagPojo>> getBaseTagsByDiaryId(Integer diaryId) {
        log.debug("查询基本标签：diaryId={}", diaryId);
        List<BaseTagPojo> tags = baseTagService.getTagsByDiaryId(diaryId);
        return ResultBuilder.successWithData(SuccessMsgEnums.BASE_TAG_QUERY_SUCCESS, tags);
    }

    /**
     * 查询所有基本标签
     * 
     * @return Result<List<BaseTagPojo>> 包含标签列表
     */
    public Result<List<BaseTagPojo>> getAllBaseTags() {
        log.info("查询所有基本标签");
        List<BaseTagPojo> tags = baseTagService.getAllTags();
        return ResultBuilder.successWithData(SuccessMsgEnums.BASE_TAG_QUERY_SUCCESS, tags);
    }

    /**
     * 根据类型查询基本标签
     * 
     * @param tagType 标签类型
     * @return Result<List<BaseTagPojo>> 包含标签列表
     */
    public Result<List<BaseTagPojo>> getBaseTagsByType(BaseTagPojo.TagType tagType) {
        log.info("按类型查询基本标签：tagType={}", tagType);
        List<BaseTagPojo> tags = baseTagService.getTagsByType(tagType);
        return ResultBuilder.successWithData(SuccessMsgEnums.BASE_TAG_QUERY_BY_TYPE_SUCCESS, tags);
    }

    /**
     * 更新基本标签
     * 
     * @param baseTagPojo 标签实体对象（必须包含 id）
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> updateBaseTag(BaseTagPojo baseTagPojo) {
        log.info("更新基本标签：id={}, name={}", baseTagPojo.getId(), baseTagPojo.getName());

        // 检查标签 ID 是否存在
        if (baseTagPojo.getId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_NOT_FOUND);
        }

        boolean success = baseTagService.updateTag(baseTagPojo);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.BASE_TAG_UPDATE_SUCCESS);
    }

    /**
     * 删除基本标签（级联删除关联表）
     * 
     * @param id 标签 ID
     * @return Result<Void> 表示是否删除成功
     */
    public Result<Void> deleteBaseTag(Integer id) {
        log.info("删除基本标签：id={}", id);

        boolean success = baseTagService.deleteTag(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_DELETE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.BASE_TAG_DELETE_SUCCESS);
    }

    /**
     * 增加基本标签使用次数
     * 
     * @param id 标签 ID
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> incrementBaseTagUsage(Integer id) {
        log.info("增加基本标签使用次数：id={}", id);

        boolean success = baseTagService.incrementUsageCount(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.BASE_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.BASE_TAG_USAGE_INCREMENT_SUCCESS);
    }

    // ==================== PersonTag 相关方法 ====================

    /**
     * 创建人物标签
     * 
     * @param personPojo 人物实体对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createPersonTag(PersonPojo personPojo) {
        log.info("创建人物标签：name={}, relation={}", personPojo.getName(), personPojo.getRelation());

        // 检查人物名称是否为空
        if (personPojo.getName() == null || personPojo.getName().trim().isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_NAME_EMPTY);
        }

        personTagService.createPerson(personPojo);
        return ResultBuilder.success(SuccessMsgEnums.PERSON_TAG_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询人物标签
     * 
     * @param id 人物 ID
     * @return Result<PersonPojo> 包含人物实体对象
     */
    public Result<PersonPojo> getPersonTagById(Integer id) {
        log.info("查询人物标签：id={}", id);
        PersonPojo person = personTagService.getPersonById(id);

        if (person == null) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_SUCCESS, person);
    }

    /**
     * 根据名称查询人物标签
     * 
     * @param name 人物名称
     * @return Result<PersonPojo> 包含人物实体对象
     */
    public Result<PersonPojo> getPersonTagByName(String name) {
        log.info("按名称查询人物标签：name={}", name);
        PersonPojo person = personTagService.getPersonByName(name);

        if (person == null) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_BY_NAME_SUCCESS, person);
    }

    /**
     * 查询所有人物标签
     * 
     * @return Result<List<PersonPojo>> 包含人物列表
     */
    public Result<List<PersonPojo>> getAllPersonTags() {
        log.info("查询所有人物标签");
        List<PersonPojo> persons = personTagService.getAllPersons();
        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_SUCCESS, persons);
    }

    /**
     * 根据性别查询人物标签
     * 
     * @param gender 性别（0:未知，1:男，2:女）
     * @return Result<List<PersonPojo>> 包含人物列表
     */
    public Result<List<PersonPojo>> getPersonTagsByGender(Integer gender) {
        log.info("按性别查询人物标签：gender={}", gender);
        List<PersonPojo> persons = personTagService.getPersonsByGender(gender);
        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_BY_GENDER_SUCCESS, persons);
    }

    /**
     * 根据关系查询人物标签
     * 
     * @param relation 关系关键词
     * @return Result<List<PersonPojo>> 包含人物列表
     */
    public Result<List<PersonPojo>> getPersonTagsByRelation(String relation) {
        log.info("按关系查询人物标签：relation={}", relation);
        List<PersonPojo> persons = personTagService.getPersonsByRelation(relation);
        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_BY_RELATION_SUCCESS, persons);
    }

    /**
     * 更新人物标签
     * 
     * @param personPojo 人物实体对象（必须包含 id）
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> updatePersonTag(PersonPojo personPojo) {
        log.info("更新人物标签：id={}, name={}", personPojo.getId(), personPojo.getName());

        // 检查人物 ID 是否存在
        if (personPojo.getId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_NOT_FOUND);
        }

        boolean success = personTagService.updatePerson(personPojo);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.PERSON_TAG_UPDATE_SUCCESS);
    }

    /**
     * 删除人物标签（级联删除关联表）
     * 
     * @param id 人物 ID
     * @return Result<Void> 表示是否删除成功
     */
    public Result<Void> deletePersonTag(Integer id) {
        log.info("删除人物标签：id={}", id);

        boolean success = personTagService.deletePerson(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_DELETE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.PERSON_TAG_DELETE_SUCCESS);
    }

    /**
     * 增加人物出现次数
     * 
     * @param id 人物 ID
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> incrementPersonAppearance(Integer id) {
        log.info("增加人物出现次数：id={}", id);

        boolean success = personTagService.incrementAppearanceCount(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.PERSON_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.PERSON_TAG_APPEARANCE_INCREMENT_SUCCESS);
    }

    /**
     * 根据日记ID 查询对应的人物标签列表
     * 
     * @param diaryId 日记 ID
     * @return Result<List<PersonPojo>> 包含人物列表
     */
    public Result<List<PersonPojo>> getPersonTagsByDiaryId(Integer diaryId) {
        log.debug("根据日记 ID 查询人物标签：diaryId={}", diaryId);
        List<PersonPojo> persons = personTagService.getPersonsByDiaryId(diaryId);
        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_SUCCESS, persons);
    }

    /**
     * 根据活动ID 查询对应的人物标签列表
     * 
     * @param activityId 活动 ID
     * @return Result<List<PersonPojo>> 包含人物列表
     */
    public Result<List<PersonPojo>> getPersonTagsByActivityId(Integer activityId) {
        log.debug("根据活动 ID 查询人物标签：activityId={}", activityId);
        List<PersonPojo> persons = personTagService.getPersonsByActivityId(activityId);
        return ResultBuilder.successWithData(SuccessMsgEnums.PERSON_TAG_QUERY_SUCCESS, persons);
    }

    // ==================== LocationTag 相关方法 ====================

    /**
     * 创建地点标签
     * 
     * @param locationPojo 地点实体对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createLocationTag(LocationPojo locationPojo) {
        log.info("创建地点标签：name={}, city={}, province={}", locationPojo.getName(),
                locationPojo.getCity(), locationPojo.getProvince());

        // 检查地点名称是否为空
        if (locationPojo.getName() == null || locationPojo.getName().trim().isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_NAME_EMPTY);
        }

        locationTagService.createLocation(locationPojo);
        return ResultBuilder.success(SuccessMsgEnums.LOCATION_TAG_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询地点标签
     * 
     * @param id 地点 ID
     * @return Result<LocationPojo> 包含地点实体对象
     */
    public Result<LocationPojo> getLocationTagById(Integer id) {
        log.info("查询地点标签：id={}", id);
        LocationPojo location = locationTagService.getLocationById(id);

        if (location == null) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_SUCCESS, location);
    }

    /**
     * 根据名称查询地点标签
     * 
     * @param name 地点名称
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByName(String name) {
        log.info("按名称查询地点标签：name={}", name);
        List<LocationPojo> locations = locationTagService.getLocationsByName(name);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_BY_NAME_SUCCESS, locations);
    }

    /**
     * 根据别名查询地点标签
     * 
     * @param alias 地点别名
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByAlias(String alias) {
        log.info("按别名查询地点标签：alias={}", alias);
        List<LocationPojo> locations = locationTagService.getLocationsByAlias(alias);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_BY_ALIAS_SUCCESS, locations);
    }

    /**
     * 根据城市查询地点标签
     * 
     * @param city 城市
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByCity(String city) {
        log.info("按城市查询地点标签：city={}", city);
        List<LocationPojo> locations = locationTagService.getLocationsByCity(city);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_BY_CITY_SUCCESS, locations);
    }

    /**
     * 根据省份查询地点标签
     * 
     * @param province 省份
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByProvince(String province) {
        log.info("按省份查询地点标签：province={}", province);
        List<LocationPojo> locations = locationTagService.getLocationsByProvince(province);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_BY_PROVINCE_SUCCESS, locations);
    }

    /**
     * 根据国家查询地点标签
     * 
     * @param country 国家
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByCountry(String country) {
        log.info("按国家查询地点标签：country={}", country);
        List<LocationPojo> locations = locationTagService.getLocationsByCountry(country);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_BY_COUNTRY_SUCCESS, locations);
    }

    /**
     * 查询所有地点标签
     * 
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getAllLocationTags() {
        log.info("查询所有地点标签");
        List<LocationPojo> locations = locationTagService.getAllLocations();
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_SUCCESS, locations);
    }

    /**
     * 更新地点标签
     * 
     * @param locationPojo 地点实体对象（必须包含 id）
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> updateLocationTag(LocationPojo locationPojo) {
        log.info("更新地点标签：id={}, name={}", locationPojo.getId(), locationPojo.getName());

        // 检查地点 ID 是否存在
        if (locationPojo.getId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_NOT_FOUND);
        }

        boolean success = locationTagService.updateLocation(locationPojo);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.LOCATION_TAG_UPDATE_SUCCESS);
    }

    /**
     * 删除地点标签（级联删除关联表）
     * 
     * @param id 地点 ID
     * @return Result<Void> 表示是否删除成功
     */
    public Result<Void> deleteLocationTag(Integer id) {
        log.info("删除地点标签：id={}", id);

        boolean success = locationTagService.deleteLocation(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_DELETE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.LOCATION_TAG_DELETE_SUCCESS);
    }

    /**
     * 增加地点出现次数
     * 
     * @param id 地点 ID
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> incrementLocationAppearance(Integer id) {
        log.info("增加地点出现次数：id={}", id);

        boolean success = locationTagService.incrementAppearanceCount(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.LOCATION_TAG_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.LOCATION_TAG_APPEARANCE_INCREMENT_SUCCESS);
    }

    /**
     * 根据日记ID 查询对应的地点标签列表
     * 
     * @param diaryId 日记ID
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByDiaryId(Integer diaryId) {
        log.debug("根据日记ID 查询地点标签：diaryId={}", diaryId);
        List<LocationPojo> locations = locationTagService.getLocationsByDiaryId(diaryId);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_SUCCESS, locations);
    }

    /**
     * 根据活动ID 查询对应的地点标签列表
     * 
     * @param activityId 活动 ID
     * @return Result<List<LocationPojo>> 包含地点列表
     */
    public Result<List<LocationPojo>> getLocationTagsByActivityId(Integer activityId) {
        log.debug("根据活动 ID 查询地点标签：activityId={}", activityId);
        List<LocationPojo> locations = locationTagService.getLocationsByActivityId(activityId);
        return ResultBuilder.successWithData(SuccessMsgEnums.LOCATION_TAG_QUERY_SUCCESS, locations);
    }

    // ==================== Event 相关方法 ====================

    /**
     * 创建事件
     * 
     * @param event 事件实体对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createEvent(EventPojo event) {
        log.info("创建事件：name={}, startDate={}, endDate={}",
                event.getName(), event.getStartDate(), event.getEndDate());

        // 检查事件名称是否为空
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.TOG_EVENT_NAME_EMPTY);
        }

        eventTagService.createEvent(event);
        return ResultBuilder.success(SuccessMsgEnums.TOG_EVENT_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询事件
     * 
     * @param id 事件 ID
     * @return Result<EventPojo> 包含事件实体对象
     */
    public Result<EventPojo> getEventById(Integer id) {
        log.info("查询事件：id={}", id);
        EventPojo event = eventTagService.getEventById(id);

        if (event == null) {
            throw new BusinessException(BusinessErrorMsgEnums.TOG_EVENT_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.TOG_EVENT_QUERY_SUCCESS, event);
    }

    /**
     * 查询所有事件
     * 
     * @return Result<List<EventPojo>> 包含事件列表
     */
    public Result<List<EventPojo>> getAllEvents() {
        log.info("查询所有事件");
        List<EventPojo> events = eventTagService.getAllEvents();
        return ResultBuilder.successWithData(SuccessMsgEnums.TOG_EVENT_QUERY_SUCCESS, events);
    }

    /**
     * 更新事件
     * 
     * @param event 事件实体对象（必须包含 id）
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> updateEvent(EventPojo event) {
        log.info("更新事件：id={}, name={}", event.getId(), event.getName());

        // 检查事件 ID 是否存在
        if (event.getId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.TOG_EVENT_NOT_FOUND);
        }

        boolean success = eventTagService.updateEvent(event);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.TOG_EVENT_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.TOG_EVENT_UPDATE_SUCCESS);
    }

    /**
     * 删除事件（级联删除所有活动及关联表）
     * 
     * @param id 事件 ID
     * @return Result<Void> 表示是否删除成功
     */
    public Result<Void> deleteEvent(Integer id) {
        log.info("删除事件：id={}", id);

        boolean success = eventTagService.deleteEvent(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.TOG_EVENT_DELETE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.TOG_EVENT_DELETE_SUCCESS);
    }

    // ==================== Activity 相关方法 ====================

    /**
     * 创建活动
     * 
     * @param activity 活动实体对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createActivity(ActivityPojo activity) {
        log.info("创建活动：name={}, eventId={}, activityDate={}",
                activity.getName(), activity.getEventId(), activity.getActivityDate());

        // 检查活动名称是否为空
        if (activity.getName() == null || activity.getName().trim().isEmpty()) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_NAME_EMPTY);
        }

        // 检查事件是否存在
        if (activity.getEventId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_TOG_EVENT_NOT_FOUND);
        }

        eventTagService.createActivity(activity);
        return ResultBuilder.success(SuccessMsgEnums.SUB_EVENT_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询活动
     * 
     * @param id 活动 ID
     * @return Result<ActivityPojo> 包含活动实体对象
     */
    public Result<ActivityPojo> getActivityById(Integer id) {
        log.info("查询活动：id={}", id);
        ActivityPojo activity = eventTagService.getActivityById(id);

        if (activity == null) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_NOT_FOUND);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.SUB_EVENT_QUERY_SUCCESS, activity);
    }

    /**
     * 根据事件 ID 查询活动
     * 
     * @param eventId 事件 ID
     * @return Result<List<ActivityPojo>> 包含活动列表
     */
    public Result<List<ActivityPojo>> getActivitiesByEventId(Integer eventId) {
        log.info("根据事件 ID 查询活动：eventId={}", eventId);
        List<ActivityPojo> activities = eventTagService.getActivitiesByEventId(eventId);
        return ResultBuilder.successWithData(SuccessMsgEnums.SUB_EVENT_QUERY_BY_TOG_EVENT_SUCCESS, activities);
    }

    /**
     * 查询所有活动
     * 
     * @return Result<List<ActivityPojo>> 包含活动列表
     */
    public Result<List<ActivityPojo>> getAllActivities() {
        log.info("查询所有活动");
        List<ActivityPojo> activities = eventTagService.getAllActivities();
        return ResultBuilder.successWithData(SuccessMsgEnums.SUB_EVENT_QUERY_SUCCESS, activities);
    }

    /**
     * 更新活动
     * 
     * @param activity 活动实体对象（必须包含 id）
     * @return Result<Void> 表示是否更新成功
     */
    public Result<Void> updateActivity(ActivityPojo activity) {
        log.info("更新活动：id={}, name={}", activity.getId(), activity.getName());

        // 检查活动 ID 是否存在
        if (activity.getId() == null) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_NOT_FOUND);
        }

        boolean success = eventTagService.updateActivity(activity);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_UPDATE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.SUB_EVENT_UPDATE_SUCCESS);
    }

    /**
     * 删除活动（级联删除关联的人物和地点关系）
     * 
     * @param id 活动 ID
     * @return Result<Void> 表示是否删除成功
     */
    public Result<Void> deleteActivity(Integer id) {
        log.info("删除活动：id={}", id);

        boolean success = eventTagService.deleteActivity(id);
        if (!success) {
            throw new BusinessException(BusinessErrorMsgEnums.SUB_EVENT_DELETE_FAILED);
        }

        return ResultBuilder.success(SuccessMsgEnums.SUB_EVENT_DELETE_SUCCESS);
    }
}
