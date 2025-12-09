package com.lyric.lyric.Pojo.AI; // 请根据您的项目结构调整包路径

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * AI生成的标签与实体摘要JSON对应的实体类
 * 用于映射复杂的嵌套JSON结构
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AITagJson {

    /**
     * 总结描述
     */
    private String summary;

    /**
     * 所有标签集合
     */
    private Labels labels;

    /**
     * 标签集合内部类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Labels {
        /**
         * 基础标签（主题和心情）
         */
        private Tag tag;

        /**
         * 实体标签（人物、地点、事件）
         */
        private EntityTag entityTag;
    }

    /**
     * 基础标签内部类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tag {
        /**
         * 主题标签列表
         */
        private List<ThemeLabel> themes;

        /**
         * 心情标签列表
         */
        private List<MoodLabel> moods;
    }

    /**
     * 主题标签内部类
     * 对应JSON中 themes 数组里的对象
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeLabel {
        /**
         * 主题名称
         */
        private String name;

        /**
         * 颜色代码
         */
        private String color;
    }

    /**
     * 心情标签内部类
     * 对应JSON中 moods 数组里的对象
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoodLabel {
        /**
         * 心情名称
         */
        private String name;

        /**
         * 颜色代码
         */
        private String color;

        /**
         * Emoji表情
         */
        private String icon;
    }

    /**
     * 实体标签内部类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityTag {
        /**
         * 人物实体映射
         * Key: 人物名称 (如 "张三")
         * Value: 该人物的详细信息
         */
        private Map<String, PersonInfo> person;

        /**
         * 地点实体映射
         * Key: 地点名称 (如 "北京故宫")
         * Value: 该地点的详细信息
         */
        private Map<String, LocationInfo> location;

        /**
         * 事件实体映射
         * Key: 事件名称 (如 "毕业典礼")
         * Value: 该事件的详细信息
         */
        private Map<String, EventInfo> event;
    }

    /**
     * 人物信息内部类
     * 对应JSON中 person 对象下每个键对应的值
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonInfo {
        /**
         * 与作者的关系
         */
        private String relationship;

        /**
         * 性别
         */
        private String gender;

        /**
         * 性格总结
         */
        private String personality;

        /**
         * 颜色代码
         */
        private String color;
    }

    /**
     * 地点信息内部类
     * 对应JSON中 location 对象下每个键对应的值
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        /**
         * 地点描述
         */
        private String description;

        /**
         * 颜色代码
         */
        private String color;
    }

    /**
     * 事件信息内部类
     * 对应JSON中 event 对象下每个键对应的值
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventInfo {

        /**
         * 事件发生地点
         */
        private String location;

        /**
         * 事件发生日期 (格式: YYYY-MM-DD)
         */
        private String date;

        /**
         * 事件描述
         */
        private String description;

        /**
         * 参与事件的人物及其角色映射
         * Key: 关联人物名称
         * Value: 在事件中的角色
         */
        private Map<String, String> persons;

        /**
         * 颜色代码
         */
        private String color;
    }
}