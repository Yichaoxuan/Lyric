package com.lyric.lyric.Dto.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日记-人物关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class DiaryPerson {

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 人物ID
     */
    private Long personId;

    /**
     * 置信度
     */
    private Double confidence;
    /**
     * 有参构造方法
     * @param diaryId 日记ID
     * @param personId 人物ID
     * @param confidence 置信度
     */
    public DiaryPerson(Long diaryId, Long personId, Double confidence) {
        this.diaryId = diaryId;
        this.personId = personId;
        this.confidence = confidence;
    }
}