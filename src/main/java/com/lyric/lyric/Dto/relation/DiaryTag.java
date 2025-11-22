package com.lyric.lyric.Dto.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 日记-标签关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class DiaryTag {

    /**
     * 日记ID
     */
    private Long diaryId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 置信度
     */
    private Double confidence;
    /**
     * 有参构造方法
     * @param diaryId 日记ID
     * @param tagId 标签ID
     * @param confidence 置信度
     */
    public DiaryTag(Long diaryId, Long tagId, Double confidence) {
        this.diaryId = diaryId;
        this.tagId = tagId;
        this.confidence = confidence;
    }
}