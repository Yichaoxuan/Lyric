package com.lyric.lyric.POJO.tag;

import com.lyric.lyric.POJO.AI.AITagJson;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签实体类
 * 对应数据库表: tag
 * 包含主题标签和心情标签两种类型
 *
 * @author Lyric
 */
@Data
@NoArgsConstructor
public class BaseTagPojo {

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
     * 使用次数
     */
    private Integer usageCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 由AITagJson.ThemeLabel或MoodLabel转换为TagPojo对象
     * @param themeTag AITagJson.ThemeLabel对象
     */
    public BaseTagPojo(AITagJson.ThemeTag themeTag) {
        this.name = themeTag.getName();
        this.tagType = TagType.THEME;
        this.color = themeTag.getColor();
        this.icon = null;
        this.usageCount = 1;
        this.createdAt = null;
    }

    /**
     * 由AITagJson.MoodLabel转换为TagPojo对象
     * @param moodTag AITagJson.MoodLabel对象
     */
    public BaseTagPojo(AITagJson.MoodTag moodTag) {
        this.name = moodTag.getName();
        this.tagType = TagType.MOOD;
        this.color = moodTag.getColor();
        this.icon = moodTag.getIcon();
        this.usageCount = 0;
        this.createdAt = null;
    }

    /**
     * 标签类型枚举
     * 枚举值：THEME(主题标签), MOOD（心情标签）
     */
    @Getter
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
