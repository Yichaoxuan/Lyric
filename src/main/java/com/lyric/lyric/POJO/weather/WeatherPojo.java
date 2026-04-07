package com.lyric.lyric.POJO.weather;

import com.lyric.lyric.Service.weather.GetWeatherService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 天气信息实体类
 * 对应数据库表: weather
 *
 * @author Yichaoxuan
 * @since 2026-03-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 关联的地点ID
     */
    private Integer locationId;

    /**
     * 天气日期
     */
    private LocalDate weatherDate;

    /**
     * 天气状况
     */
    private String weatherCondition;

    /**
     * 最高温度
     */
    private Double tempMax;

    /**
     * 最低温度
     */
    private Double tempMin;

    /**
     * 天气图标
     */
    private String weatherIcon;

    /**
     * 创建一个 WeatherPojo 对象
     * @param locationId 关联的日记ID
     * @param weatherDate 天气日期
     * @param weatherInformation 天气信息
     */
    public WeatherPojo(Integer locationId, LocalDate weatherDate, GetWeatherService.WeatherInformation weatherInformation) {
        this.locationId = locationId;
        this.weatherDate = weatherDate;
        this.weatherCondition = weatherInformation.getWeatherCondition();
        this.tempMax = weatherInformation.getTempMax();
        this.tempMin = weatherInformation.getTempMin();
        this.weatherIcon = weatherInformation.getWeatherIcon();
    }
}