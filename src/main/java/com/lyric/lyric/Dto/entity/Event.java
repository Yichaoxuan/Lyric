package com.lyric.lyric.Dto.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 事件请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class Event {

    /**
     * 事件名称
     */
    private String name;

    /**
     * 事件日期
     */
    private LocalDateTime eventDate;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

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
     * 有参构造方法
     * @param name 事件名称
     * @param eventDate 事件日期
     * @param description 事件描述
     * @param importance 重要性等级
     */
    public Event(String name, LocalDateTime eventDate, String description, ImportanceLevel importance) {
        this.name = name;
        this.eventDate = eventDate;
        this.description = description;
        this.importance = importance;
    }
}