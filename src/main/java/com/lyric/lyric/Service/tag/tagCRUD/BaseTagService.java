package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Mapper.relation.DiaryTagMapper;
import com.lyric.lyric.Mapper.tag.TagMapper;
import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import com.lyric.lyric.POJO.tag.BaseTagPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础标签服务类
 * 提供基本标签的增删改查功能，支持主题标签和心情标签两种类型
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class BaseTagService {

    private final TagMapper tagMapper;
    private final DiaryTagMapper diaryTagMapper;

    public BaseTagService(TagMapper tagMapper, DiaryTagMapper diaryTagMapper) {
        this.tagMapper = tagMapper;
        this.diaryTagMapper = diaryTagMapper;
    }

    /**
     * 创建新标签
     * @param baseTagPojo 标签实体对象
     * @return 创建后的标签 ID（数据库自增主键）
     */
    public Integer createTag(BaseTagPojo baseTagPojo) {
        log.info("创建新标签：name={}, tagType={}", baseTagPojo.getName(), baseTagPojo.getTagType());
        tagMapper.insert(baseTagPojo);
        log.info("标签创建成功，ID={}", baseTagPojo.getId());
        return baseTagPojo.getId();
    }

    /**
     * 根据 ID 查询标签
     * @param id 标签 ID
     * @return 标签实体对象，若不存在则返回 null
     */
    public BaseTagPojo getTagById(Integer id) {
        log.debug("查询标签：id={}", id);
        BaseTagPojo tag = tagMapper.selectById(id);
        if (tag == null) {
            log.warn("标签不存在：id={}", id);
        }
        return tag;
    }

    /**
     * 根据日记 ID 查询对应的标签列表
     * @param diaryId 日记 ID
     * @return 标签实体列表，若日记没有关联标签则返回空列表
     */
    public List<BaseTagPojo> getTagsByDiaryId(Integer diaryId) {
        log.debug("根据日记 ID 查询标签：diaryId={}", diaryId);

        // 步骤 1: 查询该日记关联的所有标签 ID
        List<DiaryTagPojo> relations = diaryTagMapper.selectByDiaryId(diaryId);
        if (relations == null || relations.isEmpty()) {
            log.debug("日记未关联任何标签：diaryId={}", diaryId);
            return new ArrayList<>();
        }

        // 步骤 2: 提取所有标签 ID
        List<Integer> tagIds = new ArrayList<>();
        for (DiaryTagPojo relation : relations) {
            tagIds.add(relation.getTagId());
        }

        // 步骤 3: 批量查询标签详情
        List<BaseTagPojo> tags = new ArrayList<>();
        for (Integer tagId : tagIds) {
            BaseTagPojo tag = tagMapper.selectById(tagId);
            if (tag != null) {
                tags.add(tag);
            } else {
                log.warn("标签不存在，跳过：tagId={}", tagId);
            }
        }

        log.info("根据日记 ID 查询到 {} 个标签：diaryId={}", tags.size(), diaryId);
        return tags;
    }


    /**
     * 查询所有标签
     * @return 标签列表
     */
    public List<BaseTagPojo> getAllTags() {
        log.debug("查询所有标签");
        return tagMapper.selectAll();
    }

    /**
     * 根据标签类型查询标签
     * @param tagType 标签类型（THEME 或 MOOD）
     * @return 标签列表
     */
    public List<BaseTagPojo> getTagsByType(BaseTagPojo.TagType tagType) {
        log.debug("查询指定类型的标签：tagType={}", tagType);
        return tagMapper.selectByTagType(tagType);
    }

    /**
     * 更新标签信息
     * @param baseTagPojo 标签实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateTag(BaseTagPojo baseTagPojo) {
        log.info("更新标签：id={}, name={}", baseTagPojo.getId(), baseTagPojo.getName());
        int rows = tagMapper.update(baseTagPojo);
        if (rows > 0) {
            log.info("标签更新成功：id={}", baseTagPojo.getId());
            return true;
        } else {
            log.error("标签更新失败：id={}", baseTagPojo.getId());
            return false;
        }
    }

    /**
     * 删除标签（级联删除关联表）
     * 先删除 diary_tag 关联表中的相关记录，再删除标签本身
     * @param id 标签 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Integer id) {
        log.info("删除标签：id={}", id);
        
        // 检查标签是否存在
        BaseTagPojo tag = tagMapper.selectById(id);
        if (tag == null) {
            log.error("标签不存在，无法删除：id={}", id);
            return false;
        }
        
        // 级联删除：先删除 diary_tag 关联表中的记录
        try {
            List<com.lyric.lyric.POJO.relation.DiaryTagPojo> relations = diaryTagMapper.selectByTagId(id);
            if (!relations.isEmpty()) {
                log.info("标签被 {} 篇日记使用，将级联删除这些关联", relations.size());
                for (com.lyric.lyric.POJO.relation.DiaryTagPojo relation : relations) {
                    diaryTagMapper.deleteById(relation.getId());
                }
                log.info("已删除 {} 条标签关联记录", relations.size());
            }
            
            // 删除标签本身
            int rows = tagMapper.deleteById(id);
            if (rows > 0) {
                log.info("标签删除成功：id={}", id);
                return true;
            } else {
                log.error("标签删除失败：id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除标签时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    /**
     * 增加标签使用次数
     * @param id 标签 ID
     * @return 是否更新成功
     */
    public boolean incrementUsageCount(Integer id) {
        BaseTagPojo tag = tagMapper.selectById(id);
        if (tag == null) {
            log.error("标签不存在，无法增加使用次数：id={}", id);
            return false;
        }
        
        Integer currentCount = tag.getUsageCount();
        tag.setUsageCount(currentCount + 1);
        
        log.debug("增加标签使用次数：id={}, oldCount={}, newCount={}", 
                  id, currentCount, tag.getUsageCount());
        
        return updateTag(tag);
    }
}
