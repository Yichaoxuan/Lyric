package com.lyric.lyric.POJO.fileUpload;

import com.lyric.lyric.Enums.mediaFile.FileType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 多媒体文件实体类
 * 对应数据库表: media_file
 *
 * @author Yichaoxuan
 * @since 2026/03/16
 */
@Data
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

}