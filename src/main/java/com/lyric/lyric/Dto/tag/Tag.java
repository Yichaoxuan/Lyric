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
     * 标签名称
     */
    private String name;

    /**
     * 标签类型
     */
    private TagType tagType;

    /**
     * 颜色
     */
    private String color;

    /**
     * 图标
     */
    private String icon;

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
     * 有参构造方法
     * @param name 标签名称
     * @param tagType 标签类型
     * @param color 颜色
     * @param icon 图标
     */
    public Tag(String name, TagType tagType, String color, String icon) {
        this.name = name;
        this.tagType = tagType;
        this.color = color;
        this.icon = icon;
    }
}
