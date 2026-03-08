package com.lyric.lyric.POJO.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate appearanceDate;
}