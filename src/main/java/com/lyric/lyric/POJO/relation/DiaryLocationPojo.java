package com.lyric.lyric.POJO.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * 日记-地点关联实体类
 * 对应数据库表: diary_location
 *
 * @author Yichaoxuan
 * @serial 2026/02/04
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryLocationPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 地点ID
     */
    private Integer locationId;

    /**
     * 类型：ACTUAL(实际到达)、MENTION(提及)、MEMORY(回忆)
     */
    private MentionType mentionType;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * 
     * @param diaryId    日记ID
     * @param locationId 地点ID
     */
    public DiaryLocationPojo(Integer diaryId, Integer locationId) {
        this.diaryId = diaryId;
        this.locationId = locationId;
    }

    /**
     * 提及类型枚举
     */
    public enum MentionType {
        /**
         * 实际到达
         */
        ACTUAL,

        /**
         * 提及
         */
        MENTION,

        /**
         * 回忆
         */
        MEMORY
    }
}