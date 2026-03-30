package com.lyric.lyric.DTO.diary;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 日记请求DTO类
 * 只包含前端可信字段
 *
 * @author Yichaoxuan
 */
@Getter
@Setter
@NoArgsConstructor
public class Diary {

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
     * 是否已交由AI分析 (0:否, 1:是)
     */
    private Integer isAnalyzed;

    /**
     * 情感级别
     */
    private Double emotionalLevel = null;

    /**
     * 字数统计
     */
    private Integer wordCount;

    /**
     * 写作时长(秒)
     */
    private Integer writingDuration;

    /**
     * 日记日期
     */
    private LocalDate diaryDate;

    /**
     * 有参构造方法
     * @param id 主键ID
     * @param title 日记标题
     * @param content 日记内容
     * @param summary 总结描述
     * @param contentType 内容类型
     * @param contentFormat 编辑器格式
     * @param isDraft 是否为草稿
     * @param isAnalyzed 是否已交由AI分析
     * @param emotionalLevel 情感级别
     * @param wordCount 字数统计
     * @param writingDuration 写作时长（秒）
     * @param diaryDate 日记日期
     */
    public Diary(Integer id, String title, String content, String summary, ContentType contentType, ContentFormat contentFormat,
                 Integer isDeleted, Integer isDraft, Integer isAnalyzed, Double emotionalLevel, Integer wordCount,
                  Integer writingDuration, LocalDate diaryDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.contentType = contentType;
        this.contentFormat = contentFormat;
        this.isDeleted = isDeleted;
        this.isDraft = isDraft;
        this.isAnalyzed = isAnalyzed;
        this.emotionalLevel = emotionalLevel;
        this.wordCount = wordCount;
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
        NOTE
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