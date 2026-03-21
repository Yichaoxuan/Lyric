package com.lyric.lyric.Service.diary;

import com.lyric.lyric.DTO.diary.Diary;
import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.MapStruct.content.DiaryMapStruct;
import com.lyric.lyric.Mapper.diary.DiaryMapper;
import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Service.tag.parsing.TagParsingService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.wordCount.WordCountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日记服务类
 * 提供日记相关处理方法
 *
 * @author Yichaoxuan
 * @serial 2026/03/16
 */
@Slf4j
@Service
public class DiaryService {

    private final DiaryMapper diaryMapper;

    private final DiaryMapStruct diaryMapStruct;

    private final TagParsingService tagParsingService;

    public DiaryService(DiaryMapper diaryMapper, DiaryMapStruct diaryMapStruct, TagParsingService tagParsingService) {
        this.diaryMapper = diaryMapper;
        this.diaryMapStruct = diaryMapStruct;
        this.tagParsingService = tagParsingService;
    }

    /**
     * 创建日记
     *
     * @param diary 日记DTO对象
     * @return 创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveDiary(Diary diary) {
        log.info("创建日记");
        // 检查日记标题是否为空
        if (diary.getTitle() == null || diary.getTitle().trim().isEmpty()) {
            // 抛出业务异常，由全局异常处理器处理
            throw new BusinessException(BusinessErrorMsgEnums.DIARY_TITLE_EMPTY);
        }

        // 检查日记内容是否为空
        if (diary.getContent() == null || diary.getContent().trim().isEmpty()) {
            // 抛出业务异常，由全局异常处理器处理
            throw new BusinessException(BusinessErrorMsgEnums.DIARY_CONTENT_EMPTY);
        }

        // 创建DiaryPojo对象并赋值
        DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

        // 统计字数
        diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

        // 计算写作时长
        diaryPojo.setWritingDuration(DateTimeUtils.timeBetween(diary.getWritingStartTime(), diary.getWritingEndTime()));

        try {
            // 判断内容类型，如果是文章或笔记，直接保存
            if (diary.getContentType() == Diary.ContentType.ARTICLE
                    || diary.getContentType() == Diary.ContentType.NOTE) {
                diaryMapper.insert(diaryPojo);
                return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);
            }

            // 保存日记,并获取日记Id添加到日记实体类中
            diaryMapper.insert(diaryPojo);
            diary.setId(diaryPojo.getId());

            // 异步调用标签分析进行内容分析，生成标签
            tagParsingService.tagAnalysis(diary);

            return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 创建日记草稿
     * 用于用户开始编辑时创建一个空的草稿记录，返回日记ID供文件上传使用
     *
     * @param contentType   内容类型，默认为DIARY
     * @param contentFormat 内容格式，默认为RICH_TEXT
     * @return 包含日记ID的创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> createDraft(Diary.ContentType contentType, Diary.ContentFormat contentFormat) {
        try {
            log.info("创建日记草稿");
            // 创建DiaryPojo对象
            DiaryPojo diaryPojo = new DiaryPojo();

            // 设置默认值
            diaryPojo.setTitle(""); // 空标题
            diaryPojo.setContent(""); // 空内容
            // 将 DTO 的枚举转换为 POJO 的枚举
            diaryPojo.setContentType(contentType != null 
                    ? DiaryPojo.ContentType.valueOf(contentType.name()) 
                    : DiaryPojo.ContentType.DIARY);
            diaryPojo.setContentFormat(
                    contentFormat != null 
                            ? DiaryPojo.ContentFormat.valueOf(contentFormat.name()) 
                            : DiaryPojo.ContentFormat.RICH_TEXT);
            diaryPojo.setIsDraft(1); // 草稿状态
            diaryPojo.setWordCount(0);

            // 保存草稿
            diaryMapper.insert(diaryPojo);

            // 返回日记ID
            return ResultBuilder.successWithData(SuccessMsgEnums.SAVE_SUCCESS, diaryPojo.getId());
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 更新日记（从草稿状态）
     * 将草稿状态的日记更新为正式日记，修改日记内容并更新相关字段
     *
     * @param diary 日记DTO对象，包含要更新的内容
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDiaryFromDraft(Diary diary) {
        try {
            log.info("更新日记（从草稿状态），日记ID: {}", diary.getId());

            // 检查日记ID是否存在
            if (diary.getId() == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 查询现有日记
            DiaryPojo existingDiary = diaryMapper.selectById(diary.getId());
            if (existingDiary == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 使用MapStruct将DTO转换为POJO
            DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

            // 保留原有的创建时间
            diaryPojo.setCreatedAt(existingDiary.getCreatedAt());

            // 设置更新时间为当前时间
            LocalDateTime now = LocalDateTime.now();
            diaryPojo.setUpdatedAt(now);

            // 修改日记状态为非草稿
            diaryPojo.setIsDraft(0);

            // 统计字数
            diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

            // 计算写作时长
            diaryPojo.setWritingDuration(DateTimeUtils.timeBetween(diary.getWritingStartTime(), diary.getWritingEndTime()));

            // 更新日记
            diaryMapper.update(diaryPojo);

            // 如果不是文章或笔记类型，异步调用标签分析
            if (diary.getContentType() != Diary.ContentType.ARTICLE
                    && diary.getContentType() != Diary.ContentType.NOTE) {
                tagParsingService.tagAnalysis(diary);
            }

            return ResultBuilder.success(SuccessMsgEnums.MODIFY_SUCCESS);
        } catch (BusinessException e) {
            // 直接重新抛出业务异常
            return ResultBuilder.error(e.getBusinessErrorMsgEnums());
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 将日记移至回收站
     *
     * @param diaryId 日记ID
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> moveToTrash(Integer diaryId) {
        try {
            // 检查日记是否存在
            DiaryPojo diary = diaryMapper.selectById(diaryId);
            if (diary == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 设置日记为已删除状态
            diary.setIsDeleted(1);
            diaryMapper.update(diary);

            return ResultBuilder.success(SuccessMsgEnums.MOVE_TO_TRASH_SUCCESS);
        } catch (BusinessException e) {
            // 直接重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 从回收站彻底删除日记
     *
     * @param diaryId 日记ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> permanentlyDeleteDiary(Integer diaryId) {
        try {
            // 检查日记是否存在
            DiaryPojo diary = diaryMapper.selectById(diaryId);
            if (diary == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 检查日记是否已在回收站中（isDeleted=1）
            if (diary.getIsDeleted() != 1) {
                // 如果日记不在回收站中，不能直接永久删除
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_IN_TRASH);
            }

            // 彻底删除日记
            diaryMapper.deleteById(diaryId);

            return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
        } catch (BusinessException e) {
            // 直接重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 恢复回收站中的日记
     *
     * @param diaryId 日记ID
     * @return 恢复结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> restoreFromTrash(Integer diaryId) {
        try {
            // 检查日记是否存在
            DiaryPojo diary = diaryMapper.selectById(diaryId);
            if (diary == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 检查日记是否在回收站中（isDeleted=1）
            if (diary.getIsDeleted() != 1) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_IN_TRASH);
            }

            // 恢复日记（设置为未删除状态）
            diary.setIsDeleted(0);
            diaryMapper.update(diary);

            return ResultBuilder.success(SuccessMsgEnums.RESTORE_FROM_TRASH_SUCCESS);
        } catch (BusinessException e) {
            // 直接重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 修改日记
     *
     * @param diary 日记DTO对象
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> modifyDiary(Diary diary) {
        try {
            DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);
            diaryMapper.update(diaryPojo);
            return ResultBuilder.success(SuccessMsgEnums.MODIFY_SUCCESS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 查询日记
     *
     * @param diaryId 日记ID
     * @return 查询结果
     */
    public Result<Diary> queryDiary(Integer diaryId) {
        try {
            DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
            if (diaryPojo == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryMapStruct.toDto(diaryPojo));
        } catch (BusinessException e) {
            // 直接重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取所有日记列表
     *
     * @return 所有日记列表
     */
    public Result<List<Diary>> getAllDiaryList() {
        try {
            log.info("查询全部日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectAll();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryMapStruct.toDtoList(diaryPojoList));
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }
    
    /**
     * 获取所有非草稿日记列表
     *
     * @return 非草稿日记列表
     */
    public Result<List<Diary>> getNonDraftDiaries() {
        try {
            log.info("查询非草稿日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectNonDraftDiaries();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryMapStruct.toDtoList(diaryPojoList));
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }
    
    /**
     * 获取所有草稿列表
     *
     * @return 草稿列表
     */
    public Result<List<Diary>> getDrafts() {
        try {
            log.info("查询草稿");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectDrafts();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryMapStruct.toDtoList(diaryPojoList));
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }
}