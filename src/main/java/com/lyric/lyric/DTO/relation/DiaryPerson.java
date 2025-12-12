package com.lyric.lyric.DTO.relation;

import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 日记-人物关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryPerson {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 人物ID
     */
    private Integer personId;

    /**
     * 日记日期
     */
    private LocalDateTime appearanceDate;

    /**
     * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
     */
    private DiaryPersonPojo.MentionType mentionType;

    /**
     * 提及类型枚举
     */
    public enum MentionType {
        /**
         * 实际出现
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