package com.lyric.lyric.POJO.AI; // 请根据您的项目结构调整包路径

import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Override
    public String toString() {
        return "AITagJson{" +
                "总结:'" + summary + "\n" +
                "标签:" + labels +  "\n" +
                "}" + "\n";
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
         * 实体标签（人物、地点、事件）
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
         * 事件实体映射
         * Key: 事件名称 (如 "毕业典礼")
         * Value: 该事件的详细信息
         */
        private Map<String, EventInfo> event;

        @Override
        public String toString() {
            return "{" +
                    "人物实体映射：" + person + "\n" +
                    "地点实体映射：" + location + "\n" +
                    "事件实体映射：" + event + "\n" +
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
         * 被提及/出现的时间(格式: YYYY-MM-DD)
         */
        private String appearanceDate;

        /**
         * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
         */
        private DiaryPersonPojo.MentionType mentionType;

        /**
         * 颜色代码
         */
        private String color;

        @Override
        public String toString() {
            return "{" +
                    "关系:'" + relationship + "\n" +
                    ", 性别:'" + gender + "\n" +
                    ", 性格总结:'" + personality + "\n" +
                    ", 被提及/出现时间:'" + appearanceDate + "\n" +
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
         * 被提及/出现的时间(格式: YYYY-MM-DD)
         */
        private String appearanceDate;

        /**
         * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
         */
        private DiaryPersonPojo.MentionType mentionType;

        /**
         * 颜色代码
         */
        private String color;

        @Override
        public String toString() {
            return "{" +
                    "描述:'" + description + "\n" +
                    ", 被提及/出现时间:'" + appearanceDate + "\n" +
                    ", 类型:'" + mentionType + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    "}" + "\n";
        }
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

        @Override
        public String toString() {
            return "{" +
                    "发生地点:'" + location + "\n" +
                    ", 发生日期:'" + date + "\n" +
                    ", 事件描述:'" + description + "\n" +
                    ", 参与人物及其角色映射:'" + persons + "\n" +
                    ", 颜色代码:'" + color + "\n" +
                    "}" + "\n";
        }
    }
}