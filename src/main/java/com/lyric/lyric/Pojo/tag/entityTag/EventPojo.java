package com.lyric.lyric.Pojo.tag.entityTag;

import com.lyric.lyric.Pojo.AI.AITagJson;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import com.lyric.lyric.Utils.stringProcessing.stringUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 事件实体类
 * 对应数据库表: event
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
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
     * 事件日期
     */
    private LocalDate eventDate;

    /**
     * 事件描述
     */
    private String description;

    /**
     * 参与事件的人物及其角色
     */
    private String persons;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 数据库映射构造
     */
    public EventPojo(Integer id, String name, LocalDate eventDate, String description, String persons, ImportanceLevel importance, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.eventDate = eventDate;
        this.description = description;
        this.persons = persons;
        this.importance = importance;
        this.createdAt = createdAt;
    }

    /**
     * 由AITagJson.EventInfo转换为EventPojo对象
     * @param name 事件名称
     * @param event AITagJson.EventInfo对象
     */
    public EventPojo(String name, AITagJson.EventInfo event) {
        this.name = name;
        this.eventDate = DateTimeUtils.parseDate(event.getDate());
        this.description = event.getDescription();
        this.persons = stringUtils.mapToString(event.getPersons());
        this.importance = ImportanceLevel.MEDIUM;
        this.createdAt = null;
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