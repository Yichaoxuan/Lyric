package com.lyric.lyric.Mapper.environment;

import com.lyric.lyric.POJO.weather.WeatherPojo;
import org.apache.ibatis.annotations.*;

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
    @Insert("INSERT INTO weather(diary_id, city, weather_date, weather_condition, temp_max, temp_min, weather_icon) " +
            "VALUES(#{diaryId}, #{city}, #{weatherDate}, #{weatherCondition}, #{tempMax}, #{tempMin}, #{weatherIcon})")
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
     * 根据日记ID查询天气
     * @param diaryId 日记ID
     * @return 天气实体
     */
    @Select("SELECT * FROM weather WHERE diary_id = #{diaryId}")
    WeatherPojo selectByDiaryId(Integer diaryId);
    
    /**
     * 根据城市查询天气记录
     * @param city 城市名称
     * @return 天气列表
     */
    @Select("SELECT * FROM weather WHERE city = #{city}")
    List<WeatherPojo> selectByCity(String city);
    
    /**
     * 根据天气日期查询天气记录
     * @param weatherDate 天气日期
     * @return 天气列表
     */
    @Select("SELECT * FROM weather WHERE weather_date = #{weatherDate}")
    List<WeatherPojo> selectByWeatherDate(java.time.LocalDate weatherDate);
    
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
    @Update("UPDATE weather SET diary_id=#{diaryId}, city=#{city}, weather_date=#{weatherDate}, " +
            "weather_condition=#{weatherCondition}, temp_max=#{tempMax}, temp_min=#{tempMin}, weather_icon=#{weatherIcon} WHERE id=#{id}")
    int update(WeatherPojo weather);
    
    /**
     * 根据ID删除天气记录
     * @param id 天气ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM weather WHERE id = #{id}")
    int deleteById(Integer id);
    
    /**
     * 根据日记ID删除天气记录
     * @param diaryId 日记ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM weather WHERE diary_id = #{diaryId}")
    int deleteByDiaryId(Integer diaryId);
}