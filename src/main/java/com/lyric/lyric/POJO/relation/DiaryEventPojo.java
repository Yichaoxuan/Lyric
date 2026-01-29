package com.lyric.lyric.POJO.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-事件关联实体类
 * 对应数据库表: diary_event
 *
 * @author Yichaoxuan
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
     * 发生时间
     */
    private LocalDateTime appearanceDate;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param eventId 事件ID
     */
    public DiaryEventPojo(Integer id, Integer diaryId, Integer eventId) {
        this.id = id;
        this.diaryId = diaryId;
        this.eventId = eventId;
    }
}