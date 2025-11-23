package com.lyric.lyric.Pojo.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-人物关联实体类
 * 对应数据库表: diary_person
 *
 * @author Lyric
 */
@Getter
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
     * AI生成置信度
     */
    private Double confidence;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param personId 人物ID
     * @param confidence AI生成置信度
     */
    public DiaryPersonPojo(Integer id, Integer diaryId, Integer personId, Double confidence) {
        this.id = id;
        this.diaryId = diaryId;
        this.personId = personId;
        this.confidence = confidence;
    }
}