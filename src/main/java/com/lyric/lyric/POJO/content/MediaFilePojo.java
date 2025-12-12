package com.lyric.lyric.POJO.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 多媒体文件实体类
 * 对应数据库表: media_file
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MediaFilePojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 关联的日记ID
     */
    private Integer diaryId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private FileType fileType;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param diaryId 关联的日记ID
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param fileType 文件类型
     * @param fileSize 文件大小
     * @param uploadTime 上传时间
     */
    public MediaFilePojo(Integer id, Integer diaryId, String fileName, String filePath, FileType fileType, Long fileSize, LocalDateTime uploadTime) {
        this.id = id;
        this.diaryId = diaryId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
    }

    /**
     * 文件类型枚举
     */
    @Getter
    public enum FileType {
        /**
         * 图片文件
         */
        IMAGE,

        /**
         * 音频文件
         */
        AUDIO,

        /**
         * 视频文件
         */
        VIDEO
    }
}