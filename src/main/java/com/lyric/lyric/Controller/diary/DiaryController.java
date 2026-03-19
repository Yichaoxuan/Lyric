package com.lyric.lyric.Controller.diary;

import com.lyric.lyric.DTO.diary.Diary;
import com.lyric.lyric.Service.diary.DiaryService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日记控制器
 * 提供创建、修改、查询、删除日记的REST API接口
 *
 * @author Yichaoxuan
 * @since 2026-3-18
 */
@Slf4j
@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    /**
     * 创建日记
     *
     * @param diary 日记DTO对象
     * @return 创建结果
     */
    @PostMapping("/saveDiary")
    public Result<Void> saveDiary(@RequestBody Diary diary) {
        return diaryService.saveDiary(diary);
    }

    /**
     * 创建日记草稿
     * 用于用户开始编辑时创建一个空的草稿记录，返回日记ID供文件上传使用
     *
     * @param contentType   内容类型，可选，默认为DIARY
     * @param contentFormat 内容格式，可选，默认为RICH_TEXT
     * @return 包含日记ID的创建结果
     */
    @PostMapping("/createDraft")
    public Result<Integer> createDraft(
            @RequestParam(value = "contentType", required = false) Diary.ContentType contentType,
            @RequestParam(value = "contentFormat", required = false) Diary.ContentFormat contentFormat) {
        return diaryService.createDraft(contentType, contentFormat);
    }

    /**
     * 将日记移至回收站
     *
     * @param diaryId 日记 ID
     * @return 操作结果
     */
    @PostMapping("/moveToTrash")
    public Result<Void> moveToTrash(@RequestParam Integer diaryId) {
        return diaryService.moveToTrash(diaryId);
    }

    /**
     * 从回收站永久删除日记
     *
     * @param diaryId 日记 ID
     * @return 删除结果
     */
    @DeleteMapping("/permanentlyDelete")
    public Result<Void> permanentlyDelete(@RequestParam Integer diaryId) {
        return diaryService.permanentlyDeleteDiary(diaryId);
    }

    /**
     * 从回收站恢复日记
     *
     * @param diaryId 日记 ID
     * @return 恢复结果
     */
    @PostMapping("/restoreFromTrash")
    public Result<Void> restoreFromTrash(@RequestParam Integer diaryId) {
        return diaryService.restoreFromTrash(diaryId);
    }

    /**
     * 修改日记
     *
     * @param diary 日记 DTO 对象
     * @return 修改结果
     */
    @PostMapping("/modifyDiary")
    public Result<Void> modifyDiary(@RequestBody Diary diary) {
        return diaryService.modifyDiary(diary);
    }

    /**
     * 查询日记
     *
     * @param diaryId 日记 ID
     * @return 查询结果
     */
    @GetMapping("/queryDiary")
    public Result<Diary> queryDiary(@RequestParam Integer diaryId) {
        return diaryService.queryDiary(diaryId);
    }

    /**
     * 查询所有日记
     *
     * @return 查询结果
     */
    @GetMapping("/queryAllDiary")
    public Result<List<Diary>> queryAllDiary() {
        return diaryService.getAllDiaryList();
    }
}