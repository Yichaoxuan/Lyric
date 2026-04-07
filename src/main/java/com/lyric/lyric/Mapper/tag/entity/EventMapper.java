package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.event.EventPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;


/**
 * 事件数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-04-02
 */
@Mapper
public interface EventMapper {

        /**
         * 插入事件
         *
         * @param event  需要添加的事件POJO
         */
        @Insert("INSERT INTO event(name, start_date, end_date, description, importance, color) " +
                        "VALUES(#{name}, #{startDate}, #{endDate}, #{description}, #{importance}, #{color})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        void insert(EventPojo event);

        /**
         * 根据ID查询事件
         *
         * @param id     事件ID
         * @return       查询到的事件POJO
         */
        @Select("SELECT * FROM event WHERE id = #{id}")
        EventPojo selectEventById(Integer id);


        /**
         * 查询所有事件
         *
         * @return       所有事件POJO列表
         */
        @Select("SELECT * FROM event")
        List<EventPojo> selectAllEvents();


        /**
         * 更新事件
         *
         * @param event  需要更新的事件POJO
         * @return       更新成功返回1，否则返回0
         */
        @Update("UPDATE event SET name=#{name}, start_date=#{startDate}, end_date=#{endDate}, " +
                        "description=#{description}, importance=#{importance}, color=#{color} WHERE id=#{id}")
        int updateEvent(EventPojo event);


        /**
         * 删除事件
         *
         * @param id     事件ID
         * @return       删除成功返回1，否则返回0
         */
        @Delete("DELETE FROM event WHERE id = #{id}")
        int deleteEventById(Integer id);
}