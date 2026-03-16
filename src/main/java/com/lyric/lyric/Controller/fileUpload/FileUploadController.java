package com.lyric.lyric.Controller.fileUpload;

import com.lyric.lyric.Enums.mediaFile.FileType;
import com.lyric.lyric.POJO.fileUpload.MediaFilePojo;
import com.lyric.lyric.Service.fileUpload.MediaFileService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    private final MediaFileService mediaFileService;

    public FileUploadController(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    @PostMapping("/upload")
    public Result<MediaFilePojo> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("diaryId") Integer diaryId,
            @RequestParam(value = "type", required = false) FileType fileType) {
        return mediaFileService.upload(file, diaryId, fileType);
    }

    @GetMapping("/list")
    public Result<List<MediaFilePojo>> listByDiaryId(@RequestParam Integer diaryId) {
        return mediaFileService.getFilesByDiaryId(diaryId);
    }

    @GetMapping("/{id}")
    public Result<MediaFilePojo> getById(@PathVariable Integer id) {
        return mediaFileService.getFileById(id);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        return mediaFileService.deleteFile(id);
    }
}
