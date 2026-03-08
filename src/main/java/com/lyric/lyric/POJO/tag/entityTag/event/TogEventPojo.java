package com.lyric.lyric.POJO.tag.entityTag.event;

import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 父事件实体类
 * 对应数据库表: tog_event
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
public class TogEventPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记Id,外键
     */
    private Integer diaryId;

    /**
     * 合事件名称
     */
    private String name;

    /**
     * 整个事件开始的日期
     */
    private LocalDate startDate;

    /**
     * 整个事件结束的日期
     */
    private LocalDate endDate;

    /**
     * 整个事件的描述
     */
    private String description;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 颜色代码
     */
    private String color;

    /**
     * 数据库映射构造
     */
    public TogEventPojo(Integer id, String name, LocalDate startDate, LocalDate endDate, String description, ImportanceLevel importance, String color) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.importance = importance;
        this.color = color;
    }

    /**
     * 由AITagJson.TogEventInfo转换为TogEventPojo对象
     */
    public TogEventPojo(Integer diaryId, String name,AITagJson.TogEventInfo togEvent) {
        this.diaryId = diaryId;
        this.name = name;
        this.startDate = DateTimeUtils.parseDate(togEvent.getStartDate());
        this.endDate = DateTimeUtils.parseDate(togEvent.getEndDate());
        this.description = togEvent.getDescription();
        this.color = togEvent.getColor();
    }

    public enum ImportanceLevel {
        /**
         * 高重要性
         */
        HIGH,

        /**
         * 中等重要性
         */
        MEDIUM,

        /**
         * 低重要性
         */
        LOW
    }
}
