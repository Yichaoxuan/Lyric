package com.lyric.lyric.Pojo.diaryperson;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-人物关联实体类
 * 对应数据库表: diary_person
 *
 * @author Lyric
 * @since 2025-11-21
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryPersonPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 人物ID
     */
    private Long personId;

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
     * @param personId 人物ID
     * @param confidence AI生成置信度
     */
    public DiaryPersonPojo(Long diaryId, Long personId, Double confidence) {
        this.diaryId = diaryId;
        this.personId = personId;
        this.confidence = confidence;
    }
}