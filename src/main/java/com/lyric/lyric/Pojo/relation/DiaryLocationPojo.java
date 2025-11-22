package com.lyric.lyric.Pojo.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-地点关联实体类
 * 对应数据库表: diary_location
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryLocationPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 地点ID
     */
    private Long locationId;

    /**
     * 定位来源
     */
    private LocationSource locationSource;

    /**
     * 定位置信度
     */
    private Double confidence;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 定位来源枚举
     */
    public enum LocationSource {
        /**
         * AI定位
         */
        AI,

        /**
         * 手动定位
         */
        MANUAL
    }

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param diaryId 日记ID
     * @param locationId 地点ID
     * @param locationSource 定位来源
     * @param confidence 定位置信度
     */
    public DiaryLocationPojo(Long diaryId, Long locationId, LocationSource locationSource, Double confidence) {
        this.diaryId = diaryId;
        this.locationId = locationId;
        this.locationSource = locationSource;
        this.confidence = confidence;
    }
}