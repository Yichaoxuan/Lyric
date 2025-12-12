package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.EventPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 事件数据访问层接口
 */
@Mapper
public interface EventMapper {
    
    /**
     * 插入一条事件记录
     * @param event 事件实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO event(name, event_date, description, persons, importance) " +
            "VALUES(#{name}, #{eventDate}, #{description}, #{persons}, #{importance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EventPojo event);
    
    /**
     * 根据ID查询事件
     * @param id 事件ID
     * @return 事件实体
     */
    @Select("SELECT * FROM event WHERE id = #{id}")
    EventPojo selectById(Integer id);
    
    /**
     * 查询所有事件
     * @return 事件列表
     */
    @Select("SELECT * FROM event")
    List<EventPojo> selectAll();
    
    /**
     * 更新事件
     * @param event 事件实体
     * @return 影响的行数
     */
    @Update("UPDATE event SET name=#{name}, event_date=#{eventDate}, description=#{description}, persons=#{persons}, " +
            "importance=#{importance} WHERE id=#{id}")
    int update(EventPojo event);
    
    /**
     * 根据ID删除事件
     * @param id 事件ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM event WHERE id = #{id}")
    int deleteById(Integer id);
}