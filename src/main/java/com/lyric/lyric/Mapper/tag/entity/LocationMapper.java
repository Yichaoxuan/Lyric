package com.lyric.lyric.Mapper.tag.entity;

import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 地点数据访问层接口
 *
 * @author Yichaoxuan
 * @since 2026-02-01
 */
@Mapper
public interface LocationMapper {

        /**
         * 插入一条地点记录
         *
         * @param location 地点实体
         * @return 影响的行数
         */
        @Insert("INSERT INTO location(name, alias, description, color, longitude, latitude, district, city, province, country, geo_hash, first_appearance, last_appearance, appearance_count, importance) "
                        +
                        "VALUES(#{name}, #{alias}, #{description}, #{color}, #{longitude}, #{latitude}, #{district}, #{city}, #{province}, #{country}, #{geoHash}, #{firstAppearance}, #{lastAppearance}, #{appearanceCount}, #{importance})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(LocationPojo location);

        /**
         * 根据ID查询地点
         *
         * @param id 地点ID
         * @return 地点实体
         */
        @Select("SELECT * FROM location WHERE id = #{id}")
        LocationPojo selectById(Integer id);

        /**
         * 根据名称查询地点
         *
         * @param name 地点名称
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE name = #{name}")
        List<LocationPojo> selectByName(String name);

        /**
         * 根据别名查询地点
         *
         * @param alias 地点别名
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE alias = #{alias}")
        List<LocationPojo> selectByAlias(String alias);

        /**
         * 根据经度查询地点
         *
         * @param longitude 经度
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE longitude = #{longitude}")
        List<LocationPojo> selectByLongitude(Double longitude);

        /**
         * 根据纬度查询地点
         *
         * @param latitude 纬度
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE latitude = #{latitude}")
        List<LocationPojo> selectByLatitude(Double latitude);

        /**
         * 根据区县查询地点
         *
         * @param district 区县
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE district = #{district}")
        List<LocationPojo> selectByDistrict(String district);

        /**
         * 根据城市查询地点
         *
         * @param city 城市
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE city = #{city}")
        List<LocationPojo> selectByCity(String city);

        /**
         * 根据省份查询地点
         *
         * @param province 省份
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE province = #{province}")
        List<LocationPojo> selectByProvince(String province);

        /**
         * 根据国家查询地点
         *
         * @param country 国家
         * @return 地点列表
         */
        @Select("SELECT * FROM location WHERE country = #{country}")
        List<LocationPojo> selectByCountry(String country);

        /**
         * 查询所有地点
         *
         * @return 地点列表
         */
        @Select("SELECT * FROM location")
        List<LocationPojo> selectAll();

        /**
         * 更新地点
         *
         * @param location 地点实体
         * @return 影响的行数
         */
        @Update("UPDATE location SET name=#{name}, alias=#{alias}, description=#{description}, color=#{color}, longitude=#{longitude}, latitude=#{latitude}, "
                        +
                        "district=#{district}, city=#{city}, province=#{province}, country=#{country}, geo_hash=#{geoHash}, first_appearance=#{firstAppearance}, last_appearance=#{lastAppearance}, "
                        +
                        "appearance_count=#{appearanceCount}, importance=#{importance} WHERE id=#{id}")
        int update(LocationPojo location);

        /**
         * 根据ID删除地点
         *
         * @param id 地点ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM location WHERE id = #{id}")
        int deleteById(Integer id);

        /**
         * 根据关键词模糊搜索地点
         *
         * @param keyword 搜索关键词
         * @return 匹配的地点列表
         */
        @Select("SELECT * FROM location WHERE name LIKE '%' || #{keyword} || '%' OR alias LIKE '%' || #{keyword} || '%'")
        List<LocationPojo> searchByKeyword(String keyword);
}