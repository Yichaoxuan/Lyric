package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.event.SubEventPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.TogEventPojo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件数据访问层接口
 * 包含父事件(tog_event)和子事件(sub_event)的操作
 *
 * @author Yichaoxuan
 */
@Mapper
public interface EventMapper {

    /**
     * 根据日记 ID 查询最小的父事件下最小的子事件的日期
     * @param diaryId 日记 ID
     * @return 子事件的 event_date（格式：yyyy-MM-dd），若无则返回 null
     */
    @Select("SELECT se.event_date " +
            "FROM sub_event se " +
            "WHERE se.tog_event_id = ( " +
            "    SELECT te.id " +
            "    FROM tog_event te " +
            "    WHERE te.diary_id = #{diaryId} " +
            "    ORDER BY te.id " +
            "    LIMIT 1 " +
            ") " +
            "ORDER BY se.id " +
            "LIMIT 1")
    LocalDateTime selectMinSubEventDateByDiaryId(Integer diaryId);

    /**
     * 根据父事件 ID 查询关联的日记 ID
     * @param togEventId 父事件 ID
     * @return 日记 ID，若无则返回 null
     */
    @Select("SELECT diary_id FROM tog_event WHERE id = #{togEventId}")
    Integer selectDiaryIdByTogEventId(Integer togEventId);
    
    // ==================== 父事件(tog_event)相关操作 ====================
    
    /**
     * 插入一条父事件记录
     *
     * @param togEvent 父事件实体
     */
    @Insert("INSERT INTO tog_event(diary_id, name, start_date, end_date, description, importance, color) " +
            "VALUES(#{diaryId}, #{name}, #{startDate}, #{endDate}, #{description}, #{importance}, #{color})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTogEvent(TogEventPojo togEvent);
    
    /**
     * 根据ID查询父事件
     * @param id 父事件ID
     * @return 父事件实体
     */
    @Select("SELECT * FROM tog_event WHERE id = #{id}")
    TogEventPojo selectTogEventById(Integer id);
    
    /**
     * 根据日记ID查询父事件
     * @param diaryId 日记ID
     * @return 父事件实体
     */
    @Select("SELECT * FROM tog_event WHERE diary_id = #{diaryId}")
    TogEventPojo selectTogEventByDiaryId(Integer diaryId);
    
    /**
     * 查询所有父事件
     * @return 父事件列表
     */
    @Select("SELECT * FROM tog_event")
    List<TogEventPojo> selectAllTogEvents();
    
    /**
     * 更新父事件
     * @param togEvent 父事件实体
     * @return 影响的行数
     */
    @Update("UPDATE tog_event SET diary_id=#{diaryId}, name=#{name}, start_date=#{startDate}, end_date=#{endDate}, " +
            "description=#{description}, importance=#{importance}, color=#{color} WHERE id=#{id}")
    int updateTogEvent(TogEventPojo togEvent);
    
    /**
     * 根据ID删除父事件
     * @param id 父事件ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM tog_event WHERE id = #{id}")
    int deleteTogEventById(Integer id);
    
    // ==================== 子事件(sub_event)相关操作 ====================
    
    /**
     * 插入一条子事件记录
     *
     * @param subEvent 子事件实体
     */
    @Insert("INSERT INTO sub_event(tog_event_id, name, event_date, description, importance, color) " +
            "VALUES(#{togEventId}, #{name}, #{eventDate}, #{description}, #{importance}, #{color})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertSubEvent(SubEventPojo subEvent);
    
    /**
     * 根据ID查询子事件
     * @param id 子事件ID
     * @return 子事件实体
     */
    @Select("SELECT * FROM sub_event WHERE id = #{id}")
    SubEventPojo selectSubEventById(Integer id);
    
    /**
     * 根据父事件ID查询所有子事件
     * @param togEventId 父事件ID
     * @return 子事件列表
     */
    @Select("SELECT * FROM sub_event WHERE tog_event_id = #{togEventId}")
    List<SubEventPojo> selectSubEventsByTogEventId(Integer togEventId);
    
    /**
     * 查询所有子事件
     * @return 子事件列表
     */
    @Select("SELECT * FROM sub_event")
    List<SubEventPojo> selectAllSubEvents();
    
    /**
     * 更新子事件
     * @param subEvent 子事件实体
     * @return 影响的行数
     */
    @Update("UPDATE sub_event SET tog_event_id=#{togEventId}, name=#{name}, event_date=#{eventDate}, " +
            "description=#{description}, importance=#{importance}, color=#{color} WHERE id=#{id}")
    int updateSubEvent(SubEventPojo subEvent);
    
    /**
     * 根据ID删除子事件
     * @param id 子事件ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM sub_event WHERE id = #{id}")
    int deleteSubEventById(Integer id);
}