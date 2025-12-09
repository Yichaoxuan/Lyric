package com.lyric.lyric.Pojo.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 日记-人物关联实体类
 * 对应数据库表: diary_person
 *
 * @author Yichoaxuan
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
     * @param diaryId 日记ID
     * @param personId 人物ID
     */
    public DiaryPersonPojo(Integer diaryId, Integer personId) {
        this.diaryId = diaryId;
        this.personId = personId;
    }
}