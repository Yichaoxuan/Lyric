package com.lyric.lyric.DTO.relation;

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
     * 置信度
     */
    private Double confidence;
    /**
     * 有参构造方法
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param eventId 事件ID
     * @param confidence 置信度
     */
    public DiaryEvent(Integer id, Integer diaryId, Integer eventId, Double confidence) {
        this.id = id;
        this.diaryId = diaryId;
        this.eventId = eventId;
        this.confidence = confidence;
    }
}