package com.lyric.lyric.Dto.tag.entityTag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 事件请求DTO类
 * 只包含前端可信字段
 *
 * @author Yichaoxuan
 */
@Getter
@Setter
@NoArgsConstructor
public class Event {

    /**
     * 主键ID
     */
    private Integer id;

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
     * 参与事件的人物及其角色
     */
    private Map<String, String> persons;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 有参构造方法
     * @param id 主键ID
     * @param name 事件名称
     * @param eventDate 事件日期
     * @param description 事件描述
     * @param importance 重要性等级
     */
    public Event(Integer id, String name, LocalDateTime eventDate, String description, Map<String, String> persons, ImportanceLevel importance) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.description = description;
        this.persons = persons;
        this.importance = importance;
    }

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
}