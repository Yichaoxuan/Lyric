package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.Pojo.relation.DiaryTagPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-标签关联数据访问层接口
 */
@Mapper
public interface DiaryTagMapper {
    
    /**
     * 插入一条日记-标签关联记录
     * @param diaryTag 日记-标签关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_tag(diary_id, tag_id, confidence) " +
            "VALUES(#{diaryId}, #{tagId}, #{confidence})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryTagPojo diaryTag);
    
    /**
     * 根据ID查询日记-标签关联
     * @param id 关联ID
     * @return 日记-标签关联实体
     */
    @Select("SELECT * FROM diary_tag WHERE id = #{id}")
    DiaryTagPojo selectById(Long id);
    
    /**
     * 根据日记ID查询所有关联的标签
     * @param diaryId 日记ID
     * @return 日记-标签关联列表
     */
    @Select("SELECT * FROM diary_tag WHERE diary_id = #{diaryId}")
    List<DiaryTagPojo> selectByDiaryId(Long diaryId);
    
    /**
     * 查询所有日记-标签关联
     * @return 日记-标签关联列表
     */
    @Select("SELECT * FROM diary_tag")
    List<DiaryTagPojo> selectAll();
    
    /**
     * 更新日记-标签关联
     * @param diaryTag 日记-标签关联实体
     * @return 影响的行数
     */
    @Update("UPDATE diary_tag SET diary_id=#{diaryId}, tag_id=#{tagId}, confidence=#{confidence} WHERE id=#{id}")
    int update(DiaryTagPojo diaryTag);
    
    /**
     * 根据ID删除日记-标签关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_tag WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据日记ID和标签ID删除关联记录
     * @param diaryId 日记ID
     * @param tagId 标签ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_tag WHERE diary_id = #{diaryId} AND tag_id = #{tagId}")
    int deleteByDiaryIdAndTagId(@Param("diaryId") Long diaryId, @Param("tagId") Long tagId);
}