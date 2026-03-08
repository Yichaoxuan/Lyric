package com.lyric.lyric.POJO.tag.entityTag.event;

import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 子事件实体类
 * 对应数据库表: sub_event
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubEventPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 父事件Id
     */
    private Integer togEventId;

    /**
     * 子事件名称
     */
    private String name;

    /**
     * 子事件发生日期
     */
    private LocalDate eventDate;

    /**
     * 子事件描述
     */
    private String description;

    /**
     * 子事件重要性
     */
    private ImportanceLevel importance;

    /**
     * 颜色代码
     */
    private String color;

    /**
     * 数据库映射构造
     */
    public SubEventPojo(Integer id, String name, LocalDate eventDate, String description, ImportanceLevel importance, String color) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.description = description;
        this.importance = importance;
        this.color = color;
    }

    /**
     * 由AITagJson.SubEventInfo转换为SubEventPojo对象
     *
     * @param togEventId 父事件ID
     * @param name       子事件名称
     * @param event      AITagJson.SubEventInfo对象
     */
    public SubEventPojo(Integer togEventId, String name, AITagJson.SubEventInfo event) {
        this.togEventId = togEventId;
        this.name = name;
        this.eventDate = DateTimeUtils.parseDate(event.getDate());
        this.description = event.getDescription();
        this.importance = ImportanceLevel.MEDIUM;
        this.color = event.getColor();
    }

    /**
     * 重要性等级枚举
     */
    @Getter
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