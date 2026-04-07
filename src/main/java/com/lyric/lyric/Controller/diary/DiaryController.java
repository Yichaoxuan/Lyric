package com.lyric.lyric.Controller.diary;

import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Service.diary.DiaryService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 日记控制器
 * 提供创建、修改、查询、删除日记的 REST API 接口
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
    public Result<Void> saveDiary(@RequestBody DiaryPojo diary) {
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
            @RequestParam(value = "contentType", required = false) DiaryPojo.ContentType contentType,
            @RequestParam(value = "contentFormat", required = false) DiaryPojo.ContentFormat contentFormat) {
        return diaryService.createDraft(contentType, contentFormat);
    }

    /**
     * 更新日记（从草稿状态）
     * 将草稿状态的日记更新为正式日记
     *
     * @param diary 日记DTO对象，包含要更新的内容
     * @return 更新结果
     */
    @PostMapping("/updateDiaryFromDraft")
    public Result<Void> updateDiaryFromDraft(@RequestBody DiaryPojo diary) {
        return diaryService.updateDiaryFromDraft(diary);
    }

    /**
     * 更新草稿
     * 用于用户编辑中保存草稿
     *
     * @param diary 日记DTO对象，包含要更新的内容
     * @return 更新结果
     */
    @PostMapping("/updateDraft")
    public Result<Void> updateDraft(@RequestBody DiaryPojo diary) {
        return diaryService.updateDraft(diary);
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
     * 批量删除草稿
     *
     * @param diaryIds 草稿ID列表
     * @return 删除结果
     */
    @PostMapping("/batchDeleteDrafts")
    public Result<Void> batchDeleteDrafts(@RequestBody List<Integer> diaryIds) {
        return diaryService.batchDeleteDrafts(diaryIds);
    }

    /**
     * 批量删除回收站中的日记
     *
     * @param diaryIds 回收站日记ID列表
     * @return 删除结果
     */
    @PostMapping("/batchDeleteTrashedDiaries")
    public Result<Void> batchDeleteTrashedDiaries(@RequestBody List<Integer> diaryIds) {
        return diaryService.batchDeleteTrashedDiaries(diaryIds);
    }

    /**
     * 从草稿箱回收站彻底删除日记（包括关联的媒体文件）
     *
     * @param diaryId 日记 ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteFromDraftTrash")
    public Result<Void> deleteFromDraftTrash(@RequestParam Integer diaryId) {
        return diaryService.deleteFromDraftTrash(diaryId);
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
    public Result<Void> modifyDiary(@RequestBody DiaryPojo diary) {
        return diaryService.modifyDiary(diary);
    }

    /**
     * 查询日记
     *
     * @param diaryId 日记 ID
     * @return 查询结果
     */
    @GetMapping("/queryDiary")
    public Result<DiaryPojo> queryDiary(@RequestParam Integer diaryId) {
        return diaryService.queryDiary(diaryId);
    }

    /**
     * 查询所有日记
     *
     * @return 查询结果
     */
    @GetMapping("/queryAllDiary")
    public Result<List<DiaryPojo>> queryAllDiary() {
        return diaryService.getAllDiaryList();
    }

    /**
     * 查询所有非草稿日记
     *
     * @return 查询结果
     */
    @GetMapping("/queryNonDraftDiaries")
    public Result<List<DiaryPojo>> queryNonDraftDiaries() {
        return diaryService.getNonDraftDiaries();
    }

    /**
     * 查询所有草稿
     *
     * @return 查询结果
     */
    @GetMapping("/queryDrafts")
    public Result<List<DiaryPojo>> queryDrafts() {
        return diaryService.getDrafts();
    }

    /**
     * 查询所有回收站日记
     *
     * @return 查询结果
     */
    @GetMapping("/queryTrashedDiaries")
    public Result<List<DiaryPojo>> queryTrashedDiaries() {
        return diaryService.getTrashedDiaries();
    }

    /**
     * 根据标签 ID 查询日记列表
     *
     * @param tagId 标签 ID
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByTagId")
    public Result<List<DiaryPojo>> queryDiariesByTagId(@RequestParam Integer tagId) {
        return diaryService.getDiariesByTagId(tagId);
    }

    /**
     * 根据人物 ID 查询日记列表
     *
     * @param personId 人物 ID
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByPersonId")
    public Result<List<DiaryPojo>> queryDiariesByPersonId(@RequestParam Integer personId) {
        return diaryService.getDiariesByPersonId(personId);
    }

    /**
     * 根据人物 ID 查询该人物参与的事件列表
     *
     * @param personId 人物 ID
     * @return 事件列表（包含活动及角色信息）
     */
    @GetMapping("/queryPersonEvents")
    public Result<List<java.util.Map<String, Object>>> queryPersonEvents(@RequestParam Integer personId) {
        return diaryService.getPersonEvents(personId);
    }

    /**
     * 根据地点 ID 查询日记列表
     *
     * @param locationId 地点 ID
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByLocationId")
    public Result<List<DiaryPojo>> queryDiariesByLocationId(@RequestParam Integer locationId) {
        return diaryService.getDiariesByLocationId(locationId);
    }

    /**
     * 根据事件 ID 查询日记列表
     *
     * @param eventId 事件 ID
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByEventId")
    public Result<List<DiaryPojo>> queryDiariesByEventId(@RequestParam Integer eventId) {
        return diaryService.getDiariesByEventId(eventId);
    }

    /**
     * 根据活动 ID 查询日记列表
     *
     * @param activityId 活动 ID
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByActivityId")
    public Result<List<DiaryPojo>> queryDiariesByActivityId(@RequestParam Integer activityId) {
        return diaryService.getDiariesByActivityId(activityId);
    }

    /**
     * 根据月份查询日记列表
     *
     * @param year  年份
     * @param month 月份（1-12）
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByMonth")
    public Result<List<DiaryPojo>> queryDiariesByMonth(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return diaryService.getDiariesByMonth(year, month);
    }

    /**
     * 获取最早的日记日期
     *
     * @return 最早的日记日期
     */
    @GetMapping("/getEarliestDiaryDate")
    public Result<String> getEarliestDiaryDate() {
        return diaryService.getEarliestDiaryDate();
    }

    /**
     * 批量 AI 标签分析
     *
     * @param diaryIds 日记 ID 列表
     * @return 处理结果
     */
    @PostMapping("/batchAiTagAnalysis")
    public Result<Void> batchAiTagAnalysis(@RequestBody List<Integer> diaryIds) {
        return diaryService.batchAiTagAnalysis(diaryIds);
    }

    /**
     * 根据日期查询日记列表
     *
     * @param date 日记日期（格式：YYYY-MM-DD）
     * @return 日记列表
     */
    @GetMapping("/queryDiariesByDate")
    public Result<List<DiaryPojo>> queryDiariesByDate(@RequestParam String date) {
        return diaryService.getDiariesByDate(date);
    }

    /**
     * 批量导入Markdown文件
     *
     * @param files Markdown文件数组
     * @return 导入结果
     */
    @PostMapping("/batchImportMarkdown")
    public Result<Void> batchImportMarkdown(@RequestParam("files") MultipartFile[] files) {
        return diaryService.batchImportMarkdown(files);
    }

    @GetMapping("/search")
    public Result<List<DiaryPojo>> searchDiaries(@RequestParam String keyword) {
        return diaryService.searchDiaries(keyword);
    }

    @GetMapping("/searchByBaseTag")
    public Result<List<DiaryPojo>> searchDiariesByBaseTag(@RequestParam String keyword) {
        return diaryService.searchDiariesByBaseTag(keyword);
    }

    @GetMapping("/searchByPerson")
    public Result<List<DiaryPojo>> searchDiariesByPerson(@RequestParam String keyword) {
        return diaryService.searchDiariesByPerson(keyword);
    }

    @GetMapping("/searchByLocation")
    public Result<List<DiaryPojo>> searchDiariesByLocation(@RequestParam String keyword) {
        return diaryService.searchDiariesByLocation(keyword);
    }

    @GetMapping("/searchByEvent")
    public Result<List<DiaryPojo>> searchDiariesByEvent(@RequestParam String keyword) {
        return diaryService.searchDiariesByEvent(keyword);
    }

    @GetMapping("/getUnanalyzedCount")
    public Result<Integer> getUnanalyzedCount() {
        return diaryService.getUnanalyzedCount();
    }
}