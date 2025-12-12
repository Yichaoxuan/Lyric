package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-地点关联数据访问层接口
 */
@Mapper
public interface DiaryLocationMapper {
    
    /**
     * 插入一条日记-地点关联记录
     * @param diaryLocation 日记-地点关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_location(diary_id, location_id, appearance_date, mention_type) " +
            "VALUES(#{diaryId}, #{locationId}, #{appearanceDate}, #{mentionType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryLocationPojo diaryLocation);
    
    /**
     * 根据ID查询日记-地点关联
     * @param id 关联ID
     * @return 日记-地点关联实体
     */
    @Select("SELECT * FROM diary_location WHERE id = #{id}")
    DiaryLocationPojo selectById(Integer id);
    
    /**
     * 根据日记ID查询所有关联的地点
     * @param diaryId 日记ID
     * @return 日记-地点关联列表
     */
    @Select("SELECT * FROM diary_location WHERE diary_id = #{diaryId}")
    List<DiaryLocationPojo> selectByDiaryId(Integer diaryId);
    
    /**
     * 查询所有日记-地点关联
     * @return 日记-地点关联列表
     */
    @Select("SELECT * FROM diary_location")
    List<DiaryLocationPojo> selectAll();
    
    /**
     * 更新日记-地点关联
     * @param diaryLocation 日记-地点关联实体
     * @return 影响的行数
     */
    @Update("UPDATE diary_location SET diary_id=#{diaryId}, location_id=#{locationId}, " +
            "appearance_date=#{appearanceDate}, mention_type=#{mentionType} WHERE id=#{id}")
    int update(DiaryLocationPojo diaryLocation);
    
    /**
     * 根据ID删除日记-地点关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_location WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据日记ID和地点ID删除关联记录
     * @param diaryId 日记ID
     * @param locationId 地点ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_location WHERE diary_id = #{diaryId} AND location_id = #{locationId}")
    int deleteByDiaryIdAndLocationId(@Param("diaryId") Integer diaryId, @Param("locationId") Integer locationId);
}