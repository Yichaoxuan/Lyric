package com.lyric.lyric.Mapper.entity;

import com.lyric.lyric.Pojo.entity.PersonPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 人物数据访问层接口
 */
@Mapper
public interface PersonMapper {
    
    /**
     * 插入一条人物记录
     * @param person 人物实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO person(name, alias, relation, tags, first_appearance, last_appearance, appearance_count, importance) " +
            "VALUES(#{name}, #{alias}, #{relation}, #{tags}, #{firstAppearance}, #{lastAppearance}, #{appearanceCount}, #{importance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PersonPojo person);
    
    /**
     * 根据ID查询人物
     * @param id 人物ID
     * @return 人物实体
     */
    @Select("SELECT * FROM person WHERE id = #{id}")
    PersonPojo selectById(Long id);
    
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
    @Update("UPDATE person SET name=#{name}, alias=#{alias}, relation=#{relation}, tags=#{tags}, " +
            "first_appearance=#{firstAppearance}, last_appearance=#{lastAppearance}, appearance_count=#{appearanceCount}, " +
            "importance=#{importance} WHERE id=#{id}")
    int update(PersonPojo person);
    
    /**
     * 根据ID删除人物
     * @param id 人物ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM person WHERE id = #{id}")
    int deleteById(Long id);
}