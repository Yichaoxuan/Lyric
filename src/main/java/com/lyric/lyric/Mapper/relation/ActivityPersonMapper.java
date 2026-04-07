package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.ActivityPersonPojo;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 活动人物数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Mapper
public interface ActivityPersonMapper {
        /**
         * 插入一条活动-人物关联记录
         *
         * @param activityPerson 活动-人物关联实体对象
         * @return 影响的行数
         */
        @Insert("INSERT INTO activity_person(activity_id, person_id, role, mention_type) " +
                        "VALUES(#{activityId}, #{personId}, #{role}, #{mentionType})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(ActivityPersonPojo activityPerson);

        /**
         * 根据ID查询活动-人物关联记录
         *
         * @param id 关联记录ID
         * @return 活动-人物关联实体对象，如果不存在则返回null
         */
        @Select("SELECT * FROM activity_person WHERE id = #{id}")
        ActivityPersonPojo selectById(Integer id);

        /**
         * 根据活动ID查询所有关联的人物记录
         *
         * @param activityId 活动ID
         * @return 活动-人物关联实体列表
         */
        @Select("SELECT * FROM activity_person WHERE activity_id = #{activityId}")
        List<ActivityPersonPojo> selectByActivityId(Integer activityId);

        /**
         * 根据人物ID查询所有关联的活动记录
         *
         * @param personId 人物ID
         * @return 活动-人物关联实体列表
         */
        @Select("SELECT * FROM activity_person WHERE person_id = #{personId}")
        List<ActivityPersonPojo> selectByPersonId(Integer personId);

        /**
         * 根据活动ID和人物ID查询特定的关联记录
         *
         * @param activityId 活动ID
         * @param personId   人物ID
         * @return 活动-人物关联实体对象，如果不存在则返回null
         */
        @Select("SELECT * FROM activity_person WHERE activity_id = #{activityId} AND person_id = #{personId}")
        ActivityPersonPojo selectByActivityIdAndPersonId(Integer activityId, Integer personId);

        /**
         * 查询所有活动-人物关联记录
         *
         * @return 活动-人物关联实体列表
         */
        @Select("SELECT * FROM activity_person")
        List<ActivityPersonPojo> selectAll();

        /**
         * 更新活动-人物关联记录
         *
         * @param activityPerson 包含更新信息的活动-人物关联实体对象
         * @return 影响的行数
         */
        @Update("UPDATE activity_person SET activity_id=#{activityId}, person_id=#{personId}, " +
                        "role=#{role}, mention_type=#{mentionType} WHERE id=#{id}")
        int update(ActivityPersonPojo activityPerson);

        /**
         * 根据ID删除活动-人物关联记录
         *
         * @param id 关联记录ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM activity_person WHERE id = #{id}")
        int deleteById(Integer id);

        /**
         * 根据活动ID和人物ID删除特定的关联记录
         *
         * @param activityId 活动ID
         * @param personId   人物ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM activity_person WHERE activity_id = #{activityId} AND person_id = #{personId}")
        int deleteByActivityIdAndPersonId(Integer activityId, Integer personId);

        /**
         * 根据活动ID查询人物ID列表
         *
         * @param activityId 活动ID
         * @return 人物ID列表
         */
        @Select("SELECT person_id FROM activity_person WHERE activity_id = #{activityId}")
        List<Integer> selectPersonIdsByActivityId(Integer activityId);

        /**
         * 根据活动ID删除所有关联记录
         * 
         * @param activityId 活动ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM activity_person WHERE activity_id = #{activityId}")
        int deleteByActivityId(Integer activityId);
}
