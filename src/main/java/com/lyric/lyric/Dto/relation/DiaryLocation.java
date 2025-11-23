package com.lyric.lyric.Dto.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日记-地点关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class DiaryLocation {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 地点ID
     */
    private Integer locationId;

    /**
     * 地点来源
     */
    private String locationSource;

    /**
     * 置信度
     */
    private Double confidence;
    /**
     * 有参构造方法
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param locationId 地点ID
     * @param locationSource 地点来源
     * @param confidence 置信度
     */
    public DiaryLocation(Integer id, Integer diaryId, Integer locationId, String locationSource, Double confidence) {
        this.id = id;
        this.diaryId = diaryId;
        this.locationId = locationId;
        this.locationSource = locationSource;
        this.confidence = confidence;
    }
}