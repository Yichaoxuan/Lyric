package com.lyric.lyric.POJO.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 待处理天气的日记信息实体类
 * <p>
 * 用于存储未关联天气信息的日记相关信息，包括：
 * </p>
 * <ul>
 *     <li>日记 ID</li>
 *     <li>地点的经纬度</li>
 *     <li>城市名称</li>
 *     <li>日记日期</li>
 * </ul>
 *
 * @author Yichaoxuan
 * @since 2026-03-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryWeatherPending {

    /**
     * 日记 ID
     */
  private Integer diaryId;

    /**
     * 纬度 (包装类型，允许 null)
     */
  private Double latitude;

    /**
     * 经度 (包装类型，允许 null)
     */
  private Double longitude;

    /**
     * 城市名称
     */
  private String city;

    /**
     * 日记日期
     */
  private LocalDate diaryDate;
}
