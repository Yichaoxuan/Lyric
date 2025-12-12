package com.lyric.lyric.DTO.weather;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 天气请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class Weather {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 城市
     */
    private String city;

    /**
     * 天气日期
     */
    private LocalDateTime weatherDate;

    /**
     * 天气状况
     */
    private String weatherCondition;

    /**
     * 温度
     */
    private Double temperature;
    
    /**
     * 有参构造方法
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param city 城市
     * @param weatherDate 天气日期
     * @param weatherCondition 天气状况
     * @param temperature 温度
     */
    public Weather(Integer id, Integer diaryId, String city, LocalDateTime weatherDate, String weatherCondition, Double temperature) {
        this.id = id;
        this.diaryId = diaryId;
        this.city = city;
        this.weatherDate = weatherDate;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
    }
}