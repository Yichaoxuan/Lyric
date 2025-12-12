package com.lyric.lyric.POJO.content;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日记实体类
 * 对应数据库表: diary
 *
 * @author Lyric
 */
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryPojo {

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 日记标题
     */
    private String title;
    /**
     * 日记内容
     */
    private String content;

    /**
     * 总结描述
     */
    private String summary;

    /**
     * 内容类型
     */
    private ContentType contentType;
    /**
     * 编辑器格式
     */
    private ContentFormat contentFormat;
    /**
     * 是否删除 (0:否, 1:是)
     */
    private Integer isDeleted;
    /**
     * 是否为草稿 (0:否, 1:是)
     */
    private Integer isDraft;
    /**
     * 情感得分 (-2.0 到 2.0)
     */
    private Double emotionScore;
    /**
     * 字数统计
     */
    private Integer wordCount;
    /**
     * 写作开始时间
     */
    private LocalDateTime writingStartTime;
    /**
     * 写作结束时间
     */
    private LocalDateTime writingEndTime;
    /**
     * 写作时长(分钟)
     */
    private Integer writingDuration;
    /**
     * 日记日期
     */
    private LocalDate diaryDate;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param title 日记标题
     * @param content 日记内容
     * @param summary 总结描述
     * @param contentType 内容类型
     * @param contentFormat 编辑器格式
     * @param isDeleted 是否删除
     * @param isDraft 是否为草稿
     * @param emotionScore 情感得分
     * @param wordCount 字数统计
     * @param writingStartTime 写作开始时间
     * @param writingEndTime 写作结束时间
     * @param writingDuration 写作时长
     * @param diaryDate 日记日期
     */
    public DiaryPojo(Integer id, String title, String content, String summary, ContentType contentType, ContentFormat contentFormat,
                 Integer isDeleted, Integer isDraft, Double emotionScore, Integer wordCount,
                 LocalDateTime writingStartTime, LocalDateTime writingEndTime, Integer writingDuration,
                 LocalDate diaryDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.contentType = contentType;
        this.contentFormat = contentFormat;
        this.isDeleted = isDeleted;
        this.isDraft = isDraft;
        this.emotionScore = emotionScore;
        this.wordCount = wordCount;
        this.writingStartTime = writingStartTime;
        this.writingEndTime = writingEndTime;
        this.writingDuration = writingDuration;
        this.diaryDate = diaryDate;
    }

    /**
     * 内容类型枚举
     */
    @Getter
    public enum ContentType {
        /**
         * 日记
         */
        DIARY,

        /**
         * 文章
         */
        ARTICLE,

        /**
         * 笔记
         */
        NOTE;
    }

    /**
     * 内容格式枚举
     */
    @Getter
    public enum ContentFormat {
        /**
         * 富文本
         */
        RICH_TEXT,

        /**
         * Markdown格式
         */
        MARKDOWN
    }
}