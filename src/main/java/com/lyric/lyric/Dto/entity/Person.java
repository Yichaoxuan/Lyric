package com.lyric.lyric.Dto.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 人物请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class Person {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 人物名称
     */
    private String name;

    /**
     * 人物别称
     */
    private String alias;

    /**
     * 与用户的关系
     */
    private String relation;

    /**
     * 自定义标签 (JSON数组格式)
     */
    private String tags;

    /**
     * 首次出现时间
     */
    private LocalDateTime firstAppearance;

    /**
     * 最后一次出现时间
     */
    private LocalDateTime lastAppearance;

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
     * @param id 主键ID
     * @param name 人物名称
     * @param alias 人物别称
     * @param relation 与用户的关系
     * @param tags 自定义标签
     * @param firstAppearance 首次出现时间
     * @param lastAppearance 最后一次出现时间
     * @param importance 重要性等级
     */
    public Person(Integer id, String name, String alias, String relation, String tags,
                  LocalDateTime firstAppearance, LocalDateTime lastAppearance,
                  ImportanceLevel importance) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.relation = relation;
        this.tags = tags;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.importance = importance;
    }
}