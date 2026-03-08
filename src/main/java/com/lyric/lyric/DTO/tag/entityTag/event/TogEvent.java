package com.lyric.lyric.DTO.tag.entityTag.event;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 父事件DTO类
 * 对应TogEventPojo实体类，移除了主键和外键字段
 *
 * @author Yichaoxuan
 */
@Getter
@Setter
@NoArgsConstructor
public class TogEvent {
    
    /**
     * 父事件名称
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
     * 有参构造方法
     * @param name 父事件名称
     * @param startDate 整个事件开始的日期
     * @param endDate 整个事件结束的日期
     * @param description 整个事件的描述
     * @param importance 重要性
     * @param color 颜色代码
     */
    public TogEvent(String name, LocalDate startDate, LocalDate endDate, String description, ImportanceLevel importance, String color) {
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