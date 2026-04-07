package com.lyric.lyric.Controller.tag;

import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.EventPojo;
import com.lyric.lyric.Service.tag.tagCRUD.TagService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 事件标签控制器
 * 提供事件(Event)和活动(Activity)的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Slf4j
@RestController
@RequestMapping("/tag/event")
public class EventTagController {

    private final TagService tagService;

    public EventTagController(TagService tagService) {
        this.tagService = tagService;
    }

    // ==================== 事件 (Event) 相关接口 ====================

    /**
     * 创建事件
     *
     * @param event 事件 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Void> createEvent(@RequestBody EventPojo event) {
        return tagService.createEvent(event);
    }

    /**
     * 根据 ID 查询事件
     *
     * @param id 事件 ID
     * @return 查询结果
     */
    @GetMapping("/queryById")
    public Result<EventPojo> queryEventById(@RequestParam Integer id) {
        return tagService.getEventById(id);
    }

    /**
     * 查询所有事件
     *
     * @return 查询结果
     */
    @GetMapping("/queryAll")
    public Result<List<EventPojo>> queryAllEvents() {
        return tagService.getAllEvents();
    }

    /**
     * 更新事件
     *
     * @param event 事件 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<Void> updateEvent(@RequestBody EventPojo event) {
        return tagService.updateEvent(event);
    }

    /**
     * 删除事件（级联删除所有活动及关联表）
     *
     * @param id 事件 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteEvent(@RequestParam Integer id) {
        return tagService.deleteEvent(id);
    }

    // ==================== 活动 (Activity) 相关接口 ====================

    /**
     * 创建活动
     *
     * @param activity 活动 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/activity/create")
    public Result<Void> createActivity(@RequestBody ActivityPojo activity) {
        return tagService.createActivity(activity);
    }

    /**
     * 根据 ID 查询活动
     *
     * @param id 活动 ID
     * @return 查询结果
     */
    @GetMapping("/activity/queryById")
    public Result<ActivityPojo> queryActivityById(@RequestParam Integer id) {
        return tagService.getActivityById(id);
    }

    /**
     * 根据事件 ID 查询活动
     *
     * @param eventId 事件 ID
     * @return 查询结果
     */
    @GetMapping("/activity/queryByEventId")
    public Result<List<ActivityPojo>> queryActivitiesByEventId(@RequestParam Integer eventId) {
        return tagService.getActivitiesByEventId(eventId);
    }

    /**
     * 查询所有活动
     *
     * @return 查询结果
     */
    @GetMapping("/activity/queryAll")
    public Result<List<ActivityPojo>> queryAllActivities() {
        return tagService.getAllActivities();
    }

    /**
     * 更新活动
     *
     * @param activity 活动 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/activity/update")
    public Result<Void> updateActivity(@RequestBody ActivityPojo activity) {
        return tagService.updateActivity(activity);
    }

    /**
     * 删除活动（级联删除关联的人物和地点关系）
     *
     * @param id 活动 ID
     * @return 删除结果
     */
    @DeleteMapping("/activity/delete")
    public Result<Void> deleteActivity(@RequestParam Integer id) {
        return tagService.deleteActivity(id);
    }
}
