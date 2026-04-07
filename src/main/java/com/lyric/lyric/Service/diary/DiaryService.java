package com.lyric.lyric.Service.diary;

import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.Mapper.diary.DiaryMapper;
import com.lyric.lyric.Mapper.relation.ActivityLocationMapper;
import com.lyric.lyric.Mapper.relation.ActivityPersonMapper;
import com.lyric.lyric.Mapper.relation.DiaryActivityMapper;
import com.lyric.lyric.Mapper.relation.DiaryTagMapper;
import com.lyric.lyric.Mapper.tag.TagMapper;
import com.lyric.lyric.Mapper.tag.entity.ActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.Service.fileUpload.MediaFileService;
import com.lyric.lyric.Service.tag.parsing.TagParsingService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.wordCount.WordCountUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 日记服务类
 * 提供日记相关处理方法
 *
 * @author Yichaoxuan
 * @since 2026/04/03
 */
@Slf4j
@Service
public class DiaryService {

    private final DiaryMapper diaryMapper;

    private final TagParsingService tagParsingService;

    private final MediaFileService mediaFileService;

    private final DiaryTagMapper diaryTagMapper;

    private final ActivityMapper activityMapper;

    private final DiaryActivityMapper diaryActivityMapper;

    private final ActivityPersonMapper activityPersonMapper;

    private final ActivityLocationMapper activityLocationMapper;

    private final EventMapper eventMapper;

    private final PersonMapper personMapper;

    private final LocationMapper locationMapper;

    private final TagMapper tagMapper;

    public DiaryService(DiaryMapper diaryMapper, TagParsingService tagParsingService, MediaFileService mediaFileService,
            DiaryTagMapper diaryTagMapper, ActivityMapper activityMapper, DiaryActivityMapper diaryActivityMapper,
            ActivityPersonMapper activityPersonMapper, ActivityLocationMapper activityLocationMapper,
            EventMapper eventMapper, PersonMapper personMapper, LocationMapper locationMapper, TagMapper tagMapper) {
        this.diaryMapper = diaryMapper;
        this.tagParsingService = tagParsingService;
        this.mediaFileService = mediaFileService;
        this.diaryTagMapper = diaryTagMapper;
        this.activityMapper = activityMapper;
        this.diaryActivityMapper = diaryActivityMapper;
        this.activityPersonMapper = activityPersonMapper;
        this.activityLocationMapper = activityLocationMapper;
        this.eventMapper = eventMapper;
        this.personMapper = personMapper;
        this.locationMapper = locationMapper;
        this.tagMapper = tagMapper;
    }

    // ==================== 日记创建相关 ====================

