package com.lyric.lyric.Mapper.relation;

import com.lyric.lyric.POJO.relation.SubEventPersonPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 子事件 - 人物关联数据访问层接口
 * 对应数据库表：sub_event_person
 *
 * @author Yichaoxuan
 */
@Mapper
public interface SubEventPersonMapper {
    
    /**
     * 插入一条子事件 - 人物关联记录
     * @param subEventPerson 子事件 - 人物关联实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO sub_event_person(event_id, person_id, role) " +
            "VALUES(#{togEventId}, #{personId}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SubEventPersonPojo subEventPerson);
    
    /**
     * 根据 ID 查询子事件 - 人物关联
     * @param id 关联 ID
     * @return 子事件 - 人物关联实体
     */
    @Select("SELECT * FROM sub_event_person WHERE id = #{id}")
    SubEventPersonPojo selectById(Integer id);
    
    /**
     * 根据子事件 ID 查询所有关联的人物
     * @param togEventId 子事件 ID
     * @return 子事件 - 人物关联列表
     */
    @Select("SELECT * FROM sub_event_person WHERE event_id = #{togEventId}")
    List<SubEventPersonPojo> selectByEventId(@Param("togEventId") Integer togEventId);
    
    /**
     * 根据人物 ID 查询所有关联的子事件
     * @param personId 人物 ID
     * @return 子事件 - 人物关联列表
     */
    @Select("SELECT * FROM sub_event_person WHERE person_id = #{personId}")
    List<SubEventPersonPojo> selectByPersonId(Integer personId);
    
    /**
     * 根据子事件 ID 和人物 ID 查询关联记录
     * @param togEventId 子事件 ID
     * @param personId 人物 ID
     * @return 子事件 - 人物关联实体
     */
    @Select("SELECT * FROM sub_event_person WHERE event_id = #{togEventId} AND person_id = #{personId}")
    SubEventPersonPojo selectByEventIdAndPersonId(@Param("togEventId") Integer togEventId, @Param("personId") Integer personId);
    
    /**
     * 查询所有子事件 - 人物关联
     * @return 子事件 - 人物关联列表
     */
    @Select("SELECT * FROM sub_event_person")
    List<SubEventPersonPojo> selectAll();
    
    /**
     * 更新子事件 - 人物关联
     * @param subEventPerson 子事件 - 人物关联实体
     * @return 影响的行数
     */
    @Update("UPDATE sub_event_person SET event_id=#{togEventId}, person_id=#{personId}, " +
            "role=#{role} WHERE id=#{id}")
    int update(SubEventPersonPojo subEventPerson);
    
    /**
     * 根据 ID 删除子事件 - 人物关联
     * @param id 关联 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM sub_event_person WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据子事件 ID 和人物 ID 删除关联记录
     * @param togEventId 子事件 ID
     * @param personId 人物 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM sub_event_person WHERE event_id = #{togEventId} AND person_id = #{personId}")
    int deleteByEventIdAndPersonId(@Param("togEventId") Integer togEventId, @Param("personId") Integer personId);
}
