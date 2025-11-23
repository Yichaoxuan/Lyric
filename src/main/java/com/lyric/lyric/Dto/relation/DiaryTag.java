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
     * 主键ID
     */
    private Integer id;

    /**
     * 日记ID
     */
    private Integer diaryId;

    /**
     * 标签ID
     */
    private Integer tagId;

    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 有参构造方法
     * @param id 主键ID
     * @param diaryId 日记ID
     * @param tagId 标签ID
     * @param confidence 置信度
     */
    public DiaryTag(Integer id, Integer diaryId, Integer tagId, Double confidence) {
        this.id = id;
        this.diaryId = diaryId;
        this.tagId = tagId;
        this.confidence = confidence;
    }
}