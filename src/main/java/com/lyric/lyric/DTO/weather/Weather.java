package com.lyric.lyric.DTO.weather;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 天气请求DTO类
 * 只包含前端可信字段
 *
 * @author Yichaoxuan
 * @since 2026-03-09
 */
@Data
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
     * 最高温度
     */
    private Double temp_max;

    /**
     * 最低温度
     */
    private Double temp_min;

    /**
     * 天气图标
     */
    private String weatherIcon;
}