package com.lyric.lyric.Pojo.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类
 * 对应数据库表: tag
 * 包含主题标签和心情标签两种类型
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型
     */
    private TagType tagType;

    /**
     * 标签颜色 (十六进制)
     */
    private String color;

    /**
     * 表情图标
     */
    private String icon;

    /**
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 标签类型枚举
     */
    public enum TagType {
        /**
         * 主题标签
         */
        THEME,

        /**
         * 心情标签
         */
        MOOD
    }

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param name 标签名称
     * @param tagType 标签类型
     * @param color 标签颜色
     * @param icon 表情图标
     * @param usageCount 使用次数
     */
    public TagPojo(String name, TagType tagType, String color, String icon, Integer usageCount) {
        this.name = name;
        this.tagType = tagType;
        this.color = color;
        this.icon = icon;
        this.usageCount = usageCount;
    }
}