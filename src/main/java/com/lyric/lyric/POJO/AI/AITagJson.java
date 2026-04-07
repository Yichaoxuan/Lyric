package com.lyric.lyric.POJO.AI; // 请根据您的项目结构调整包路径

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * AI生成的标签与实体摘要JSON对应的实体类
 * 用于映射复杂的嵌套JSON结构
 *
 * @author Yichaoxuan
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

    @Override
    public String toString() {
        return "AITagJson{" +
                "总结:'" + summary + "\n" +
                "标签:" + labels +  "\n" +
                "}" + "\n";
    }

    /**
     * 提及类型枚举，对应活动关联中的 mention_type
     */
    public enum MentionType {
        ACTUAL,   // 实际出现/到达
        MENTION,  // 提及
        MEMORY    // 回忆
    }

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
         * 实体标签（人物、地点、活动）
         */
        private EntityTag entityTag;

        @Override
        public String toString() {
            return "标签{" +
                    "基础标签：" + tag + "\n" +
                    "实体标签：" + entityTag + "\n" +
                    "}" + "\n";
        }
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
        private List<ThemeTag> themes;

        /**
         * 心情标签列表
         */
        private List<MoodTag> moods;

        @Override
        public String toString() {
            return "基础标签{" +
                    "主题标签：" + themes + "\n" +
                    "心情标签：" + moods + "\n" +
                    "}" + "\n";
        }
    }

    /**
     * 主题标签内部类
     * 对应JSON中 themes 数组里的对象
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThemeTag {
        /**
         * 主题名称
         */
        private String name;

        /**
         * 颜色代码
         */
        private String color;

        @Override
        public String toString() {
            return "{" +
                    "名称:'" + name + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    "}" + "\n";
        }
    }

    /**
     * 心情标签内部类
     * 对应JSON中 moods 数组里的对象
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoodTag {
        /**
         * 心情名称
         */
        private String name;

        /**
         *  心情等级
         */
        private String level;

        /**
         * 颜色代码
         */
        private String color;

        /**
         * Emoji表情
         */
        private String icon;

        @Override
        public String toString() {
            return "{" +
                    "名称:'" + name + "\n" +
                    ", 等级:'" + level + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    ", Emoji表情:'" + icon + "\n" +
                    "}" + "\n";
        }
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
         * 活动实体映射
         * Key: 活动名称 (如 "参观兵马俑")
         * Value: 该活动的详细信息
         */
        private Map<String, ActivityInfo> activity;

        @Override
        public String toString() {
            return "{" +
                    "人物实体映射：" + person + "\n" +
                    "地点实体映射：" + location + "\n" +
                    "活动实体映射：" + activity + "\n" +
                    "}" + "\n";
        }
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
         * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
         */
        private MentionType mentionType;

        /**
         * 颜色代码
         */
        private String color;

        /**
         * 人物索引（在日记或事件中的顺序）
         */
        private String index;

        @Override
        public String toString() {
            return "{" +
                    "关系:'" + relationship + "\n" +
                    ", 性别:'" + gender + "\n" +
                    ", 性格总结:'" + personality + "\n" +
                    ", 类型:'" + mentionType + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    "}" + "\n";
        }
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
         * 所在区县
         */
        private String district;

        /**
         * 所在城市
         */
        private String city;

        /**
         * 省份
         */
        private String province;

        /**
         * 所在国家
         */
        private String country;

        /**
         * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
         */
        private MentionType mentionType;

        /**
         * 颜色代码
         */
        private String color;

        /**
         * 地点到达顺序的索引
         */
        private String index;

        /**
         * 地点明确性标识
         * "0" 表示具有明确指代性（如天安门、大兴机场、廊坊师范学院等）
         * "1" 表示不具有明确指代性（如廊坊师范学院食堂、校医务室、学校西门烧烤摊等）
         */
        private String specificity;

        @Override
        public String toString() {
            return "{" +
                    "描述:'" + description + "\n" +
                    ", 所在区县:'" + district + "\n" +
                    ", 所在城市:'" + city + "\n" +
                    ", 省份:'" + province + "\n" +
                    ", 所在国家:'" + country + "\n" +
                    ", 类型:'" + mentionType + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    ", 明确性:'" + specificity + "\n" +
                    "}" + "\n";
        }
    }

    /**
     * 活动信息内部类（对应 activity 表）
     * 注意：活动本身不包含事件归属，由后端根据业务规则归类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityInfo {

        /**
         * 活动发生日期
         */
        private String date;

        /**
         * 时间段
         */
        private String timePeriod;

        /**
         * 活动描述
         */
        private String description;


        /**
         * 活动发生地点映射
         * Key: 地点名称 (如 "北京故宫")
         * Value: 该地点的详细信息
         */
        private Map<String, LocationMention> locations;

        /**
         * 参与活动人物映射
         * Key: 人物名称 (如 "张三")
         * Value: 该人物的详细信息
         */
        private Map<String, PersonRole> persons;

        /**
         * 颜色代码
         */
        private String color;

        @Override
        public String toString() {
            return "{" +
                    "活动发生日期:'" + date + "\n" +
                    ", 时间段:'" + timePeriod + "\n" +
                    ", 活动描述:'" + description + "\n" +
                    ", 活动发生地点映射:'" + locations + "\n" +
                    ", 参与活动的人物映射:'" + persons + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    "}" + "\n";
        }

        /**
         * 活动发生地点映射内部类
         */
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LocationMention {

            /**
             * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
             */
            private MentionType mentionType;
        }

        /**
         * 活动参与人物映射内部类
         */
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PersonRole {

            /**
             * 角色
             */
            private String role;

            /**
             * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
             */
            private MentionType mentionType;
        }
    }
}