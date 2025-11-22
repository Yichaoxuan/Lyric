package com.lyric.lyric.Dto.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日记-事件关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class DiaryEvent {

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 置信度
     */
    private Double confidence;
    /**
     * 有参构造方法
     * @param diaryId 日记ID
     * @param eventId 事件ID
     * @param confidence 置信度
     */
    public DiaryEvent(Long diaryId, Long eventId, Double confidence) {
        this.diaryId = diaryId;
        this.eventId = eventId;
        this.confidence = confidence;
    }
}