package com.lyric.lyric.Pojo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 事件实体类
 * 对应数据库表: event
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 事件日期
     */
    private LocalDate eventDate;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 出现次数
     */
    private Integer appearanceCount;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 重要性等级枚举
     */
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

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param name 事件名称
     * @param eventDate 事件日期
     * @param description 事件描述
     * @param appearanceCount 出现次数
     * @param importance 重要性等级
     */
    public EventPojo(String name, LocalDate eventDate, String description, Integer appearanceCount, ImportanceLevel importance) {
        this.name = name;
        this.eventDate = eventDate;
        this.description = description;
        this.appearanceCount = appearanceCount;
        this.importance = importance;
    }
}