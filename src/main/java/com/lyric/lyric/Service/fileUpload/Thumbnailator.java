package com.lyric.lyric.Service.fileUpload;

import com.lyric.lyric.Config.fileUpload.UploadProperties;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * 缩略图生成器服务类，用于生成图片的缩略图
 * 支持从字节数组和文件两种方式生成缩略图，默认输出格式为 JPG，压缩质量为 0.8
 *
 * @author Yichaoxuan
 * @since 2026/03/16
 */
@Service
public class Thumbnailator {

    private final UploadProperties uploadProperties;

    /**
     * 构造方法，注入上传配置文件
     *
     * @param uploadProperties 上传配置文件，包含缩略图的宽度和高度配置
     */
    public Thumbnailator(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    /**
     * 从字节数组生成缩略图，使用配置的默认尺寸
     * 该方法将输入的图片数据压缩为指定尺寸的 JPG 格式缩略图
     *
     * @param imageData 原始图片的字节数组数据
     * @return 缩略图的字节数组数据，格式为 JPG，压缩质量为 0.8
     * @throws IOException 当图像处理过程中发生错误时抛出 IO 异常
     */
    public byte[] generateThumbnail(byte[] imageData) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new java.io.ByteArrayInputStream(imageData))
                .size(uploadProperties.getThumbnailWidth(), uploadProperties.getThumbnailHeight())
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 从源文件生成缩略图到目标文件，使用配置的默认尺寸
     * 该方法将输入的图片文件压缩为指定尺寸的 JPG 格式缩略图并保存到目标文件
     *
     * @param sourceFile 源图片文件，必须是有效的图片文件
     * @param targetFile 目标缩略图文件，将保存生成的缩略图
     * @throws IOException 当文件读取、写入或处理过程中发生错误时抛出 IO 异常
     */
    public void generateThumbnail(File sourceFile, File targetFile) throws IOException {
        Thumbnails.of(sourceFile)
                .size(uploadProperties.getThumbnailWidth(), uploadProperties.getThumbnailHeight())
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toFile(targetFile);
    }

    /**
     * 从源文件生成缩略图到目标文件，使用自定义尺寸
     * 该方法允许指定缩略图的具体宽高，提供更灵活简单的缩略图生成方式
     *
     * @param sourceFile 源图片文件，必须是有效的图片文件
     * @param targetFile 目标缩略图文件，将保存生成的缩略图
     * @param width 缩略图的目标宽度（像素）
     * @param height 缩略图的目标高度（像素）
     * @throws IOException 当文件读取、写入或处理过程中发生错误时抛出 IO 异常
     */
    public void generateThumbnail(File sourceFile, File targetFile, int width, int height) throws IOException {
        Thumbnails.of(sourceFile)
                .size(width, height)
                .outputFormat("jpg")
                .outputQuality(0.8)
                .toFile(targetFile);
    }
}
