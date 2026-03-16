package com.lyric.lyric.Config.fileUpload;

import com.lyric.lyric.Enums.mediaFile.FileCategory;
import com.lyric.lyric.Enums.mediaFile.FileType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 文件上传配置类
 *
 * @author Yichaoxuan
 * @since 2026/03/16
 */
@Component
@ConfigurationProperties(prefix = "user-settings.features.file-storage-config")
@Getter
@Setter
public class UploadProperties {

    private String uploadDir = "../uploads";

    private String thumbnailSuffix = "thumb_";

    private int thumbnailWidth = 200;

    private int thumbnailHeight = 200;

    private Map<String, Long> maxSize = new HashMap<>();

    public long getMaxSizeForType(FileType fileType) {
        if (maxSize == null || maxSize.isEmpty()) {
            return 50L * 1024 * 1024;
        }

        FileCategory category = fileType.getCategory();
        String categoryName = category.name().toLowerCase();

        Long size = Objects.requireNonNullElse(maxSize.get(categoryName), 50L);
        return size * 1024 * 1024;
    }
}
