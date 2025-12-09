package com.lyric.lyric.Dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 标签请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    /**
     * 主键ID
     */
    private Integer id;

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
     * 图标
     */
    private String icon;

    /**
     * 有参构造方法
     * @param id 主键ID
     * @param name 标签名称
     * @param tagType 标签类型
     * @param color 颜色
     * @param icon 图标
     */
    public Tag(Integer id, String name, TagType tagType, String color, String icon) {
        this.id = id;
        this.name = name;
        this.tagType = tagType;
        this.color = color;
        this.icon = icon;
    }

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
}