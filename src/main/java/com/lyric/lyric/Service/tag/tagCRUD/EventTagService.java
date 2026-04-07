package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Mapper.relation.ActivityLocationMapper;
import com.lyric.lyric.Mapper.relation.ActivityPersonMapper;
import com.lyric.lyric.Mapper.tag.entity.ActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.POJO.relation.ActivityLocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.EventPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 事件标签服务类
 * 提供事件(Event)和活动(Activity)的增删改查功能
 * 删除时支持级联删除关联表数据
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Slf4j
@Service
public class EventTagService {

    private final EventMapper eventMapper;
    private final ActivityMapper activityMapper;
    private final ActivityPersonMapper activityPersonMapper;
    private final ActivityLocationMapper activityLocationMapper;

    public EventTagService(EventMapper eventMapper,
                          ActivityMapper activityMapper,
                          ActivityPersonMapper activityPersonMapper,
                          ActivityLocationMapper activityLocationMapper) {
        this.eventMapper = eventMapper;
        this.activityMapper = activityMapper;
        this.activityPersonMapper = activityPersonMapper;
        this.activityLocationMapper = activityLocationMapper;
    }

    // ==================== 事件 (Event) 相关操作 ====================

    /**
     * 创建事件
     * @param event 事件实体对象
     */
    public void createEvent(EventPojo event) {
        log.info("创建新事件：name={}, startDate={}, endDate={}",
                event.getName(), event.getStartDate(), event.getEndDate());
        eventMapper.insert(event);
        log.info("事件创建成功，ID={}", event.getId());
    }

    /**
     * 根据 ID 查询事件
     * @param id 事件 ID
     * @return 事件实体对象，若不存在则返回 null
     */
    public EventPojo getEventById(Integer id) {
        log.debug("查询事件：id={}", id);
        EventPojo event = eventMapper.selectEventById(id);
        if (event == null) {
            log.warn("事件不存在：id={}", id);
        }
        return event;
    }

    /**
     * 查询所有事件
     * @return 事件列表
     */
    public List<EventPojo> getAllEvents() {
        log.debug("查询所有事件");
        return eventMapper.selectAllEvents();
    }

    /**
     * 更新事件信息
     * @param event 事件实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateEvent(EventPojo event) {
        log.info("更新事件：id={}, name={}", event.getId(), event.getName());
        int rows = eventMapper.updateEvent(event);
        if (rows > 0) {
            log.info("事件更新成功：id={}", event.getId());
            return true;
        } else {
            log.error("事件更新失败：id={}", event.getId());
            return false;
        }
    }

    /**
     * 删除事件（级联删除所有活动及关联表）
     * 删除顺序：activity_location -> activity_person -> activity -> event
     * @param id 事件 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteEvent(Integer id) {
        log.info("删除事件：id={}", id);

        // 检查事件是否存在
        EventPojo event = eventMapper.selectEventById(id);
        if (event == null) {
            log.error("事件不存在，无法删除：id={}", id);
            return false;
        }

        try {
            // 查询该事件下的所有活动
            List<ActivityPojo> activities = activityMapper.selectByEventId(id);
            if (!activities.isEmpty()) {
                log.info("事件下包含 {} 个活动，将级联删除", activities.size());

                // 对每个活动进行级联删除
                for (ActivityPojo activity : activities) {
                    deleteActivityWithRelations(activity.getId());
                }
                log.info("已删除 {} 个活动及其关联数据", activities.size());
            }

            // 删除事件本身
            int rows = eventMapper.deleteEventById(id);
            if (rows > 0) {
                log.info("事件删除成功：id={}", id);
                return true;
            } else {
                log.error("事件删除失败：id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除事件时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    // ==================== 活动 (Activity) 相关操作 ====================

    /**
     * 创建活动
     * @param activity 活动实体对象
     */
    public void createActivity(ActivityPojo activity) {
        log.info("创建新活动：name={}, eventId={}, activityDate={}",
                activity.getName(), activity.getEventId(), activity.getActivityDate());
        activityMapper.insert(activity);
        log.info("活动创建成功，ID={}", activity.getId());
    }

    /**
     * 根据 ID 查询活动
     * @param id 活动 ID
     * @return 活动实体对象，若不存在则返回 null
     */
    public ActivityPojo getActivityById(Integer id) {
        log.debug("查询活动：id={}", id);
        ActivityPojo activity = activityMapper.selectById(id);
        if (activity == null) {
            log.warn("活动不存在：id={}", id);
        }
        return activity;
    }

    /**
     * 根据事件 ID 查询所有活动
     * @param eventId 事件 ID
     * @return 活动列表
     */
    public List<ActivityPojo> getActivitiesByEventId(Integer eventId) {
        log.debug("根据事件 ID 查询活动：eventId={}", eventId);
        return activityMapper.selectByEventId(eventId);
    }

    /**
     * 查询所有活动
     * @return 活动列表
     */
    public List<ActivityPojo> getAllActivities() {
        log.debug("查询所有活动");
        return activityMapper.selectAll();
    }

    /**
     * 更新活动信息
     * @param activity 活动实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateActivity(ActivityPojo activity) {
        log.info("更新活动：id={}, name={}", activity.getId(), activity.getName());
        int rows = activityMapper.update(activity);
        if (rows > 0) {
            log.info("活动更新成功：id={}", activity.getId());
            return true;
        } else {
            log.error("活动更新失败：id={}", activity.getId());
            return false;
        }
    }

    /**
     * 删除活动（不删除事件，仅级联删除该活动的关联表）
     * 删除顺序：activity_location -> activity_person -> activity
     * @param id 活动 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteActivity(Integer id) {
        log.info("删除活动：id={}", id);

        // 检查活动是否存在
        ActivityPojo activity = activityMapper.selectById(id);
        if (activity == null) {
            log.error("活动不存在，无法删除：id={}", id);
            return false;
        }

        try {
            // 级联删除关联表数据
            deleteActivityWithRelations(id);
            log.info("活动及其关联数据删除成功：id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除活动时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    /**
     * 内部方法：级联删除活动的关联表数据
     * 删除顺序：activity_location -> activity_person -> activity
     * @param activityId 活动 ID
     */
    private void deleteActivityWithRelations(Integer activityId) {
        log.debug("开始级联删除活动关联数据：activityId={}", activityId);

        // 1. 删除 activity_location 关联
        List<ActivityLocationPojo> locationRelations = activityLocationMapper.selectByActivityId(activityId);
        if (!locationRelations.isEmpty()) {
            log.info("活动关联了 {} 个地点，正在删除", locationRelations.size());
            for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : locationRelations) {
                activityLocationMapper.deleteById(relation.getId());
            }
            log.info("已删除 {} 条地点关联记录", locationRelations.size());
        }

        // 2. 删除 activity_person 关联
        List<com.lyric.lyric.POJO.relation.ActivityPersonPojo> personRelations =
            activityPersonMapper.selectByActivityId(activityId);
        if (!personRelations.isEmpty()) {
            log.info("活动关联了 {} 个人物，正在删除", personRelations.size());
            for (com.lyric.lyric.POJO.relation.ActivityPersonPojo relation : personRelations) {
                activityPersonMapper.deleteById(relation.getId());
            }
            log.info("已删除 {} 条人物关联记录", personRelations.size());
        }

        // 3. 删除活动本身
        int rows = activityMapper.deleteById(activityId);
        if (rows > 0) {
            log.debug("活动删除成功：id={}", activityId);
        } else {
            log.error("活动删除失败：id={}", activityId);
        }
    }
}
