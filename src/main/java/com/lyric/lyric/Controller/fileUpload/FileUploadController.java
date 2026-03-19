package com.lyric.lyric.Controller.fileUpload;

import com.lyric.lyric.Enums.mediaFile.FileType;
import com.lyric.lyric.POJO.fileUpload.MediaFilePojo;
import com.lyric.lyric.Service.fileUpload.MediaFileService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 * 提供文件上传、获取文件列表、获取文件详情和删除文件的功能
 *
 * @author Yichaoxun
 * @since 2026-03-18
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    private final MediaFileService mediaFileService;

    public FileUploadController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    /**
     * 上传媒体文件到指定日记
     *
     * @param file     待上传的文件对象，支持各种文件格式
     * @param diaryId  日记的唯一标识 ID，文件将关联到该日记
     * @param fileType 文件类型枚举值，可选参数，用于指定文件的具体类型
     * @return 返回上传成功后的媒体文件信息，包含文件 ID、URL 等详细信息
     */
    @PostMapping("/upload")
    public Result<MediaFilePojo> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("diaryId") Integer diaryId,
            @RequestParam(value = "type", required = false) FileType fileType) {
        return mediaFileService.upload(file, diaryId, fileType);
    }

    /**
     * 根据日记 ID 获取该日记下的所有媒体文件列表
     *
     * @param diaryId 日记的唯一标识 ID
     * @return 返回该日记下所有媒体文件的列表，每个文件包含完整的文件信息
     */
    @GetMapping("/list")
    public Result<List<MediaFilePojo>> listByDiaryId(@RequestParam Integer diaryId) {
        return mediaFileService.getFilesByDiaryId(diaryId);
    }

    /**
     * 根据文件 ID 获取单个媒体文件的详细信息
     *
     * @param id 媒体文件的唯一标识 ID
     * @return 返回指定 ID 的媒体文件完整信息，如果文件不存在则返回相应错误提示
     */
    @GetMapping("/{id}")
    public Result<MediaFilePojo> getById(@PathVariable Integer id) {
        return mediaFileService.getFileById(id);
    }

    /**
     * 根据文件 ID 删除指定的媒体文件
     *
     * @param id 待删除的媒体文件的唯一标识 ID
     * @return 删除操作的结果，成功时返回空响应，失败时返回错误信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        return mediaFileService.deleteFile(id);
    }
}
