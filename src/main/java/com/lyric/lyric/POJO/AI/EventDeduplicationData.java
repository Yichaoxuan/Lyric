package com.lyric.lyric.POJO.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 事件去重分析数据传输对象
 * 用于向 AI 提供完整的事件、活动、人物、地点信息，支持智能去重分析
 *
 * @author Yichaoxuan
 * @since 2026-04-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDeduplicationData {

    /**
     * 新活动信息
     */
    private NewActivityInfo newActivity;

    /**
     * 候选事件列表（包含每个事件及其关联的活动、人物、地点）
     */
    private List<CandidateEventInfo> candidateEvents;

    @Override
    public String toString() {
        return "EventDeduplicationData{" +
                "\n新活动信息：" + newActivity +
                ", \n候选事件列表：" + candidateEvents +
                "\n}";
    }

    /**
     * 新活动信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewActivityInfo {
        /**
         * 活动名称
         */
        private String name;

        /**
         * 活动日期
         */
        private String date;

        /**
         * 时间段（MORNING/NOON/AFTERNOON/EVENING/NIGHT）
         */
        private String timePeriod;

        /**
         * 活动描述
         */
        private String description;

        /**
         * 参与活动的人物列表
         */
        private List<PersonRelation> persons;

        /**
         * 活动涉及的地点列表
         */
        private List<LocationRelation> locations;

        @Override
        public String toString() {
            return "\n    NewActivityInfo{" +
                    "\n      名称：'" + name + '\'' +
                    ", \n      日期：'" + date + '\'' +
                    ", \n      时间段：'" + timePeriod + '\'' +
                    ", \n      描述：'" + description + '\'' +
                    ", \n      人物列表：" + persons +
                    ", \n      地点列表：" + locations +
                    "\n    }";
        }
    }

    /**
     * 候选事件信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateEventInfo {
        /**
         * 事件 ID
         */
        private Integer eventId;

        /**
         * 事件名称
         */
        private String eventName;

        /**
         * 事件开始日期
         */
        private LocalDate startDate;

        /**
         * 事件结束日期
         */
        private LocalDate endDate;

        /**
         * 事件描述
         */
        private String description;

        /**
         * 该事件包含的活动列表
         */
        private List<ActivityWithRelations> activities;

        @Override
        public String toString() {
            return "\n  CandidateEventInfo{" +
                    "\n    事件 ID:" + eventId +
                    ", \n    名称：'" + eventName + '\'' +
                    ", \n    开始日期：" + startDate +
                    ", \n    结束日期：" + endDate +
                    ", \n    描述：'" + description + '\'' +
                    ", \n    活动列表：" + activities +
                    "\n  }";
        }
    }

    /**
     * 活动及其关联信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityWithRelations {
        /**
         * 活动 ID
         */
        private Integer activityId;

        /**
         * 活动名称
         */
        private String activityName;

        /**
         * 活动描述
         */
        private String description;

        /**
         * 参与活动的人物列表
         */
        private List<PersonRelation> persons;

        /**
         * 活动涉及的地点列表
         */
        private List<LocationRelation> locations;

        @Override
        public String toString() {
            return "\n      ActivityWithRelations{" +
                    "\n        活动 ID:" + activityId +
                    ", \n        名称：'" + activityName + '\'' +
                    ", \n        描述：'" + description + '\'' +
                    ", \n        人物列表：" + persons +
                    ", \n        地点列表：" + locations +
                    "\n      }";
        }
    }

    /**
     * 人物关联信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonRelation {
        /**
         * 人物 ID
         */
        private Integer personId;

        /**
         * 人物在活动中的角色
         */
        private String role;

        @Override
        public String toString() {
            return "{PersonID:" + personId + ", 角色：'" + role + '\'' + ", 类型：" + "}";
        }
    }

    /**
     * 地点关联信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationRelation {
        /**
         * 地点 ID
         */
        private Integer locationId;

        @Override
        public String toString() {
            return "{LocationID:" + locationId + ", 类型：" + "}";
        }
    }
}