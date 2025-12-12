package com.lyric.lyric.Mapper.environment;

import com.lyric.lyric.POJO.weather.WeatherPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 天气数据访问层接口
 */
@Mapper
public interface WeatherMapper {
    
    /**
     * 插入一条天气记录
     * @param weather 天气实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO weather(diary_id, city, weather_date, weather_condition, temperature) " +
            "VALUES(#{diaryId}, #{city}, #{weatherDate}, #{weatherCondition}, #{temperature})")
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
     * 查询所有天气记录
     * @return 天气列表
     */
    @Select("SELECT * FROM weather")
    List<WeatherPojo> selectAll();
    
    /**
     * 更新天气
     * @param weather 天气实体
     * @return 影响的行数
     */
    @Update("UPDATE weather SET diary_id=#{diaryId}, city=#{city}, weather_date=#{weatherDate}, " +
            "weather_condition=#{weatherCondition}, temperature=#{temperature} WHERE id=#{id}")
    int update(WeatherPojo weather);
    
    /**
     * 根据ID删除天气
     * @param id 天气ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM weather WHERE id = #{id}")
    int deleteById(Integer id);
}