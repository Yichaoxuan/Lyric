package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.Pojo.relation.EventLocationPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 事件-地点关联数据访问层接口
 */
@Mapper
public interface EventLocationMapper {
    
    /**
     * 插入一条事件-地点关联记录
     * @param eventLocation 事件-地点关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO event_location(event_id, location_id) " +
            "VALUES(#{eventId}, #{locationId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EventLocationPojo eventLocation);
    
    /**
     * 根据ID查询事件-地点关联
     * @param id 关联ID
     * @return 事件-地点关联实体
     */
    @Select("SELECT * FROM event_location WHERE id = #{id}")
    EventLocationPojo selectById(Long id);
    
    /**
     * 根据事件ID查询所有关联的地点
     * @param eventId 事件ID
     * @return 事件-地点关联列表
     */
    @Select("SELECT * FROM event_location WHERE event_id = #{eventId}")
    List<EventLocationPojo> selectByEventId(Long eventId);
    
    /**
     * 查询所有事件-地点关联
     * @return 事件-地点关联列表
     */
    @Select("SELECT * FROM event_location")
    List<EventLocationPojo> selectAll();
    
    /**
     * 更新事件-地点关联
     * @param eventLocation 事件-地点关联实体
     * @return 影响的行数
     */
    @Update("UPDATE event_location SET event_id=#{eventId}, location_id=#{locationId} WHERE id=#{id}")
    int update(EventLocationPojo eventLocation);
    
    /**
     * 根据ID删除事件-地点关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM event_location WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 根据事件ID和地点ID删除关联记录
     * @param eventId 事件ID
     * @param locationId 地点ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM event_location WHERE event_id = #{eventId} AND location_id = #{locationId}")
    int deleteByEventIdAndLocationId(@Param("eventId") Long eventId, @Param("locationId") Long locationId);
}