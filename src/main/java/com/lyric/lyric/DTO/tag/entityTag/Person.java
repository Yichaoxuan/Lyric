package com.lyric.lyric.DTO.tag.entityTag;

import com.lyric.lyric.Utils.stringProcessing.stringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人物请求DTO类
 * 只包含前端可信字段
 *
 * @author Yichuanxuan
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
    private List<String> alias;

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
     * 有参构造方法
     * @param id 主键ID
     * @param name 人物名称
     * @param alias 人物别称
     * @param relation 与用户的关系
     * @param personality 性格总结
     * @param color 颜色代码
     * @param firstAppearance 首次出现时间
     * @param lastAppearance 最后一次出现时间
     * @param importance 重要性等级
     */
    public Person(Integer id, String name, String alias, String relation, String personality, String color,
                  LocalDateTime firstAppearance, LocalDateTime lastAppearance, Integer appearanceCount,
                  ImportanceLevel importance) {
        this.id = id;
        this.name = name;
        this.alias = stringUtils.stringToList(alias);
        this.relation = relation;
        this.personality =  personality;
        this.color = color;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.appearanceCount = appearanceCount;
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