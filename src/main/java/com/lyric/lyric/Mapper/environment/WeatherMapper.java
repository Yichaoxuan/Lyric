package com.lyric.lyric.Mapper.environment;

import com.lyric.lyric.POJO.weather.WeatherPojo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 天气数据访问层接口
 * 对应数据库表: weather
 *
 * @author Yichaoxuan
 * @since 2026-03-09
 */
@Mapper
public interface WeatherMapper {
    
    /**
     * 插入一条天气记录
     * @param weather 天气实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO weather(location_id, weather_date, weather_condition, temp, temp_max, temp_min, weather_icon) " +
            "VALUES(#{locationId}, #{weatherDate}, #{weatherCondition}, #{temp}, #{tempMax}, #{tempMin}, #{weatherIcon})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WeatherPojo weather);
    
    /**
     * 根据ID查询天气
     * @param id 天气ID
     * @return 天气实体
     */
    @Select("SELECT * FROM weather WHERE id = #{id}")
    WeatherPojo selectById(Integer id);
    
    /**
     * 根据地点ID查询天气
     * @param locationId 地点ID
     * @return 天气实体
     */
    @Select("SELECT * FROM weather WHERE location_id = #{locationId}")
    List<WeatherPojo> selectByDiaryId(Integer locationId);
    
    /**
     * 根据天气日期查询天气记录
     * @param weatherDate 天气日期
     * @return 天气列表
     */
    @Select("SELECT * FROM weather WHERE weather_date = #{weatherDate}")
    List<WeatherPojo> selectByWeatherDate(LocalDate weatherDate);

    /**
     * 根据天气日期和地点ID查询天气记录
     * @param locationId 地点ID
     * @param weatherDate 天气日期
     * @return 天气实体
     */
    @Select("SELECT * FROM weather WHERE location_id = #{locationId} AND weather_date = #{weatherDate}")
    WeatherPojo selectByDiaryIdAndWeatherDate(Integer locationId, LocalDateTime weatherDate);
    
    /**
     * 根据天气状况查询天气记录
     * @param weatherCondition 天气状况
     * @return 天气列表
     */
    @Select("SELECT * FROM weather WHERE weather_condition = #{weatherCondition}")
    List<WeatherPojo> selectByWeatherCondition(String weatherCondition);
    
    /**
     * 查询所有天气记录
     * @return 天气列表
     */
    @Select("SELECT * FROM weather")
    List<WeatherPojo> selectAll();
    
    /**
     * 更新天气记录
     * @param weather 天气实体
     * @return 影响的行数
     */
    @Update("UPDATE weather SET location_id=#{locationId}, weather_date=#{weatherDate}, " +
            "weather_condition=#{weatherCondition},temp=#{temp}, temp_max=#{tempMax}, temp_min=#{tempMin}, weather_icon=#{weatherIcon} WHERE id=#{id}")
    int update(WeatherPojo weather);
    
    /**
     * 根据ID删除天气记录
     * @param id 天气ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM weather WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据地点ID和日期删除天气记录
     *
     * @param locationId  地点ID
     * @param weatherDate 天气日期
     */
    @Delete("DELETE FROM weather WHERE location_id = #{locationId} AND weather_date = #{weatherDate}")
    void deleteByLocationIdAndWeatherDate(Integer locationId, LocalDateTime weatherDate);
}