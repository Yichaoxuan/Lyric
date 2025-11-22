package com.lyric.lyric.Pojo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 人物实体类
 * 对应数据库表: person
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PersonPojo {

    /**
     * 主键ID
     */
    private Long id;

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
     * @param name 人物名称
     * @param alias 人物别称
     * @param relation 与用户的关系
     * @param tags 自定义标签
     * @param firstAppearance 首次出现时间
     * @param lastAppearance 最后一次出现时间
     * @param appearanceCount 出现次数
     * @param importance 重要性等级
     */
    public PersonPojo(String name, String alias, String relation, String tags,
                  LocalDateTime firstAppearance, LocalDateTime lastAppearance,
                  Integer appearanceCount, ImportanceLevel importance) {
        this.name = name;
        this.alias = alias;
        this.relation = relation;
        this.tags = tags;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.appearanceCount = appearanceCount;
        this.importance = importance;
    }
}