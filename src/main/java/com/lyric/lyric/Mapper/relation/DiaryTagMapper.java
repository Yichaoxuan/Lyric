package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-标签关联数据访问层接口
 */
@Mapper
public interface DiaryTagMapper {

    /**
     * 插入一条日记-标签关联记录
     * 
     * @param diaryTag 日记-标签关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_tag(diary_id, tag_id) " +
            "VALUES(#{diaryId}, #{tagId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryTagPojo diaryTag);

    /**
     * 根据ID查询日记-标签关联
     * 
     * @param id 关联ID
     * @return 日记-标签关联实体
     */
    @Select("SELECT * FROM diary_tag WHERE id = #{id}")
    DiaryTagPojo selectById(Integer id);

    /**
     * 根据日记ID查询所有关联的标签
     * 
     * @param diaryId 日记ID
     * @return 日记-标签关联列表
     */
    @Select("SELECT * FROM diary_tag WHERE diary_id = #{diaryId}")
    List<DiaryTagPojo> selectByDiaryId(Integer diaryId);

    /**
     * 根据标签ID查询所有关联的日记
     */
    @Select("SELECT * FROM diary_tag WHERE tag_id = #{tagId}")
    List<DiaryTagPojo> selectByTagId(Integer tagId);

    /**
     * 通过日记ID和标签ID查询关联记录
     */
    @Select("SELECT * FROM diary_tag WHERE diary_id = #{diaryId} AND tag_id = #{tagId}")
    DiaryTagPojo selectByDiaryIdAndTagId(Integer diaryId, Integer tagId);

    /**
     * 查询所有日记-标签关联
     * 
     * @return 日记-标签关联列表
     */
    @Select("SELECT * FROM diary_tag")
    List<DiaryTagPojo> selectAll();

    /**
     * 更新日记-标签关联
     * 
     * @param diaryTag 日记-标签关联实体
     * @return 影响的行数
     */
    @Update("UPDATE diary_tag SET diary_id=#{diaryId}, tag_id=#{tagId} WHERE id=#{id}")
    int update(DiaryTagPojo diaryTag);

    /**
     * 根据ID删除日记-标签关联
     * 
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_tag WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据日记ID和标签ID删除关联记录
     * 
     * @param diaryId 日记ID
     * @param tagId   标签ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_tag WHERE diary_id = #{diaryId} AND tag_id = #{tagId}")
    int deleteByDiaryIdAndTagId(@Param("diaryId") Integer diaryId, @Param("tagId") Integer tagId);

    /**
     * 根据日记ID删除所有关联记录
     *
     * @param diaryId 日记ID
     */
    @Delete("DELETE FROM diary_tag WHERE diary_id = #{diaryId}")
    void deleteByDiaryId(Integer diaryId);
}