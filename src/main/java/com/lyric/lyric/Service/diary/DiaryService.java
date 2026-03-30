package com.lyric.lyric.Service.diary;

import com.lyric.lyric.DTO.diary.Diary;
import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.MapStruct.content.DiaryMapStruct;
import com.lyric.lyric.Mapper.diary.DiaryMapper;
import com.lyric.lyric.Mapper.relation.DiaryLocationMapper;
import com.lyric.lyric.Mapper.relation.DiaryPersonMapper;
import com.lyric.lyric.Mapper.relation.DiaryTagMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Service.fileUpload.MediaFileService;
import com.lyric.lyric.Service.tag.parsing.TagParsingService;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.wordCount.WordCountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 日记服务类
 * 提供日记相关处理方法
 *
 * @author Yichaoxuan
 * @since 2026/03/24
 */
@Slf4j
@Service
public class DiaryService {

    private final DiaryMapper diaryMapper;

    private final DiaryMapStruct diaryMapStruct;

    private final TagParsingService tagParsingService;

    private final MediaFileService mediaFileService;

    private final DiaryTagMapper diaryTagMapper;

    private final DiaryPersonMapper diaryPersonMapper;

    private final DiaryLocationMapper diaryLocationMapper;

    private final EventMapper eventMapper;

    public DiaryService(DiaryMapper diaryMapper, DiaryMapStruct diaryMapStruct,
            TagParsingService tagParsingService, MediaFileService mediaFileService,
            DiaryTagMapper diaryTagMapper, DiaryPersonMapper diaryPersonMapper,
            DiaryLocationMapper diaryLocationMapper, EventMapper eventMapper) {
        this.diaryMapper = diaryMapper;
        this.diaryMapStruct = diaryMapStruct;
        this.tagParsingService = tagParsingService;
        this.mediaFileService = mediaFileService;
        this.diaryTagMapper = diaryTagMapper;
        this.diaryPersonMapper = diaryPersonMapper;
        this.diaryLocationMapper = diaryLocationMapper;
        this.eventMapper = eventMapper;
    }

    // ==================== 日记创建相关 ====================

