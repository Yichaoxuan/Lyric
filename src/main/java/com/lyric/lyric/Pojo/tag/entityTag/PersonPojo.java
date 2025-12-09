package com.lyric.lyric.Pojo.tag.entityTag;

import com.lyric.lyric.Pojo.AI.AITagJson;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 人物实体类
 * 对应数据库表: person
 *
 * @author Lyric
 */
@Data
@NoArgsConstructor
public class PersonPojo {

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
     * 性格总结
     */
    private String personality;

    /**
     * 颜色代码
     */
    private String color;

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
     * 数据库映射构造
     */
    public PersonPojo(Integer id, String name, String alias, String relation, String personality, String color, LocalDateTime firstAppearance, LocalDateTime lastAppearance, Integer appearanceCount, ImportanceLevel importance, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.relation = relation;
        this.personality = personality;
        this.color = color;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.appearanceCount = appearanceCount;
        this.importance = importance;
        this.createdAt = createdAt;
    }

    /**
     * 由AITagJson.personInfo 转换为PersonPojo对象
     *
     * @param name 人物名称
     * @param persons AITagJson.PersonInfo对象
     */
    public PersonPojo(String name, AITagJson.PersonInfo persons) {
        this.name = name;
        this.relation = persons.getRelationship();
        this.personality = persons.getPersonality();
        this.color = persons.getColor();
        this.firstAppearance = null;
        this.lastAppearance = null;
        this.appearanceCount = null;
        this.importance = null;
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