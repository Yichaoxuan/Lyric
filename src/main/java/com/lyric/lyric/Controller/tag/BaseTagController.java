package com.lyric.lyric.Controller.tag;

import com.lyric.lyric.POJO.tag.BaseTagPojo;
import com.lyric.lyric.Service.tag.tagCRUD.TagService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基本标签控制器
 * 提供基本标签的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@RestController
@RequestMapping("/tag/base")
public class BaseTagController {

    private final TagService tagService;

    public BaseTagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 创建基本标签
     *
     * @param baseTagPojo 基本标签 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Void> createBaseTag(@RequestBody BaseTagPojo baseTagPojo) {
        return tagService.createBaseTag(baseTagPojo);
    }

    /**
     * 根据 ID 查询基本标签
     *
     * @param id 标签 ID
     * @return 查询结果
     */
    @GetMapping("/queryById")
    public Result<BaseTagPojo> queryBaseTagById(@RequestParam Integer id) {
        return tagService.getBaseTagById(id);
    }

    /**
     * 查询所有基本标签
     *
     * @return 查询结果
     */
    @GetMapping("/queryAll")
    public Result<List<BaseTagPojo>> queryAllBaseTags() {
        return tagService.getAllBaseTags();
    }

    /**
     * 根据类型查询基本标签
     *
     * @param tagType 标签类型
     * @return 查询结果
     */
    @GetMapping("/queryByType")
    public Result<List<BaseTagPojo>> queryBaseTagsByType(@RequestParam BaseTagPojo.TagType tagType) {
        return tagService.getBaseTagsByType(tagType);
    }

    /**
     * 更新基本标签
     *
     * @param baseTagPojo 基本标签 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<Void> updateBaseTag(@RequestBody BaseTagPojo baseTagPojo) {
        return tagService.updateBaseTag(baseTagPojo);
    }

    /**
     * 删除基本标签（级联删除关联表）
     *
     * @param id 标签 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteBaseTag(@RequestParam Integer id) {
        return tagService.deleteBaseTag(id);
    }

    /**
     * 增加基本标签使用次数
     *
     * @param id 标签 ID
     * @return 更新结果
     */
    @PostMapping("/incrementUsage")
    public Result<Void> incrementBaseTagUsage(@RequestParam Integer id) {
        return tagService.incrementBaseTagUsage(id);
    }
}
