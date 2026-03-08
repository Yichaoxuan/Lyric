package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-人物关联数据访问层接口
 */
@Mapper
public interface DiaryPersonMapper {
    
    /**
     * 插入一条日记-人物关联记录
     *
     * @param diaryPerson 日记-人物关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_person(diary_id, person_id, mention_type) " +
            "VALUES(#{diaryId}, #{personId}, #{mentionType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryPersonPojo diaryPerson);
    
    /**
     * 根据ID查询日记-人物关联
     *
     * @param id 关联ID
     * @return 日记-人物关联实体
     */
    @Select("SELECT * FROM diary_person WHERE id = #{id}")
    DiaryPersonPojo selectById(Integer id);
    
    /**
     * 根据日记ID查询所有关联的人物
     *
     * @param diaryId 日记ID
     * @return 日记-人物关联列表
     */
    @Select("SELECT * FROM diary_person WHERE diary_id = #{diaryId}")
    List<DiaryPersonPojo> selectByDiaryId(Integer diaryId);

    /**
     * 根据人物ID查询所有关联的日记
     *
     * @param personId 人物ID
     * @return 日记-人物关联列表
     */
    @Select("SELECT * FROM diary_person WHERE person_id = #{personId}")
    List<DiaryPersonPojo> selectByPersonId(Integer personId);

    /**
     * 根据日记ID和人物ID查询关联记录
     *
     * @param diaryId 日记ID
     * @param personId 人物ID
     * @return 日记-人物关联实体
     */
    @Select("SELECT * FROM diary_person WHERE diary_id = #{diaryId} AND person_id = #{personId}")
    DiaryPersonPojo selectByDiaryIdAndPersonId(@Param("diaryId") Integer diaryId, @Param("personId") Integer personId);
    
    /**
     * 查询所有日记-人物关联
     *
     * @return 日记-人物关联列表
     */
    @Select("SELECT * FROM diary_person")
    List<DiaryPersonPojo> selectAll();
    
    /**
     * 更新日记-人物关联
     *
     * @param diaryPerson 日记-人物关联实体
     * @return 影响的行数
     */
    @Update("UPDATE diary_person SET diary_id=#{diaryId}, person_id=#{personId}, " +
            "mention_type=#{mentionType} WHERE id=#{id}")
    int update(DiaryPersonPojo diaryPerson);
    
    /**
     * 根据ID删除日记-人物关联
     *
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_person WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据日记ID和人物ID删除关联记录
     *
     * @param diaryId 日记ID
     * @param personId 人物ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_person WHERE diary_id = #{diaryId} AND person_id = #{personId}")
    int deleteByDiaryIdAndPersonId(@Param("diaryId") Integer diaryId, @Param("personId") Integer personId);
}