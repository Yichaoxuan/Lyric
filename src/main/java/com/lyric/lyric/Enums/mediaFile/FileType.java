package com.lyric.lyric.Enums.mediaFile;

import lombok.Getter;

/**
 * 文件类型枚举类
 * 包含系统中所有文件类型的枚举值
 *
 * @author Yichaoxuan
 * @since 2026/03/12
 */
@Getter
public enum FileType {
    // 图片类型
    JPEG("image/jpeg", "jpg", FileCategory.IMAGE),
    PNG("image/png", "png", FileCategory.IMAGE),
    GIF("image/gif", "gif", FileCategory.IMAGE),

    // 音频类型
    MP3("audio/mpeg", "mp3", FileCategory.AUDIO),
    WAV("audio/wav", "wav", FileCategory.AUDIO),
    OGG("audio/ogg", "ogg", FileCategory.AUDIO),

    // 视频类型
    MP4("video/mp4", "mp4", FileCategory.VIDEO),
    MOV("video/quicktime", "mov", FileCategory.VIDEO),
    AVI("video/x-msvideo", "avi", FileCategory.VIDEO);

    private final String mimeType;
    private final String extension;
    private final FileCategory category;

    FileType(String mimeType, String extension, FileCategory category) {
        this.mimeType = mimeType;
        this.extension = extension;
        this.category = category;
    }

    // 根据 MIME 类型查找枚举（用于请求校验）
    public static FileType fromMimeType(String mimeType) {
        for (FileType type : values()) {
            if (type.mimeType.equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        return null;
    }

    // 根据扩展名查找（可选）
    public static FileType fromExtension(String ext) {
        for (FileType type : values()) {
            if (type.extension.equalsIgnoreCase(ext)) {
                return type;
            }
        }
        return null;
    }

}