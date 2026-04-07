package com.lyric.lyric.POJO.diary;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日记实体类
 * 对应数据库表: diary
 *
 * @author Yichaoxuan
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
    private String title = "";

    /**
     * 日记内容
     */
    private String content = "";

    /**
     * 总结描述
     */
    private String summary = "";

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
    private Integer isDeleted = 0;

    /**
     * 是否为草稿 (0:否, 1:是)
     */
    private Integer isDraft = 1;

    /**
     * 是否已交由AI分析 (0:否, 1:是)
     */
    private Integer isAnalyzed = 0;

    /**
     * 情感级别
     */
    private Double emotionalLevel = 0.0;

    /**
     * 字数统计
     */
    private Integer wordCount = 0;

    /**
     * 写作时长(秒)
     */
    private Integer writingDuration = 0;

    /**
     * 日记日期
     */
    private LocalDate diaryDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 有参构造方法
     * 
     * @param contentType   内容类型
     * @param contentFormat 编辑器格式
     * @param diaryDate     日记日期
     */
    public DiaryPojo(ContentType contentType, ContentFormat contentFormat, LocalDate diaryDate) {
        this.contentType = contentType;
        this.contentFormat = contentFormat;
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