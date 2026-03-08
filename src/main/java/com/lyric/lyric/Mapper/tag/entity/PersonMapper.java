package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 人物数据访问层接口
 *
 * @author Yichaoxuan
 */
@Mapper
public interface PersonMapper {

    /**
     * 插入一条人物记录
     * @param person 人物实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO person(name, alias, gender, relation, personality, color, first_appearance, last_appearance, appearance_count, importance) " +
            "VALUES(#{name}, #{alias}, #{gender}, #{relation}, #{personality}, #{color}, #{firstAppearance}, #{lastAppearance}, #{appearanceCount}, #{importance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PersonPojo person);

    /**
     * 根据ID查询人物
     * @param id 人物ID
     * @return 人物实体
     */
    @Select("SELECT * FROM person WHERE id = #{id}")
    PersonPojo selectById(Integer id);

    /**
     * 根据性别查询人物
     * @param gender 人物性别
     * @return 人物列表
     */
    @Select("SELECT * FROM person WHERE gender = #{gender}")
    List<PersonPojo> selectByGender(Integer gender);

    /**
     * 根据名称查询人物
     * @param name 人物名称
     * @return 人物实体
     */
    @Select("SELECT * FROM person WHERE name = #{name}")
    PersonPojo selectByName(String name);

    /**
     * 根据关系查询人物
     * @param relation 人物关系
     * @return 人物列表
     */
    @Select("SELECT * FROM person WHERE relation LIKE '%' || #{relation} || '%'")
    List<PersonPojo> selectByRelation(String relation);

    /**
     * 根据名称和关系查询人物
     * @param name 人物名称
     * @param relation 人物关系
     * @return 人物列表
     */
    @Select("SELECT * FROM person WHERE name = #{name} AND relation LIKE '%' || #{relation} || '%'")
    List<PersonPojo> selectByNameAndRelation(String name, String relation);

    /**
     * 查询所有人物
     * @return 人物列表
     */
    @Select("SELECT * FROM person")
    List<PersonPojo> selectAll();

    /**
     * 更新人物
     * @param person 人物实体
     * @return 影响的行数
     */
    @Update("UPDATE person SET name=#{name}, alias=#{alias}, gender=#{gender}, relation=#{relation}, personality=#{personality}, color=#{color}, " +
            "first_appearance=#{firstAppearance}, last_appearance=#{lastAppearance}, appearance_count=#{appearanceCount}, " +
            "importance=#{importance} WHERE id=#{id}")
    int update(PersonPojo person);

    /**
     * 根据ID删除人物
     * @param id 人物ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM person WHERE id = #{id}")
    int deleteById(Integer id);
}