package com.lyric.lyric.Pojo.diaryevent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-事件关联实体类
 * 对应数据库表: diary_event
 *
 * @author Lyric
 * @since 2025-11-21
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEventPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 事件ID
     */
    private Integer eventId;

    /**
     * AI生成置信度
     */
    private Double confidence;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param eventId 事件ID
     * @param confidence AI生成置信度
     */
    public DiaryEventPojo(Integer id, Integer diaryId, Integer eventId, Double confidence) {
        this.id = id;
        this.diaryId = diaryId;
        this.eventId = eventId;
        this.confidence = confidence;
    }
}