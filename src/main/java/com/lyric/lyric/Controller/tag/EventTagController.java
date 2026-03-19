package com.lyric.lyric.Controller.tag;

import com.lyric.lyric.POJO.tag.entityTag.event.SubEventPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.TogEventPojo;
import com.lyric.lyric.Service.tag.tagCRUD.TagService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 事件标签控制器
 * 提供父事件和子事件的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@RestController
@RequestMapping("/tag/event")
public class EventTagController {

    private final TagService tagService;

    public EventTagController(TagService tagService) {
        this.tagService = tagService;
    }

    // ==================== 父事件 (TogEvent) 相关接口 ====================

    /**
     * 创建父事件
     *
     * @param togEvent 父事件 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/togEvent/create")
    public Result<Void> createTogEvent(@RequestBody TogEventPojo togEvent) {
        return tagService.createTogEvent(togEvent);
    }

    /**
     * 根据 ID 查询父事件
     *
     * @param id 父事件 ID
     * @return 查询结果
     */
    @GetMapping("/togEvent/queryById")
    public Result<TogEventPojo> queryTogEventById(@RequestParam Integer id) {
        return tagService.getTogEventById(id);
    }

    /**
     * 根据日记 ID 查询父事件
     *
     * @param diaryId 日记 ID
     * @return 查询结果
     */
    @GetMapping("/togEvent/queryByDiaryId")
    public Result<TogEventPojo> queryTogEventByDiaryId(@RequestParam Integer diaryId) {
        return tagService.getTogEventByDiaryId(diaryId);
    }

    /**
     * 查询所有父事件
     *
     * @return 查询结果
     */
    @GetMapping("/togEvent/queryAll")
    public Result<List<TogEventPojo>> queryAllTogEvents() {
        return tagService.getAllTogEvents();
    }

    /**
     * 更新父事件
     *
     * @param togEvent 父事件 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/togEvent/update")
    public Result<Void> updateTogEvent(@RequestBody TogEventPojo togEvent) {
        return tagService.updateTogEvent(togEvent);
    }

    /**
     * 删除父事件（级联删除所有子事件及关联表）
     *
     * @param id 父事件 ID
     * @return 删除结果
     */
    @DeleteMapping("/togEvent/delete")
    public Result<Void> deleteTogEvent(@RequestParam Integer id) {
        return tagService.deleteTogEvent(id);
    }

    // ==================== 子事件 (SubEvent) 相关接口 ====================

    /**
     * 创建子事件
     *
     * @param subEvent 子事件 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/subEvent/create")
    public Result<Void> createSubEvent(@RequestBody SubEventPojo subEvent) {
        return tagService.createSubEvent(subEvent);
    }

    /**
     * 根据 ID 查询子事件
     *
     * @param id 子事件 ID
     * @return 查询结果
     */
    @GetMapping("/subEvent/queryById")
    public Result<SubEventPojo> querySubEventById(@RequestParam Integer id) {
        return tagService.getSubEventById(id);
    }

    /**
     * 根据父事件 ID 查询子事件
     *
     * @param togEventId 父事件 ID
     * @return 查询结果
     */
    @GetMapping("/subEvent/queryByTogEventId")
    public Result<List<SubEventPojo>> querySubEventsByTogEventId(@RequestParam Integer togEventId) {
        return tagService.getSubEventsByTogEventId(togEventId);
    }

    /**
     * 查询所有子事件
     *
     * @return 查询结果
     */
    @GetMapping("/subEvent/queryAll")
    public Result<List<SubEventPojo>> queryAllSubEvents() {
        return tagService.getAllSubEvents();
    }

    /**
     * 更新子事件
     *
     * @param subEvent 子事件 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/subEvent/update")
    public Result<Void> updateSubEvent(@RequestBody SubEventPojo subEvent) {
        return tagService.updateSubEvent(subEvent);
    }

    /**
     * 删除子事件（级联删除关联表）
     *
     * @param id 子事件 ID
     * @return 删除结果
     */
    @DeleteMapping("/subEvent/delete")
    public Result<Void> deleteSubEvent(@RequestParam Integer id) {
        return tagService.deleteSubEvent(id);
    }
}
