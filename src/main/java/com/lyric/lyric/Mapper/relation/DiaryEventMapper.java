package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.DiaryEventPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记-事件关联数据访问层接口
 */
@Mapper
public interface DiaryEventMapper {
    
    /**
     * 插入一条日记-事件关联记录
     * @param diaryEvent 日记-事件关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary_event(diary_id, event_id) " +
            "VALUES(#{diaryId}, #{eventId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryEventPojo diaryEvent);
    
    /**
     * 根据ID查询日记-事件关联
     * @param id 关联ID
     * @return 日记-事件关联实体
     */
    @Select("SELECT * FROM diary_event WHERE id = #{id}")
    DiaryEventPojo selectById(Integer id);
    
    /**
     * 根据日记ID查询所有关联的事件
     * @param diaryId 日记ID
     * @return 日记-事件关联列表
     */
    @Select("SELECT * FROM diary_event WHERE diary_id = #{diaryId}")
    List<DiaryEventPojo> selectByDiaryId(Integer diaryId);
    
    /**
     * 查询所有日记-事件关联
     * @return 日记-事件关联列表
     */
    @Select("SELECT * FROM diary_event")
    List<DiaryEventPojo> selectAll();
    
    /**
     * 更新日记-事件关联
     * @param diaryEvent 日记-事件关联实体
     * @return 影响的行数
     */
    @Update("UPDATE diary_event SET diary_id=#{diaryId}, event_id=#{eventId} WHERE id=#{id}")
    int update(DiaryEventPojo diaryEvent);
    
    /**
     * 根据ID删除日记-事件关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_event WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据日记ID和事件ID删除关联记录
     * @param diaryId 日记ID
     * @param eventId 事件ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary_event WHERE diary_id = #{diaryId} AND event_id = #{eventId}")
    int deleteByDiaryIdAndEventId(@Param("diaryId") Integer diaryId, @Param("eventId") Integer eventId);
}