    /**
     * 创建日记草稿
     * 用于用户开始编辑时创建一个空的草稿记录，返回日记 ID 供文件上传使用
     *
     * @param contentType   内容类型，默认为 DIARY
     * @param contentFormat 内容格式，默认为 RICH_TEXT
     * @return 包含日记 ID 的创建结果
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Integer> createDraft(DiaryPojo.ContentType contentType, DiaryPojo.ContentFormat contentFormat) {
        try {

            // 创建 DiaryPojo 对象并设置默认值
            DiaryPojo diaryPojo = new DiaryPojo(contentType, contentFormat, DateTimeUtils.today());

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
     * 验证标题和内容后保存日记，非文章/笔记类型会异步进行 AI 标签分析
     *
     * @param diary 日记 POJO 对象，需包含标题、内容等必填字段
     * @return 创建结果
     * @throws BusinessException 当标题或内容为空时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> saveDiary(DiaryPojo diary) {

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

        // 统计字数
        diary.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

        // 设置是否为草稿
        diary.setIsDraft(0);

        try {
            if (diary.getContentType() == DiaryPojo.ContentType.ARTICLE
                    || diary.getContentType() == DiaryPojo.ContentType.NOTE) {
                diaryMapper.insert(diary);
                log.info("日记保存成功（文章/笔记类型，跳过AI分析），ID: {}", diary.getId());
                return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);
            }
            diaryMapper.insert(diary);
            log.info("日记已保存到数据库，ID: {}，isAnalyzed: {}，启动异步AI分析...", diary.getId(), diary.getIsAnalyzed());
            tagParsingService.tagAnalysis(diary);
            log.info("异步AI分析任务已提交，立即返回响应给前端");
            return ResultBuilder.success(SuccessMsgEnums.DIARY_SAVE_WITH_AI_ANALYSIS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 日记更新相关 ====================

    /**
     * 更新日记（从草稿状态）
     * 将草稿状态的日记更新为正式日记，修改日记内容并更新相关字段
     * 自动计算字数、写作时长，并对非文章/笔记类型进行 AI 标签分析
     *
     * @param diary 日记 POJO 对象，包含要更新的内容
     * @return 更新结果
     * @throws BusinessException 当日记 ID 不存在时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> updateDiaryFromDraft(DiaryPojo diary) {
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

            // 设置是否为草稿状态
            diary.setIsDraft(0);

            // 保留原有的创建时间
            diary.setCreatedAt(existingDiary.getCreatedAt());

            // 设置更新时间为当前时间
            diary.setUpdatedAt(DateTimeUtils.now());

            // 统计字数
            diary.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

            // 设置写作时长（如果前端已经计算）
            if (diary.getWritingDuration() != null) {
                diary.setWritingDuration(diary.getWritingDuration() + diary.getWritingDuration());
            }

            // 更新日记
            diaryMapper.update(diary);

            // 如果不是文章或笔记类型，异步调用标签分析
            if (diary.getContentType() != DiaryPojo.ContentType.ARTICLE
                    && diary.getContentType() != DiaryPojo.ContentType.NOTE) {
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
     * 直接更新日记信息，不改变草稿状态
     *
     * @param diary 日记 POJO 对象
     * @return 修改结果
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> modifyDiary(DiaryPojo diary) {
        try {
            diaryMapper.update(diary);
            return ResultBuilder.success(SuccessMsgEnums.MODIFY_SUCCESS);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 更新草稿
     * 更新草稿状态的日记，保留草稿标记
     *
     * @param diary 日记 POJO 对象
     * @return 更新结果
     * @throws BusinessException 当日记不存在时返回错误结果
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    public Result<Void> updateDraft(DiaryPojo diary) {
        try {

            log.info("更新草稿，diaryId={}", diary.getId());

            // 获取数据库中该日记的 POJO 对象
            DiaryPojo oldDiaryPojo = diaryMapper.selectById(diary.getId());

            // 检查日记是否存在
            if (oldDiaryPojo == null) {
                return ResultBuilder.error(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }

            // 设置草稿状态
            diary.setIsDraft(1);

            // 更新
            diaryMapper.update(diary);

            return ResultBuilder.success(SuccessMsgEnums.SAVE_SUCCESS);

        } catch (Exception e) {
            // 抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 删除与回收站管理 ====================

    /**
     * 将日记移至回收站
     * 软删除日记，设置 isDeleted=1
     *
     * @param diaryId 日记 ID
     * @return 操作结果
     * @throws BusinessException 当日记不存在时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> moveToTrash(Integer diaryId) {
        try {
            DiaryPojo diary = diaryMapper.selectById(diaryId);
            if (diary == null) {
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }
            cascadeDeleteRelatedData(diaryId);
            diary.setIsDeleted(1);
            diaryMapper.update(diary);
            return ResultBuilder.success(SuccessMsgEnums.MOVE_TO_TRASH_SUCCESS);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 恢复回收站中的日记
     * 将已删除的日记恢复到正常状态，设置 isDeleted=0
     *
     * @param diaryId 日记 ID
     * @return 恢复结果
     * @throws BusinessException 当日记不存在或不在回收站中时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
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
     * 该方法会删除日记记录及其关联的所有媒体文件，仅适用于草稿或已在回收站的日记
     *
     * @param diaryId 日记 ID
     * @return 删除结果
     * @throws BusinessException 当日记不存在、不在回收站中或非草稿状态时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
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
     * 从数据库中批量删除草稿状态的日记
     *
     * @param diaryIds 草稿 ID 列表
     * @return 删除结果
     * @throws SystemException 当数据库操作失败时抛出系统异常
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
     * 从数据库中批量删除已在回收站的日记
     *
     * @param diaryIds 回收站日记 ID 列表
     * @return 删除结果
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDeleteTrashedDiaries(List<Integer> diaryIds) {
        try {
            log.info("批量删除回收站日记，数量：{}", diaryIds != null ? diaryIds.size() : 0);
            if (diaryIds == null || diaryIds.isEmpty()) {
                return ResultBuilder.error(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }
            for (Integer diaryId : diaryIds) {
                cascadeDeleteRelatedData(diaryId);
            }
            int deletedCount = diaryMapper.batchDeleteTrashedDiaries(diaryIds);
            log.info("实际删除回收站日记数量：{}", deletedCount);
            return ResultBuilder.success(SuccessMsgEnums.DELETE_SUCCESS);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    private void cascadeDeleteRelatedData(Integer diaryId) {
        log.info("开始级联删除日记{}的关联数据", diaryId);
        List<DiaryTagPojo> diaryTags = diaryTagMapper.selectByDiaryId(diaryId);
        if (diaryTags != null) {
            for (DiaryTagPojo dt : diaryTags) {
                int count = tagMapper.countDiariesByTagId(dt.getTagId());
                if (count <= 1) {
                    tagMapper.deleteById(dt.getTagId());
                    log.info("删除无其他关联的标签: {}", dt.getTagId());
                }
            }
            diaryTagMapper.deleteByDiaryId(diaryId);
        }
        List<Integer> activityIds = diaryActivityMapper.selectByDiaryId(diaryId);
        if (activityIds != null && !activityIds.isEmpty()) {
            for (Integer activityId : activityIds) {
                deleteActivityIfOrphaned(activityId);
            }
            diaryActivityMapper.deleteByDiaryId(diaryId);
        }
        log.info("日记{}的关联数据清理完成", diaryId);
    }

    private void deleteActivityIfOrphaned(Integer activityId) {
        int diaryCount = activityMapper.countDiariesByActivityId(activityId);
        if (diaryCount > 1) {
            return;
        }
        log.info("活动{}只关联当前日记，执行级联删除", activityId);
        ActivityPojo activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return;
        }
        List<Integer> personIds = activityPersonMapper.selectPersonIdsByActivityId(activityId);
        if (personIds != null) {
            for (Integer personId : personIds) {
                int personActivityCount = personMapper.countActivitiesByPersonId(personId);
                if (personActivityCount <= 1) {
                    personMapper.deleteById(personId);
                    log.info("删除无其他关联的人物: {}", personId);
                }
            }
        }
        activityPersonMapper.deleteByActivityId(activityId);
        activityLocationMapper.deleteByActivityId(activityId);
        activityMapper.deleteById(activityId);
        Integer eventId = activity.getEventId();
        if (eventId != null) {
            int activityCountForEvent = eventMapper.countActivitiesByEventId(eventId);
            if (activityCountForEvent <= 1) {
                eventMapper.deleteEventById(eventId);
                log.info("删除无其他关联的事件: {}", eventId);
            }
        }
    }

    // ==================== 基础查询相关 ====================

    /**
     * 查询日记
     * 根据日记 ID 获取日记详情
     *
     * @param diaryId 日记 ID
     * @return 查询结果，包含日记详细信息
     * @throws BusinessException 当日记不存在时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    public Result<DiaryPojo> queryDiary(Integer diaryId) {
        try {
            DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
            if (diaryPojo == null) {
                // 抛出业务异常，由全局异常处理器处理
                throw new BusinessException(BusinessErrorMsgEnums.DIARY_NOT_FOUND);
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaryPojo);
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
     * 查询数据库中所有日记记录（包括草稿和已删除）
     *
     * @return 所有日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getAllDiaryList() {
        try {
            log.info("查询全部日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectAll();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryPojoList);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取所有非草稿日记列表
     * 查询所有正式日记（排除草稿）
     *
     * @return 非草稿日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getNonDraftDiaries() {
        try {
            log.info("查询非草稿日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectNonDraftDiaries();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryPojoList);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取所有草稿列表
     * 查询所有草稿状态的日记
     *
     * @return 草稿列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDrafts() {
        try {
            log.info("查询草稿");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectDrafts();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryPojoList);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取所有回收站日记列表
     * 查询所有已删除但尚未彻底删除的日记
     *
     * @return 回收站日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getTrashedDiaries() {
        try {
            log.info("查询回收站日记");
            List<DiaryPojo> diaryPojoList = diaryMapper.selectTrashedDiaries();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    diaryPojoList);
        } catch (Exception e) {
            // 数据库操作异常，抛出系统异常
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 按关联条件查询 ====================

    /**
     * 根据标签 ID 查询日记列表
     * 通过标签关联关系查询该标签下的所有日记
     *
     * @param tagId 标签 ID
     * @return 日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByTagId(Integer tagId) {
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
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryPojo);
                }
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据事件 ID 查询日记列表
     * 通过事件→活动→日记的关联关系查询与该事件关联的所有日记
     *
     * @param eventId 事件 ID
     * @return 日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByEventId(Integer eventId) {
        try {
            log.info("根据事件 ID 查询日记，eventId={}", eventId);

            // 步骤 1: 查询该事件下的所有活动
            List<ActivityPojo> activities = activityMapper.selectByEventId(eventId);

            if (activities == null || activities.isEmpty()) {
                log.debug("事件下没有活动：eventId={}", eventId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 2: 收集所有活动关联的日记 ID（使用 Set 去重）
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (ActivityPojo activity : activities) {
                List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(activity.getId());
                if (relatedDiaryIds != null && !relatedDiaryIds.isEmpty()) {
                    diaryIds.addAll(relatedDiaryIds);
                }
            }

            if (diaryIds.isEmpty()) {
                log.debug("事件的活动未关联任何日记：eventId={}", eventId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 3: 批量查询日记详情
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryPojo);
                } else {
                    log.warn("日记不存在，跳过：diaryId={}", diaryId);
                }
            }

            log.info("根据事件 ID 查询到 {} 篇日记：eventId={}", diaries.size(), eventId);
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据活动ID 获取日记列表
     *
     * @param activityId 活动ID
     * @return 日记列表
     */
    public Result<List<DiaryPojo>> getDiariesByActivityId(Integer activityId) {
        try {
            log.info("根据活动 ID 查询日记：activityId={}", activityId);

            // 验证参数
            if (activityId == null) {
                log.warn("活动ID为空");
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 1: 查询该活动关联的所有日记 ID
            List<Integer> diaryIds = diaryActivityMapper.selectByActivityId(activityId);

            if (diaryIds == null || diaryIds.isEmpty()) {
                log.debug("活动未关联任何日记：activityId={}", activityId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 2: 批量查询日记详情
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary != null) {
                    diaries.add(diary);
                } else {
                    log.warn("日记不存在，跳过：diaryId={}", diaryId);
                }
            }

            log.info("根据活动 ID 查询到 {} 篇日记：activityId={}", diaries.size(), activityId);
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据人物 ID 查询日记列表
     * 通过人物→活动→日记的关联关系查询与该人物关联的所有日记
     *
     * @param personId 人物 ID
     * @return 日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByPersonId(Integer personId) {
        try {
            log.info("根据人物 ID 查询日记，personId={}", personId);

            // 步骤 1: 查询该人物参与的所有活动
            List<com.lyric.lyric.POJO.relation.ActivityPersonPojo> activityPersonRelations = activityPersonMapper
                    .selectByPersonId(personId);

            if (activityPersonRelations == null || activityPersonRelations.isEmpty()) {
                log.debug("人物未参与任何活动：personId={}", personId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 2: 收集所有活动关联的日记 ID（使用 Set 去重）
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.relation.ActivityPersonPojo relation : activityPersonRelations) {
                List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(relation.getActivityId());
                if (relatedDiaryIds != null && !relatedDiaryIds.isEmpty()) {
                    diaryIds.addAll(relatedDiaryIds);
                }
            }

            if (diaryIds.isEmpty()) {
                log.debug("人物参与的活动未关联任何日记：personId={}", personId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 3: 批量查询日记详情
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryPojo);
                } else {
                    log.warn("日记不存在，跳过：diaryId={}", diaryId);
                }
            }

            log.info("根据人物 ID 查询到 {} 篇日记：personId={}", diaries.size(), personId);
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据人物 ID 查询该人物参与的事件列表
     * 包含事件名称、活动数量、日记数量及角色信息
     *
     * @param personId 人物 ID
     * @return 事件列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    @SuppressWarnings("unchecked")
    public Result<List<java.util.Map<String, Object>>> getPersonEvents(Integer personId) {
        try {
            log.info("根据人物 ID 查询事件列表：personId={}", personId);

            List<com.lyric.lyric.POJO.relation.ActivityPersonPojo> activityPersonRelations = activityPersonMapper
                    .selectByPersonId(personId);

            if (activityPersonRelations == null || activityPersonRelations.isEmpty()) {
                log.debug("人物未参与任何活动：personId={}", personId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            java.util.Map<Integer, java.util.Map<String, Object>> eventMap = new java.util.LinkedHashMap<>();

            for (com.lyric.lyric.POJO.relation.ActivityPersonPojo relation : activityPersonRelations) {
                ActivityPojo activity = activityMapper.selectById(relation.getActivityId());
                if (activity == null || activity.getEventId() == null)
                    continue;

                Integer eventId = activity.getEventId();
                if (!eventMap.containsKey(eventId)) {
                    com.lyric.lyric.POJO.tag.entityTag.event.EventPojo event = eventMapper.selectEventById(eventId);
                    if (event == null)
                        continue;

                    java.util.Map<String, Object> eventInfo = new java.util.LinkedHashMap<>();
                    eventInfo.put("eventId", eventId);
                    eventInfo.put("eventName", event.getName());
                    eventInfo.put("activities", new ArrayList<java.util.Map<String, Object>>());
                    eventInfo.put("diaryCount", 0);
                    eventMap.put(eventId, eventInfo);
                }

                java.util.Map<String, Object> eventInfo = eventMap.get(eventId);
                List<java.util.Map<String, Object>> activities = (List<java.util.Map<String, Object>>) eventInfo
                        .get("activities");

                java.util.Map<String, Object> activityInfo = new java.util.LinkedHashMap<>();
                activityInfo.put("activityId", activity.getId());
                activityInfo.put("activityName", activity.getName());
                activityInfo.put("role", relation.getRole());
                activityInfo.put("activityDate", activity.getActivityDate());

                List<Integer> diaryIds = diaryActivityMapper.selectByActivityId(activity.getId());
                activityInfo.put("diaryIds", diaryIds != null ? diaryIds : new ArrayList<>());

                activities.add(activityInfo);

                int currentCount = (int) eventInfo.get("diaryCount");
                eventInfo.put("diaryCount", currentCount + (diaryIds != null ? diaryIds.size() : 0));
            }

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS,
                    new ArrayList<>(eventMap.values()));
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据地点 ID 查询日记列表
     * 通过地点→活动→日记的关联关系查询与该地点关联的所有日记
     *
     * @param locationId 地点 ID
     * @return 日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByLocationId(Integer locationId) {
        try {
//            log.info("根据地点 ID 查询日记，locationId={}", locationId);

            // 步骤 1: 查询该地点关联的所有活动
            List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> activityLocationRelations = activityLocationMapper
                    .selectByLocationId(locationId);

            if (activityLocationRelations == null || activityLocationRelations.isEmpty()) {
                log.debug("地点未关联任何活动：locationId={}", locationId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 2: 收集所有活动关联的日记 ID（使用 Set 去重）
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : activityLocationRelations) {
                List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(relation.getActivityId());
                if (relatedDiaryIds != null && !relatedDiaryIds.isEmpty()) {
                    diaryIds.addAll(relatedDiaryIds);
                }
            }

            if (diaryIds.isEmpty()) {
                log.debug("地点关联的活动未关联任何日记：locationId={}", locationId);
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }

            // 步骤 3: 批量查询日记详情
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diaryPojo = diaryMapper.selectById(diaryId);
                if (diaryPojo != null) {
                    diaries.add(diaryPojo);
                } else {
                    log.warn("日记不存在，跳过：diaryId={}", diaryId);
                }
            }

//            log.info("根据地点 ID 查询到 {} 篇日记：locationId={}", diaries.size(), locationId);
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 按时间查询 ====================

    /**
     * 根据日期查询日记列表
     * 查询指定日期创建的所有日记
     *
     * @param date 日记日期（格式：YYYY-MM-DD）
     * @return 日记列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByDate(String date) {
        try {
            log.info("根据日期查询日记，date={}", date);

            // 查询指定日期的日记
            List<com.lyric.lyric.POJO.diary.DiaryPojo> diary = diaryMapper.selectByDate(date);

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diary);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据月份查询日记列表
     * 查询指定年份和月份的所有日记
     *
     * @param year  年份
     * @param month 月份（1-12）
     * @return 日记列表
     * @throws BusinessException 当参数为空或月份超出范围时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> getDiariesByMonth(Integer year, Integer month) {
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
            List<DiaryPojo> diary = diaryMapper.selectByMonth(year, month);

            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diary);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 获取最早的日记日期
     * 查询数据库中创建时间最早的日记日期
     *
     * @return 最早的日记日期
     * @throws SystemException 当数据库操作失败时抛出系统异常
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

    /**
     * 搜索日记
     * 根据关键词搜索日记
     *
     * @param keyword 搜索关键词
     * @return 搜索结果列表
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<List<DiaryPojo>> searchDiaries(String keyword) {
        try {
            log.info("搜索日记，keyword={}", keyword);
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }
            List<DiaryPojo> diaries = diaryMapper.searchByKeyword(keyword.trim());
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 查询未分析的日记数量
     *
     * @return 未分析的日记数量
     * @throws SystemException 当数据库操作失败时抛出系统异常
     */
    public Result<Integer> getUnanalyzedCount() {
        try {
            log.info("查询未分析日记数量");
            Integer count = diaryMapper.countUnanalyzedDiaries();
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, count != null ? count : 0);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== 标签搜索相关 ====================

    /**
     * 根据关键词搜索主题/情绪标签关联的日记
     *
     * @param keyword 搜索关键词
     * @return 匹配的日记列表
     */
    public Result<List<DiaryPojo>> searchDiariesByBaseTag(String keyword) {
        try {
            log.info("搜索主题/情绪标签关联日记，keyword={}", keyword);
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }
            List<com.lyric.lyric.POJO.tag.BaseTagPojo> tags = tagMapper.searchByKeyword(keyword.trim());
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.tag.BaseTagPojo tag : tags) {
                List<DiaryTagPojo> relations = diaryTagMapper.selectByTagId(tag.getId());
                for (DiaryTagPojo relation : relations) {
                    diaryIds.add(relation.getDiaryId());
                }
            }
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary != null) {
                    diaries.add(diary);
                }
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据关键词搜索人物标签关联的日记
     *
     * @param keyword 搜索关键词
     * @return 匹配的日记列表
     */
    public Result<List<DiaryPojo>> searchDiariesByPerson(String keyword) {
        try {
            log.info("搜索人物标签关联日记，keyword={}", keyword);
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }
            List<com.lyric.lyric.POJO.tag.entityTag.PersonPojo> persons = personMapper.searchByKeyword(keyword.trim());
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.tag.entityTag.PersonPojo person : persons) {
                List<com.lyric.lyric.POJO.relation.ActivityPersonPojo> activityPersons = activityPersonMapper
                        .selectByPersonId(person.getId());
                if (activityPersons != null) {
                    for (com.lyric.lyric.POJO.relation.ActivityPersonPojo ap : activityPersons) {
                        List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(ap.getActivityId());
                        if (relatedDiaryIds != null) {
                            diaryIds.addAll(relatedDiaryIds);
                        }
                    }
                }
            }
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary != null) {
                    diaries.add(diary);
                }
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据关键词搜索地点标签关联的日记
     *
     * @param keyword 搜索关键词
     * @return 匹配的日记列表
     */
    public Result<List<DiaryPojo>> searchDiariesByLocation(String keyword) {
        try {
            log.info("搜索地点标签关联日记，keyword={}", keyword);
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }
            List<com.lyric.lyric.POJO.tag.entityTag.LocationPojo> locations = locationMapper
                    .searchByKeyword(keyword.trim());
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.tag.entityTag.LocationPojo location : locations) {
                List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> activityLocations = activityLocationMapper
                        .selectByLocationId(location.getId());
                if (activityLocations != null) {
                    for (com.lyric.lyric.POJO.relation.ActivityLocationPojo al : activityLocations) {
                        List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(al.getActivityId());
                        if (relatedDiaryIds != null) {
                            diaryIds.addAll(relatedDiaryIds);
                        }
                    }
                }
            }
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary != null) {
                    diaries.add(diary);
                }
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 根据关键词搜索事件标签关联的日记
     *
     * @param keyword 搜索关键词
     * @return 匹配的日记列表
     */
    public Result<List<DiaryPojo>> searchDiariesByEvent(String keyword) {
        try {
            log.info("搜索事件标签关联日记，keyword={}", keyword);
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, new ArrayList<>());
            }
            List<com.lyric.lyric.POJO.tag.entityTag.event.EventPojo> events = eventMapper
                    .searchByKeyword(keyword.trim());
            java.util.Set<Integer> diaryIds = new java.util.HashSet<>();
            for (com.lyric.lyric.POJO.tag.entityTag.event.EventPojo event : events) {
                List<ActivityPojo> activities = activityMapper.selectByEventId(event.getId());
                if (activities != null) {
                    for (ActivityPojo activity : activities) {
                        List<Integer> relatedDiaryIds = diaryActivityMapper.selectByActivityId(activity.getId());
                        if (relatedDiaryIds != null) {
                            diaryIds.addAll(relatedDiaryIds);
                        }
                    }
                }
            }
            List<DiaryPojo> diaries = new ArrayList<>();
            for (Integer diaryId : diaryIds) {
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary != null) {
                    diaries.add(diary);
                }
            }
            return ResultBuilder.successWithData(SuccessMsgEnums.QUERY_SUCCESS, diaries);
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    // ==================== AI 分析相关 ====================

    /**
     * 批量 AI 标签分析
     * 对未分析的日记进行异步批量分析，生成智能标签
     *
     * @param diaryIds 日记 ID 列表
     * @return 处理结果
     * @throws BusinessException 当参数无效时返回错误结果
     * @throws SystemException   当数据库操作失败时抛出系统异常
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

    /**
     * 批量导入Markdown文件
     * 解析Markdown文件内容并创建日记记录，自动进行AI分析
     *
     * @param files Markdown文件数组
     * @return 导入结果
     * @throws BusinessException 当文件为空或格式不正确时抛出业务异常
     * @throws SystemException   当数据库操作失败时抛出系统异常
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchImportMarkdown(MultipartFile[] files) {
        try {
            log.info("批量导入Markdown文件，文件数量：{}", files != null ? files.length : 0);

            if (files == null || files.length == 0) {
                throw new BusinessException(BusinessErrorMsgEnums.COMMON_PARAM_INVALID);
            }

            List<Integer> importedDiaryIds = new java.util.ArrayList<>();

            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".md")) {
                    log.warn("跳过非Markdown文件：{}", originalFilename);
                    continue;
                }

                String content = new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
                if (content.trim().isEmpty()) {
                    log.warn("跳过空文件：{}", originalFilename);
                    continue;
                }

                String title = extractTitle(content, originalFilename);
                java.time.LocalDate diaryDate = extractDate(content);
                String cleanContent = cleanContent(content);

                DiaryPojo diary = new DiaryPojo();
                diary.setTitle(title);
                diary.setContent(cleanContent);
                diary.setContentType(DiaryPojo.ContentType.DIARY);
                diary.setContentFormat(DiaryPojo.ContentFormat.MARKDOWN);
                diary.setIsDeleted(0);
                diary.setIsDraft(0);
                diary.setIsAnalyzed(0);
                diary.setEmotionalLevel(0.0);
                diary.setWordCount(WordCountUtils.calculateWordCount(cleanContent, DiaryPojo.ContentFormat.MARKDOWN));
                diary.setDiaryDate(diaryDate);

                diaryMapper.insert(diary);
                importedDiaryIds.add(diary.getId());
                log.info("成功导入日记：{}，日期：{}，ID：{}", title, diaryDate, diary.getId());
            }

            if (!importedDiaryIds.isEmpty()) {
                tagParsingService.batchTagAnalysis(importedDiaryIds);
                log.info("已对{}篇导入的日记启动AI分析", importedDiaryIds.size());
            }

            return ResultBuilder.success(SuccessMsgEnums.BATCH_IMPORT_SUCCESS);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     *
     * 提取Markdown文件的标题
     *
     */
    private String extractTitle(String content, String filename) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#")) {
                return line.replaceAll("^#+\\s*", "").trim();
            }
        }
        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * 提取Markdown文件的日期
     *
     */
    private java.time.LocalDate extractDate(String content) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern
                .compile("\\*\\*日期\\*\\*[：:]\\s*(\\d{4}-\\d{2}-\\d{2})");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            try {
                return java.time.LocalDate.parse(matcher.group(1));
            } catch (Exception e) {
                log.warn("日期解析失败，使用当前日期");
            }
        }
        return java.time.LocalDate.now();
    }

    /**
     * 清理Markdown文件内容
     * 去除标题和日期行，并去除多余的换行符
     *
     * @param content Markdown文件内容
     * @return 清理后的内容
     */
    private String cleanContent(String content) {
        String[] lines = content.split("\n");
        StringBuilder cleaned = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("# ") || trimmed.matches("^\\*\\*日期\\*\\*[：:].*$")) {
                continue;
            }
            if (!cleaned.isEmpty() || !trimmed.isEmpty()) {
                if (!cleaned.isEmpty())
                    cleaned.append("\n");
                cleaned.append(line);
            }
        }
        return cleaned.toString().trim();
    }
}