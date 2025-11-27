package com.lyric.lyric.Service.diary;


import com.lyric.lyric.Dto.content.Diary;
import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.MapStruct.content.DiaryMapStruct;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Pojo.content.DiaryPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.wordCount.WordCountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DiaryService {

    private final DiaryMapper diaryMapper;

    private final DiaryMapStruct diaryMapStruct;

    private final AIAnalysisService AIAnalysisService;

    public DiaryService(DiaryMapper diaryMapper, DiaryMapStruct diaryMapStruct, AIAnalysisService AIAnalysisService) {
        this.diaryMapper = diaryMapper;
        this.diaryMapStruct = diaryMapStruct;
        this.AIAnalysisService = AIAnalysisService;
    }

    /**
     * 创建日记
     *
     * @param diary 日记DTO对象
     * @return 创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> insertDiary(Diary diary) {

        //创建DiaryPojo对象并赋值
        DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

        //设置默认值
        diaryPojo.setIsDeleted(1);
        diaryPojo.setIsDraft(1);
        diaryPojo.setEmotionScore(0.0);

        //统计字数
        diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

        //计算写作时长
        diaryPojo.setWritingDuration(DateTimeUtils.timeBetween(diary.getWritingStartTime(), diary.getWritingEndTime()));

        //判断内容类型，如果是文章或笔记，直接保存
        if (diary.getContentType() == Diary.ContentType.ARTICLE || diary.getContentType() == Diary.ContentType.NOTE) {
            diaryMapper.insert(diaryPojo);
        }

        //保存日记,并返回日记Id
        Integer dairyId = diaryMapper.insert(diaryPojo);

        //异步调用AI分析进行内容分析，生成标签
        AIAnalysisService.tagAnalysis(dairyId, diary.getContent());

        return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);

    }

    /**
     * 将日记移至回收站
     *
     * @param diaryId 日记ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> moveToTrash(Integer diaryId) {
        // 检查日记是否存在
        DiaryPojo diary = diaryMapper.selectById(diaryId);
        if (diary == null) {
            return ResultBuilder.error(ErrorMsgEnums.DIARY_NOT_FOUND);
        }
        
        // 设置日记为已删除状态
        diary.setIsDeleted(1);
        diaryMapper.update(diary);
        
        return ResultBuilder.success(SuccessMsgEnums.MOVE_TO_TRASH_SUCCESS);
    }
    
    /**
     * 从回收站彻底删除日记
     *
     * @param diaryId 日记ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> permanentlyDeleteDiary(Integer diaryId) {
        // 检查日记是否存在
        DiaryPojo diary = diaryMapper.selectById(diaryId);
        if (diary == null) {
            return ResultBuilder.error(ErrorMsgEnums.DIARY_NOT_FOUND);
        }
        
        // 检查日记是否已在回收站中（isDeleted=1）
        if (diary.getIsDeleted() != 1) {
            // 如果日记不在回收站中，不能直接永久删除
            return ResultBuilder.error(ErrorMsgEnums.DIARY_NOT_IN_TRASH);
        }
        
        // 彻底删除日记
        diaryMapper.deleteById(diaryId);
        
        return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
    }

    /**
     * 恢复回收站中的日记
     *
     * @param diaryId 日记ID
     * @return 恢复结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> restoreFromTrash(Integer diaryId) {
        // 检查日记是否存在
        DiaryPojo diary = diaryMapper.selectById(diaryId);
        if (diary == null) {
            return ResultBuilder.error(ErrorMsgEnums.DIARY_NOT_FOUND);
        }
        
        // 检查日记是否在回收站中（isDeleted=1）
        if (diary.getIsDeleted() != 1) {
            return ResultBuilder.error(ErrorMsgEnums.DIARY_NOT_IN_TRASH);
        }
        
        // 恢复日记（设置为未删除状态）
        diary.setIsDeleted(0);
        diaryMapper.update(diary);
        
        return ResultBuilder.success(SuccessMsgEnums.RESTORE_FROM_TRASH_SUCCESS);
    }


    /**
     * 修改日记
     *
     * @param diary 日记DTO对象
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> modifyDiary(Diary diary) {
        DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);
        diaryMapper.update(diaryPojo);
        return ResultBuilder.success(SuccessMsgEnums.MODIFY_SUCCESS);
    }

    /**
     * 查询日记
     *
     * @param diaryId 日记ID
     * @return 查询结果
     */
    public Result<Diary> queryDiary(Integer diaryId) {
        return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryMapStruct.toDto(diaryMapper.selectById(diaryId)));
    }

}