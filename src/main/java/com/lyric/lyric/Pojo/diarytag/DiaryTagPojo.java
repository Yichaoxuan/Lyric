package com.lyric.lyric.Pojo.diarytag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-标签关联实体类
 * 对应数据库表: diary_tag
 *
 * @author Lyric
 * @since 2025-11-21
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryTagPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * AI生成置信度
     */
    private Double confidence;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param diaryId 日记ID
     * @param tagId 标签ID
     * @param confidence AI生成置信度
     */
    public DiaryTagPojo(Long diaryId, Long tagId, Double confidence) {
        this.diaryId = diaryId;
        this.tagId = tagId;
        this.confidence = confidence;
    }
}