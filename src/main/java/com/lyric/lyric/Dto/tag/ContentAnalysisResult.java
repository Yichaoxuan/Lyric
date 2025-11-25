package com.lyric.lyric.Dto.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 内容分析结果DTO类
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentAnalysisResult {

    /**
     * 总结描述
     */
    private String summary;

    /**
     * 标签列表
     */
    private List<Label> labels;

    /**
     * 有参构造方法
     *
     * @param summary 总结描述
     * @param labels  标签列表
     */
    public ContentAnalysisResult(String summary, List<Label> labels) {
        this.summary = summary;
        this.labels = labels;
    }
}
