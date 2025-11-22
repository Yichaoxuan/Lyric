package com.lyric.lyric.Pojo.environment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 天气信息实体类
 * 对应数据库表: weather
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeatherPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联的日记ID
     */
    private Long diaryId;

    /**
     * 城市
     */
    private String city;

    /**
     * 天气日期
     */
    private LocalDate weatherDate;

    /**
     * 天气状况
     */
    private String weatherCondition;

    /**
     * 温度
     */
    private Double temperature;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param diaryId 关联的日记ID
     * @param city 城市
     * @param weatherDate 天气日期
     * @param weatherCondition 天气状况
     * @param temperature 温度
     */
    public WeatherPojo(Long diaryId, String city, LocalDate weatherDate, String weatherCondition, Double temperature) {
        this.diaryId = diaryId;
        this.city = city;
        this.weatherDate = weatherDate;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
    }
}