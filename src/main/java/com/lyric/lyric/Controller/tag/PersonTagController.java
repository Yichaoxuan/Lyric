package com.lyric.lyric.Controller.tag;

import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.tag.tagCRUD.TagService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 人物标签控制器
 * 提供人物标签的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@RestController
@RequestMapping("/tag/person")
public class PersonTagController {

    private final TagService tagService;

    public PersonTagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * 创建人物标签
     *
     * @param personPojo 人物标签 DTO 对象
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Void> createPersonTag(@RequestBody PersonPojo personPojo) {
        return tagService.createPersonTag(personPojo);
    }

    /**
     * 根据 ID 查询人物标签
     *
     * @param id 人物 ID
     * @return 查询结果
     */
    @GetMapping("/queryById")
    public Result<PersonPojo> queryPersonTagById(@RequestParam Integer id) {
        return tagService.getPersonTagById(id);
    }

    /**
     * 根据名称查询人物标签
     *
     * @param name 人物名称
     * @return 查询结果
     */
    @GetMapping("/queryByName")
    public Result<PersonPojo> queryPersonTagByName(@RequestParam String name) {
        return tagService.getPersonTagByName(name);
    }

    /**
     * 查询所有人物标签
     *
     * @return 查询结果
     */
    @GetMapping("/queryAll")
    public Result<List<PersonPojo>> queryAllPersonTags() {
        return tagService.getAllPersonTags();
    }

    /**
     * 更新人物标签
     *
     * @param personPojo 人物标签 DTO 对象（必须包含 id）
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result<Void> updatePersonTag(@RequestBody PersonPojo personPojo) {
        return tagService.updatePersonTag(personPojo);
    }

    /**
     * 删除人物标签（级联删除关联表）
     *
     * @param id 人物 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deletePersonTag(@RequestParam Integer id) {
        return tagService.deletePersonTag(id);
    }

    /**
     * 增加人物出现次数
     *
     * @param id 人物 ID
     * @return 更新结果
     */
    @PostMapping("/incrementAppearance")
    public Result<Void> incrementPersonAppearance(@RequestParam Integer id) {
        return tagService.incrementPersonAppearance(id);
    }
}