    /**
     * 创建日记草稿
     * 用于用户开始编辑时创建一个空的草稿记录，返回日记 ID 供文件上传使用
     *
     * @param contentType   内容类型，默认为 DIARY
     * @param contentFormat 内容格式，默认为 RICH_TEXT
     * @return 包含日记 ID 的创建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> createDraft(Diary.ContentType contentType, Diary.ContentFormat contentFormat) {
        try {
            // 创建 DiaryPojo 对象
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

            // 保存草稿
            diaryMapper.insert(diaryPojo);
            log.info("创建日记草稿，日记id:{}", diaryPojo.getId());

            // 返回日记 ID
            return ResultBuilder.successWithData(SuccessMsgEnums.SAVE_SUCCESS, diaryPojo.getId());
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 创建日记
     *
     * @param diary 日记 DTO 对象
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

        // 创建 DiaryPojo 对象并赋值
        DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

        // 统计字数
        diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

        // 设置写作时长（前端已计算好）
        diaryPojo.setWritingDuration(diary.getWritingDuration());

        try {
            // 判断内容类型，如果是文章或笔记，直接保存
            if (diary.getContentType() == Diary.ContentType.ARTICLE
                    || diary.getContentType() == Diary.ContentType.NOTE) {
                diaryMapper.insert(diaryPojo);
                return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);
            }

            // 保存日记，并获取日记 Id 添加到日记实体类中
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

    // ==================== 日记更新相关 ====================

    /**
     * 更新日记（从草稿状态）
     * 将草稿状态的日记更新为正式日记，修改日记内容并更新相关字段
     *
     * @param diary 日记 DTO 对象，包含要更新的内容
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDiaryFromDraft(Diary diary) {
        try {
            log.warn("更新日记（从草稿状态），日记 ID: {}", diary.getId());

            // 检查日记 ID 是否存在
            if (diary.getId() == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 查询现有日记
            DiaryPojo existingDiary = diaryMapper.selectById(diary.getId());
            if (existingDiary == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 使用 MapStruct 将 DTO 转换为 POJO
            DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

            diaryPojo.setId(diary.getId());

            // 设置是否为草稿状态
            diaryPojo.setIsDraft(0);

            // 保留原有的创建时间
            diaryPojo.setCreatedAt(existingDiary.getCreatedAt());

            // 设置更新时间为当前时间
            LocalDateTime now = LocalDateTime.now();
            diaryPojo.setUpdatedAt(now);

            // 统计字数
            diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

            // 设置写作时长（如果提供了时间）
            if (diary.getWritingDuration() != null) {
                diaryPojo.setWritingDuration(diaryPojo.getWritingDuration() + diary.getWritingDuration());
            }

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
     * 修改日记
     *
     * @param diary 日记 DTO 对象
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
     * 更新草稿
     *
     * @param diary 日记 DTO 对象
     * @return 更新结果
     */
    public Result<Void> updateDraft(Diary diary) {
        try {

            log.info("更新草稿，diaryId={}", diary.getId());

            // 将 DTO 对象转换为 POJO 对象
            DiaryPojo newDiaryPojo = diaryMapStruct.toPojo(diary);

            // 手动设置 id
            newDiaryPojo.setId(diary.getId());

            // 获取数据库中该日记的 POJO 对象
            DiaryPojo oldDiaryPojo = diaryMapper.selectById(newDiaryPojo.getId());

            // 检查日记是否存在
            if (oldDiaryPojo == null) {
                return ResultBuilder.error(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 设置草稿状态和前端传递的 emotionalLevel
            newDiaryPojo.setIsDraft(1);
            newDiaryPojo.setEmotionalLevel(0.0);

            // 更新
            diaryMapper.update(newDiaryPojo);

            return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);

        } catch (Exception e) {
            // 抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 删除与回收站管理 ====================

    /**
     * 将日记移至回收站
     *
     * @param diaryId 日记 ID
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
     * 恢复回收站中的日记
     *
     * @param diaryId 日记 ID
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
     * 从草稿箱回收站彻底删除日记（包括关联的媒体文件）
     * 该方法会删除日记记录及其关联的所有媒体文件
     *
     * @param diaryId 日记 ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteFromDraftTrash(Integer diaryId) {
        try {
            log.info("从回收站彻底删除日记，日记 ID: {}", diaryId);
            // 检查日记是否存在
            DiaryPojo diary = diaryMapper.selectById(diaryId);
            if (diary == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 检查日记是否已在回收站中（isDeleted=1）或是否为草稿（isDraft=1）
            if (diary.getIsDeleted() != 1 && diary.getIsDraft() != 1) {
                // 如果日记不在回收站中，不能直接永久删除
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_IN_TRASH);
            }

            // 删除日记中的多媒体文件
            mediaFileService.deleteFilesByDiaryId(diaryId);

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
     * 批量删除草稿
     *
     * @param diaryIds 草稿 ID 列表
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDeleteDrafts(List<Integer> diaryIds) {
        try {
            log.info("批量删除草稿，数量：{}", diaryIds != null ? diaryIds.size() : 0);
            // 验证输入参数
            if (diaryIds == null || diaryIds.isEmpty()) {
                return ResultBuilder.error(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 执行批量删除并获取实际影响的行数
            int deletedCount = diaryMapper.batchDeleteDrafts(diaryIds);
            log.info("实际删除草稿数量：{}", deletedCount);

            return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 批量删除回收站中的日记
     *
     * @param diaryIds 回收站日记 ID 列表
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDeleteTrashedDiaries(List<Integer> diaryIds) {
        try {
            log.info("批量删除回收站日记，数量：{}", diaryIds != null ? diaryIds.size() : 0);
            // 验证输入参数
            if (diaryIds == null || diaryIds.isEmpty()) {
                return ResultBuilder.error(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 执行批量删除并获取实际影响的行数
            int deletedCount = diaryMapper.batchDeleteTrashedDiaries(diaryIds);
            log.info("实际删除回收站日记数量：{}", deletedCount);

            return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 基础查询相关 ====================

    /**
     * 查询日记
     *
     * @param diaryId 日记 ID
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
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryMapStruct.toDtoList(diaryPojoList));
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
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryMapStruct.toDtoList(diaryPojoList));
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
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryMapStruct.toDtoList(diaryPojoList));
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取所有回收站日记列表
     *
     * @return 回收站日记列表
     */
    public Result<List<Diary>> getTrashedDiaries() {
        try {
            log.info("查询回收站日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectTrashedDiaries();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryMapStruct.toDtoList(diaryPojoList));
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 按关联条件查询 ====================

    /**
     * 根据标签 ID 查询日记列表
     *
     * @param tagId 标签 ID
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByTagId(Integer tagId) {
        try {
            log.info("根据标签 ID 查询日记，tagId={}", tagId);

            // 查询该标签下的所有日记 ID
            List<com.lyric.lyric.POJO.relation.DiaryTagPojo> relations = diaryTagMapper.selectByTagId(tagId);

            // 提取日记 ID 列表
            List<Integer> diaryIds = new ArrayList<>();
            for (com.lyric.lyric.POJO.relation.DiaryTagPojo relation : relations) {
                diaryIds.add(relation.getDiaryId());
            }

            // 根据日记 ID 查询日记详情
            List<Diary> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryMapStruct.toDto(diaryPojo));
                }
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据人物 ID 查询日记列表
     *
     * @param personId 人物 ID
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByPersonId(Integer personId) {
        try {
            log.info("根据人物 ID 查询日记，personId={}", personId);

            // 查询该人物下的所有日记 ID
            List<com.lyric.lyric.POJO.relation.DiaryPersonPojo> relations = diaryPersonMapper
                    .selectByPersonId(personId);

            // 提取日记 ID 列表
            List<Integer> diaryIds = new ArrayList<>();
            for (com.lyric.lyric.POJO.relation.DiaryPersonPojo relation : relations) {
                diaryIds.add(relation.getDiaryId());
            }

            // 根据日记 ID 查询日记详情
            List<Diary> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryMapStruct.toDto(diaryPojo));
                }
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据地点 ID 查询日记列表
     *
     * @param locationId 地点 ID
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByLocationId(Integer locationId) {
        try {
            log.info("根据地点 ID 查询日记，locationId={}", locationId);

            // 查询该地点下的所有日记 ID
            List<com.lyric.lyric.POJO.relation.DiaryLocationPojo> relations = diaryLocationMapper
                    .selectByLocationId(locationId);

            // 提取日记 ID 列表
            List<Integer> diaryIds = new ArrayList<>();
            for (com.lyric.lyric.POJO.relation.DiaryLocationPojo relation : relations) {
                diaryIds.add(relation.getDiaryId());
            }

            // 根据日记 ID 查询日记详情
            List<Diary> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryMapStruct.toDto(diaryPojo));
                }
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据事件 ID 查询日记列表
     *
     * @param eventId 事件 ID
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByEventId(Integer eventId) {
        try {
            log.info("根据事件 ID 查询日记，eventId={}", eventId);

            // 根据事件 ID 查询关联的日记 ID
            Integer diaryId = eventMapper.selectDiaryIdByTogEventId(eventId);

            // 查询日记详情
            List<Diary> diaries = new ArrayList<>();
            if (diaryId != null) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryMapStruct.toDto(diaryPojo));
                }
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 按时间查询 ====================

    /**
     * 根据日期查询日记列表
     *
     * @param date 日记日期（格式：YYYY-MM-DD）
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByDate(String date) {
        try {
            log.info("根据日期查询日记，date={}", date);

            // 查询指定日期的日记
            List<com.lyric.lyric.POJO.diary.DiaryPojo> diaryPos = diaryMapper.selectByDate(date);

            // 将 POJO 转换为 DTO
            List<Diary> diaries = new ArrayList<>();
            for (com.lyric.lyric.POJO.diary.DiaryPojo diaryPojo : diaryPos) {
                diaries.add(diaryMapStruct.toDto(diaryPojo));
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据月份查询日记列表
     *
     * @param year  年份
     * @param month 月份（1-12）
     * @return 日记列表
     */
    public Result<List<Diary>> getDiariesByMonth(Integer year, Integer month) {
        try {
            log.info("根据月份查询日记，year={}, month={}", year, month);

            // 验证月份参数
            if (year == null || month == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            if (month < 1 || month > 12) {
                throw new BusinessException(BusinessErrorMsgEnums.MONTHS_OUT_OF_RANGE);
            }

            // 查询指定月份的日记
            List<DiaryPojo> diaryPos = diaryMapper.selectByMonth(year, month);

            // 将 POJO 转换为 DTO
            List<Diary> diaries = new ArrayList<>();
            for (DiaryPojo diaryPojo : diaryPos) {
                diaries.add(diaryMapStruct.toDto(diaryPojo));
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取最早的日记日期
     *
     * @return 最早的日记日期
     */
    public Result<String> getEarliestDiaryDate() {
        try {
            log.info("获取最早的日记日期");

            // 查询最早的日记日期
            String earliestDate = diaryMapper.selectEarliestDiaryDate();

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, earliestDate);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== AI 分析相关 ====================

    /**
     * 批量 AI 标签分析
     * 对未分析的日记进行异步批量分析
     *
     * @param diaryIds 日记 ID 列表
     * @return 处理结果
     */
    public Result<Void> batchAiTagAnalysis(List<Integer> diaryIds) {
        try {
            log.info("批量 AI 标签分析，日记数量：{}", diaryIds != null ? diaryIds.size() : 0);

            // 验证输入参数
            if (diaryIds == null || diaryIds.isEmpty()) {
                return ResultBuilder.error(BusinessErrorMsgEnums.COMMON_PARAM_INVALID);
            }

            // 调用标签解析服务进行批量分析（异步执行）
            tagParsingService.batchTagAnalysis(diaryIds);

            return ResultBuilder.success(SuccessMsgEnums.ANALYSIS_STARTED);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }
}