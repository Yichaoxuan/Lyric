package com.lyric.lyric.Dto.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 多媒体文件请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class MediaFile {

    /**
     * 日记ID
     */
    private Long diaryId;

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
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;
    /**
     * 有参构造方法
     * @param diaryId 日记ID
     * @param fileName 文件名
     * @param filePath 文件路径
     * @param fileType 文件类型
     * @param fileSize 文件大小
     * @param uploadTime 上传时间
     */
    public MediaFile(Long diaryId, String fileName, String filePath, String fileType, Long fileSize, LocalDateTime uploadTime) {
        this.diaryId = diaryId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadTime = uploadTime;
    }
}