package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.ActivityLocationPojo;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 活动地点数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Mapper
public interface ActivityLocationMapper {
    /**
     * 插入一条活动-地点关联记录
     *
     * @param activityLocation 活动-地点关联实体对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO activity_location(activity_id, location_id, mention_type) " +
            "VALUES(#{activityId}, #{locationId}, #{mentionType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ActivityLocationPojo activityLocation);

    /**
     * 根据ID查询活动-地点关联记录
     *
     * @param id 关联记录ID
     * @return 活动-地点关联实体对象，如果不存在则返回null
     */
    @Select("SELECT * FROM activity_location WHERE id = #{id}")
    ActivityLocationPojo selectById(Integer id);

    /**
     * 根据活动ID查询所有关联的地点
     *
     * @param activityId 活动ID
     * @return 活动-地点关联实体列表
     */
    @Select("SELECT * FROM activity_location WHERE activity_id = #{activityId}")
    List<ActivityLocationPojo> selectByActivityId(Integer activityId);

    /**
     * 根据活动ID查询所有关联的地点ID
     *
     * @param activityId 活动ID
     * @return 地点ID列表
     */
    @Select("SELECT location_id FROM activity_location WHERE activity_id = #{activityId}")
    List<Integer> selectLocationIdsByActivityId(Integer activityId);

    /**
     * 根据地点ID查询所有关联的活动记录
     *
     * @param locationId 地点ID
     * @return 活动-地点关联实体列表
     */
    @Select("SELECT * FROM activity_location WHERE location_id = #{locationId}")
    List<ActivityLocationPojo> selectByLocationId(Integer locationId);

    /**
     * 根据活动ID和地点ID查询特定的关联记录
     *
     * @param activityId 活动ID
     * @param locationId 地点ID
     * @return 活动-地点关联实体对象，如果不存在则返回null
     */
    @Select("SELECT * FROM activity_location WHERE activity_id = #{activityId} AND location_id = #{locationId}")
    ActivityLocationPojo selectByActivityIdAndLocationId(Integer activityId, Integer locationId);

    /**
     * 查询所有活动-地点关联记录
     *
     * @return 活动-地点关联实体列表
     */
    @Select("SELECT * FROM activity_location")
    List<ActivityLocationPojo> selectAll();

    /**
     * 更新活动-地点关联记录
     *
     * @param activityLocation 包含更新信息的活动-地点关联实体对象
     * @return 影响的行数
     */
    @Update("UPDATE activity_location SET activity_id=#{activityId}, location_id=#{locationId}, " +
            "mention_type=#{mentionType} WHERE id=#{id}")
    int update(ActivityLocationPojo activityLocation);

    /**
     * 根据ID删除活动-地点关联记录
     *
     * @param id 关联记录ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM activity_location WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据活动ID和地点ID删除特定的关联记录
     *
     * @param activityId 活动ID
     * @param locationId 地点ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM activity_location WHERE activity_id = #{activityId} AND location_id = #{locationId}")
    int deleteByActivityIdAndLocationId(Integer activityId, Integer locationId);
}
