package com.lyric.lyric.Pojo.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-事件关联实体类
 * 对应数据库表: diary_event
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEventPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 事件ID
     */
    private Long eventId;

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
     * @param diaryId 日记ID
     * @param eventId 事件ID
     * @param confidence AI生成置信度
     */
    public DiaryEventPojo(Long diaryId, Long eventId, Double confidence) {
        this.diaryId = diaryId;
        this.eventId = eventId;
        this.confidence = confidence;
    }
}