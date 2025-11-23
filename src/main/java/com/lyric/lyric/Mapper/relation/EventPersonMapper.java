package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.Pojo.relation.EventPersonPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 事件-人物关联数据访问层接口
 */
@Mapper
public interface EventPersonMapper {
    
    /**
     * 插入一条事件-人物关联记录
     * @param eventPerson 事件-人物关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO event_person(event_id, person_id, role) " +
            "VALUES(#{eventId}, #{personId}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EventPersonPojo eventPerson);
    
    /**
     * 根据ID查询事件-人物关联
     * @param id 关联ID
     * @return 事件-人物关联实体
     */
    @Select("SELECT * FROM event_person WHERE id = #{id}")
    EventPersonPojo selectById(Integer id);
    
    /**
     * 根据事件ID查询所有关联的人物
     * @param eventId 事件ID
     * @return 事件-人物关联列表
     */
    @Select("SELECT * FROM event_person WHERE event_id = #{eventId}")
    List<EventPersonPojo> selectByEventId(Integer eventId);
    
    /**
     * 查询所有事件-人物关联
     * @return 事件-人物关联列表
     */
    @Select("SELECT * FROM event_person")
    List<EventPersonPojo> selectAll();
    
    /**
     * 更新事件-人物关联
     * @param eventPerson 事件-人物关联实体
     * @return 影响的行数
     */
    @Update("UPDATE event_person SET event_id=#{eventId}, person_id=#{personId}, role=#{role} WHERE id=#{id}")
    int update(EventPersonPojo eventPerson);
    
    /**
     * 根据ID删除事件-人物关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM event_person WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据事件ID和人物ID删除关联记录
     * @param eventId 事件ID
     * @param personId 人物ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM event_person WHERE event_id = #{eventId} AND person_id = #{personId}")
    int deleteByEventIdAndPersonId(@Param("eventId") Integer eventId, @Param("personId") Integer personId);
}