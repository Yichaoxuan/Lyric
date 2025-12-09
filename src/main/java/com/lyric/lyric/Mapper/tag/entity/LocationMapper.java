package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.Pojo.tag.entityTag.LocationPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 地点数据访问层接口
 */
@Mapper
public interface LocationMapper {
    
    /**
     * 插入一条地点记录
     * @param location 地点实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO location(name, alias, description, color, longitude, latitude, city, geo_hash, first_appearance, last_appearance, appearance_count, importance) " +
            "VALUES(#{name}, #{alias}, #{description}, #{color}, #{longitude}, #{latitude}, #{city}, #{geoHash}, #{firstAppearance}, #{lastAppearance}, #{appearanceCount}, #{importance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LocationPojo location);
    
    /**
     * 根据ID查询地点
     * @param id 地点ID
     * @return 地点实体
     */
    @Select("SELECT * FROM location WHERE id = #{id}")
    LocationPojo selectById(Integer id);
    
    /**
     * 查询所有地点
     * @return 地点列表
     */
    @Select("SELECT * FROM location")
    List<LocationPojo> selectAll();
    
    /**
     * 更新地点
     * @param location 地点实体
     * @return 影响的行数
     */
    @Update("UPDATE location SET name=#{name}, alias=#{alias}, description=#{description}, color=#{color}, longitude=#{longitude}, latitude=#{latitude}, " +
            "city=#{city}, geo_hash=#{geoHash}, first_appearance=#{firstAppearance}, last_appearance=#{lastAppearance}, " +
            "appearance_count=#{appearanceCount}, importance=#{importance} WHERE id=#{id}")
    int update(LocationPojo location);
    
    /**
     * 根据ID删除地点
     * @param id 地点ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM location WHERE id = #{id}")
    int deleteById(Integer id);
}