package com.lyric.lyric.Dto.environment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * 日记ID
     */
    private Long diaryId;

    /**
     * 城市
     */
    private String city;

    /**
     * 天气日期
     */
    private String weatherDate;

    /**
     * 天气状况
     */
    private String weatherCondition;

    /**
     * 温度
     */
    private String temperature;
    /**
     * 有参构造方法
     * @param diaryId 日记ID
     * @param city 城市
     * @param weatherDate 天气日期
     * @param weatherCondition 天气状况
     * @param temperature 温度
     */
    public Weather(Long diaryId, String city, String weatherDate, String weatherCondition, String temperature) {
        this.diaryId = diaryId;
        this.city = city;
        this.weatherDate = weatherDate;
        this.weatherCondition = weatherCondition;
        this.temperature = temperature;
    }
}