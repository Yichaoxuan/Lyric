package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 活动数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-04-02
 */
@Mapper
public interface ActivityMapper {

    /**
     * 插入一条活动记录
     *
     * @param activity 活动实体
     */
    @Insert("INSERT INTO activity(event_id, name, activity_date, time_period, description, importance, color) " +
            "VALUES(#{eventId}, #{name}, #{activityDate}, #{timePeriod}, #{description}, #{importance}, #{color})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ActivityPojo activity);

    /**
     * 根据ID查询活动
     *
     * @param id 活动ID
     * @return 活动实体
     */
    @Select("SELECT * FROM activity WHERE id = #{id}")
    ActivityPojo selectById(Integer id);

    /**
     * 根据事件ID查询活动
     *
     * @param eventId 事件ID
     * @return 活动列表
     */
    @Select("SELECT * FROM activity WHERE event_id = #{eventId}")
    List<ActivityPojo> selectByEventId(Integer eventId);

    /**
     * 查询所有活动
     *
     * @return 活动列表
     */
    @Select("SELECT * FROM activity")
    List<ActivityPojo> selectAll();

    /**
     * 更新活动
     *
     * @param activity 活动实体
     * @return 影响的行数
     */
    @Update("UPDATE activity SET event_id=#{eventId}, name=#{name}, activity_date=#{activityDate}, " +
            "time_period=#{timePeriod}, description=#{description}, importance=#{importance}, color=#{color} WHERE id=#{id}")
    int update(ActivityPojo activity);

    /**
     * 根据ID删除活动
     *
     * @param id 活动ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM activity WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 查询指定日期的所有活动
     *
     * @param date 日期（格式：YYYY-MM-DD）
     * @return 活动列表
     */
    @Select("SELECT * FROM activity WHERE DATE(activity_date) = DATE(#{date})")
    List<ActivityPojo> selectByDate(String date);
}
