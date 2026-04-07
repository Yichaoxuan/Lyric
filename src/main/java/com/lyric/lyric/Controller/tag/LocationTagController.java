package com.lyric.lyric.Controller.tag;

import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.Service.tag.tagCRUD.TagService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地点标签控制器
 * 提供地点标签的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@RestController
@RequestMapping("/tag/location")
public class LocationTagController {

    private final TagService tagService;

    public LocationTagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 创建地点标签
     *
     * @param locationPojo 地点标签 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Void> createLocationTag(@RequestBody LocationPojo locationPojo) {
        return tagService.createLocationTag(locationPojo);
    }

    /**
     * 根据 ID 查询地点标签
     *
     * @param id 地点 ID
     * @return 查询结果
     */
    @GetMapping("/queryById")
    public Result<LocationPojo> queryLocationTagById(@RequestParam Integer id) {
        return tagService.getLocationTagById(id);
    }

    /**
     * 根据名称查询地点标签
     *
     * @param name 地点名称
     * @return 查询结果
     */
    @GetMapping("/queryByName")
    public Result<List<LocationPojo>> queryLocationTagsByName(@RequestParam String name) {
        return tagService.getLocationTagsByName(name);
    }

    /**
     * 根据日记ID 查询地点标签列表
     *
     * @param diaryId 日记ID
     * @return 查询结果
     */
    @GetMapping("/queryByDiaryId")
    public Result<List<LocationPojo>> queryLocationTagsByDiaryId(@RequestParam Integer diaryId) {
        return tagService.getLocationTagsByDiaryId(diaryId);
    }

    /**
     * 根据活动ID 查询地点标签列表
     *
     * @param activityId 活动ID
     * @return 查询结果
     */
    @GetMapping("/queryByActivityId")
    public Result<List<LocationPojo>> queryLocationTagsByActivityId(@RequestParam Integer activityId) {
        return tagService.getLocationTagsByActivityId(activityId);
    }

    /**
     * 查询所有地点标签
     *
     * @return 查询结果
     */
    @GetMapping("/queryAll")
    public Result<List<LocationPojo>> queryAllLocationTags() {
        return tagService.getAllLocationTags();
    }

    /**
     * 更新地点标签
     *
     * @param locationPojo 地点标签 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<Void> updateLocationTag(@RequestBody LocationPojo locationPojo) {
        return tagService.updateLocationTag(locationPojo);
    }

    /**
     * 删除地点标签（级联删除关联表）
     *
     * @param id 地点 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteLocationTag(@RequestParam Integer id) {
        return tagService.deleteLocationTag(id);
    }

    /**
     * 增加地点出现次数
     *
     * @param id 地点 ID
     * @return 更新结果
     */
    @PostMapping("/incrementAppearance")
    public Result<Void> incrementLocationAppearance(@RequestParam Integer id) {
        return tagService.incrementLocationAppearance(id);
    }
}
