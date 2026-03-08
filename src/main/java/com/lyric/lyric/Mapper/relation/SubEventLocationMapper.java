package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.SubEventLocationPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 子事件-地点关联数据访问层接口
 * 对应数据库表: sub_event_location
 *
 * @author Yichaoxuan
 */
@Mapper
public interface SubEventLocationMapper {
    
    /**
     * 插入一条子事件-地点关联记录
     * @param subEventLocation 子事件-地点关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO sub_event_location(event_id, location_id) " +
            "VALUES(#{eventId}, #{locationId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SubEventLocationPojo subEventLocation);
    
    /**
     * 根据ID查询子事件-地点关联
     * @param id 关联ID
     * @return 子事件-地点关联实体
     */
    @Select("SELECT * FROM sub_event_location WHERE id = #{id}")
    SubEventLocationPojo selectById(Integer id);
    
    /**
     * 根据子事件ID查询所有关联的地点
     * @param eventId 子事件ID
     * @return 子事件-地点关联列表
     */
    @Select("SELECT * FROM sub_event_location WHERE event_id = #{eventId}")
    List<SubEventLocationPojo> selectByEventId(Integer eventId);
    
    /**
     * 根据地点ID查询所有关联的子事件
     * @param locationId 地点ID
     * @return 子事件-地点关联列表
     */
    @Select("SELECT * FROM sub_event_location WHERE location_id = #{locationId}")
    List<SubEventLocationPojo> selectByLocationId(Integer locationId);
    
    /**
     * 根据子事件ID和地点ID查询关联记录
     * @param eventId 子事件ID
     * @param locationId 地点ID
     * @return 子事件-地点关联实体
     */
    @Select("SELECT * FROM sub_event_location WHERE event_id = #{eventId} AND location_id = #{locationId}")
    SubEventLocationPojo selectByEventIdAndLocationId(@Param("eventId") Integer eventId, @Param("locationId") Integer locationId);
    
    /**
     * 查询所有子事件-地点关联
     * @return 子事件-地点关联列表
     */
    @Select("SELECT * FROM sub_event_location")
    List<SubEventLocationPojo> selectAll();
    
    /**
     * 更新子事件-地点关联
     * @param subEventLocation 子事件-地点关联实体
     * @return 影响的行数
     */
    @Update("UPDATE sub_event_location SET event_id=#{eventId}, location_id=#{locationId} WHERE id=#{id}")
    int update(SubEventLocationPojo subEventLocation);
    
    /**
     * 根据ID删除子事件-地点关联
     * @param id 关联ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM sub_event_location WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据子事件ID和地点ID删除关联记录
     * @param eventId 子事件ID
     * @param locationId 地点ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM sub_event_location WHERE event_id = #{eventId} AND location_id = #{locationId}")
    int deleteByEventIdAndLocationId(@Param("eventId") Integer eventId, @Param("locationId") Integer locationId);
}