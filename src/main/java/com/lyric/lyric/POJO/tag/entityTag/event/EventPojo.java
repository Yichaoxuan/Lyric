package com.lyric.lyric.POJO.tag.entityTag.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * 事件实体类
 * 对应数据库表: event
 *
 * @author Yichaoxuan
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 事件名称
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

    public EventPojo(String name, LocalDate startDate, LocalDate endDate, String description, ImportanceLevel importance, String color) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.importance = importance;
        this.color = color;
    }

    /**
     * 重要性等级枚举
     */
    public enum ImportanceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}