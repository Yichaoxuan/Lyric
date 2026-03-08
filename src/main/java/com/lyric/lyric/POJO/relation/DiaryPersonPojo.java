package com.lyric.lyric.POJO.relation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 日记-人物关联实体类
 * 对应数据库表: diary_person
 *
 * @author Yichoaxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryPersonPojo {

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
     * 类型：ACTUAL(实际出现)、MENTION(提及)、MEMORY(回忆)
     */
    private MentionType mentionType;

    public DiaryPersonPojo (Integer diaryId, Integer personId, MentionType mentionType) {
        this.diaryId = diaryId;
        this.personId = personId;
        this.mentionType = mentionType;
    }

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