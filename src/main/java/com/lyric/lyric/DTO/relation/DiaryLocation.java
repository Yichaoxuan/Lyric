package com.lyric.lyric.DTO.relation;

import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-地点关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryLocation {

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
     * 日记日期
     */
    private LocalDateTime appearanceDate;

    /**
     * 类型：ACTUAL(实际到达)、MENTION(提及)、MEMORY(回忆)
     */
    private DiaryLocationPojo.MentionType mentionType;

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