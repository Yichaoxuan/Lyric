package com.lyric.lyric.Mapper.relation;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-活动关联数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Mapper
public interface DiaryActivityMapper {

    /**
     * 插入一条日记-活动关联记录
     *
     * @param diaryId 日记ID
     * @param activityId 活动ID
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_activity(diary_id, activity_id) " +
            "VALUES(#{diaryId}, #{activityId})")
    int insert(@Param("diaryId") Integer diaryId, @Param("activityId") Integer activityId);

    /**
     * 根据ID查询日记-活动关联的活动ID
     *
     * @param id 关联ID
     * @return 活动ID，如果不存在则返回null
     */
    @Select("SELECT activity_id FROM diary_activity WHERE id = #{id}")
    Integer selectById(Integer id);

    /**
     * 根据日记ID查询所有关联的活动ID列表
     *
     * @param diaryId 日记ID
     * @return 活动ID列表
     */
    @Select("SELECT activity_id FROM diary_activity WHERE diary_id = #{diaryId}")
    List<Integer> selectByDiaryId(Integer diaryId);

    /**
     * 根据活动ID查询所有关联的日记ID列表
     *
     * @param activityId 活动ID
     * @return 日记ID列表
     */
    @Select("SELECT diary_id FROM diary_activity WHERE activity_id = #{activityId}")
    List<Integer> selectByActivityId(Integer activityId);

    /**
     * 更新日记-活动关联
     *
     * @param id 关联ID
     * @param diaryId 日记ID
     * @param activityId 活动ID
     * @return 影响的行数
     */
    @Update("UPDATE diary_activity SET diary_id=#{diaryId}, activity_id=#{activityId} WHERE id=#{id}")
    int update(@Param("id") Integer id, @Param("diaryId") Integer diaryId, @Param("activityId") Integer activityId);

    /**
     * 根据ID删除日记-活动关联
     *
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_activity WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据日记ID和活动ID删除关联记录
     *
     * @param diaryId 日记ID
     * @param activityId 活动ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_activity WHERE diary_id = #{diaryId} AND activity_id = #{activityId}")
    int deleteByDiaryIdAndActivityId(@Param("diaryId") Integer diaryId, @Param("activityId") Integer activityId);

    /**
     * 根据日记ID删除所有关联记录
     *
     * @param diaryId 日记ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_activity WHERE diary_id = #{diaryId}")
    int deleteByDiaryId(Integer diaryId);

    /**
     * 根据活动ID删除所有关联记录
     *
     * @param activityId 活动ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_activity WHERE activity_id = #{activityId}")
    int deleteByActivityId(Integer activityId);
}
