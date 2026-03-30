package com.lyric.lyric.Service.tag.parsing;

import com.lyric.lyric.DTO.diary.Diary;
import com.lyric.lyric.Mapper.diary.DiaryMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签解析服务类
 *
 * @author Yichaoxuan
 * @since 2026/03/24
 */
@Slf4j
@Service
public class TagParsingService {

    private final DiaryMapper diaryMapper;

    private final AIAnalysisService aiAnalysisService;

    private final PersonsParsingService personsParsingService;

    private final LocationParsingService locationParsingService;

    private final EventParsingService eventParsingService;

    private final BaseTagParsingService baseTagParsingService;

    public TagParsingService(AIAnalysisService aiAnalysisService, DiaryMapper diaryMapper, PersonsParsingService personsParsingService,
                             LocationParsingService locationParsingService, EventParsingService eventParsingService, BaseTagParsingService baseTagParsingService) {
        this.aiAnalysisService = aiAnalysisService;
        this.diaryMapper = diaryMapper;
        this.personsParsingService = personsParsingService;
        this.locationParsingService = locationParsingService;
        this.eventParsingService = eventParsingService;
        this.baseTagParsingService = baseTagParsingService;
     }

    /**
     * 调用AI进行分析
     *
     * @param diary    日记对象
     */
    @Async("aiAnalysisExecutor")
    public void tagAnalysis(Diary diary) {
        try {
            // 调用AI进行分析
            AITagJson aiTag = aiAnalysisService.tagAnalysis(diary.getContent(), DateTimeUtils.format(diary.getDiaryDate()));

            // 验证结果是否为空
            if (aiTag != null) {
                processAITag(diary.getId(), aiTag);
            } else {
               log.info("标签分析结果为空，可能是功能未开启");
            }
        } catch (Exception e) {
            log.error("处理AI标签,分析结果时发生异常", e);
        }
    }

    /**
     * 处理AI标签分析结果
     * @param diaryId 日记ID
     * @param aiTag AI分析结果
     */
    public void processAITag(Integer diaryId, AITagJson aiTag) {
        try {

            //获取日记
            DiaryPojo diary = diaryMapper.selectById(diaryId);

            //判断是否获取到日记
            if (diary == null) {
                log.warn("未找到该日记,日记Id为：{}", diaryId);
                // 等待一段时间后重试，以解决并发问题
                try {
                    Thread.sleep(1000);
                    diary = diaryMapper.selectById(diaryId);
                    if (diary == null) {
                        log.warn("重试后仍未找到该日记,日记Id为：{}", diaryId);
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("等待日记保存时被中断,日记Id为：{}", diaryId);
                    return;
                }
            }

            log.info("开始处理AI标签分析结果,日记Id为：{}", diaryId);

            //获取总结描述
            String summary = aiTag.getSummary();

            //更新总结描述
            diary.setSummary(summary);

            //更新日记
            diaryMapper.update(diary);

            log.info("总结描述为：{}" , summary);

            // 获取标签集合
            AITagJson.Labels labels = aiTag.getLabels();
            if (labels == null) {
                log.warn("标签集合为空");
                return;
            }

            // 获取基础标签
            AITagJson.Tag tag = labels.getTag();
            if (tag == null) {
                log.warn("基础标签为空");
                return;
            }

            List<AITagJson.ThemeTag> themes = tag.getThemes();
            List<AITagJson.MoodTag> moods = tag.getMoods();

            //获取实体标签
            AITagJson.EntityTag entityTag = labels.getEntityTag();
            if (entityTag == null) {
                log.warn("实体标签为空");
                return;
            }

            // 获取人物实体标签
            Map<String, AITagJson.PersonInfo> personInfoMapMap = entityTag.getPerson();
            // 获取地点实体标签
            Map<String, AITagJson.LocationInfo> locationInfoMap = entityTag.getLocation();
            // 获取父事件实体标签
            Map<String, AITagJson.TogEventInfo> TogEventInfoMap = entityTag.getEvent();

            // 转换为基础主题标签对象并插入数据库
            if (themes != null) {

                log.info("---开始进行主题标签处理---");

                for (AITagJson.ThemeTag theme : themes) {
                    // 判断返回的theme是否为空
                    if (theme == null) {
                        continue;
                    }
                    // 去重并插入数据库
                    baseTagParsingService.themeTagDeduplication(diaryId, theme);
                }

                log.info("---主题标签处理结束---");
            }

            // 转换为基础情绪标签对象并插入数据库
            if (moods != null) {

                log.info("---开始进行情绪标签处理---");

                for (AITagJson.MoodTag mood : moods) {
                    // 判断返回的mood是否为空
                    if (mood == null) {
                        continue;
                    }

                    // 去重并插入数据库
                    baseTagParsingService.moodTagDeduplication(diaryId, mood);
                }

                log.info("---情绪标签处理结束---");
            }

            Map<Integer, Integer> integerIntegerMap;

            // 转换为人物实体标签对象并插入数据库
            if (personInfoMapMap != null) {

                log.info("---开始进行人物实体处理---");

                // 调用人物去重处理器
                integerIntegerMap = personsParsingService.personDeduplication(diaryId, personInfoMapMap);

                log.info("---人物实体标签处理结束---");
            } else {
                integerIntegerMap = new HashMap<>();
            }

            // 转换为地点实体标签对象并插入数据库
            if (locationInfoMap != null) {

                log.info("---开始进行地点实体处理---");

                locationParsingService.locationDeduplication(diaryId, locationInfoMap);

                log.info("---地点实体标签处理结束---");
            }

            //转换为事件实体标签对象并插入数据库
            if (TogEventInfoMap != null) {

                log.info("---开始进行事件实体标签处理---");

                eventParsingService.eventDeduplication(diaryId, TogEventInfoMap,integerIntegerMap);

                log.info("---事件实体标签处理结束---");
            }

            // 将该日记标记为已分析
            diaryMapper.updateIsAnalyzed(diaryId, 1);

            log.info("标签分析处理完成");
        } catch (Exception e) {
            log.error("处理 AI 标签时发生异常", e);
        }
    }

    /**
     * 批量调用 AI 进行分析
     *
     * @param diaryIds 日记 ID 列表
     */
    @Async("aiAnalysisExecutor")
    public void batchTagAnalysis(List<Integer> diaryIds) {
        if (diaryIds == null || diaryIds.isEmpty()) {
            log.warn("日记 ID 列表为空，跳过批量分析");
            return;
        }

        log.info("开始批量 AI 标签分析，日记数量：{}", diaryIds.size());
        
        int successCount = 0;
        int failCount = 0;
        int skipCount = 0;

        for (Integer diaryId : diaryIds) {
            try {
                // 检查日记是否已被分析
                DiaryPojo diary = diaryMapper.selectById(diaryId);
                if (diary == null) {
                    log.warn("日记不存在，跳过，日记 Id：{}", diaryId);
                    failCount++;
                    continue;
                }

                if (diary.getIsAnalyzed() != null && diary.getIsAnalyzed() == 1) {
                    log.info("日记已被分析，跳过，日记 Id：{}", diaryId);
                    skipCount++;
                    continue;
                }

                // 调用 AI 进行分析
                AITagJson aiTag = aiAnalysisService.tagAnalysis(diary.getContent(), DateTimeUtils.format(diary.getDiaryDate()));

                // 验证结果是否为空
                if (aiTag != null) {
                    processAITag(diaryId, aiTag);
                    successCount++;
                    log.info("日记分析成功，日记 Id：{}", diaryId);
                } else {
                    log.info("标签分析结果为空，可能是功能未开启，日记 Id：{}", diaryId);
                    skipCount++;
                }
            } catch (Exception e) {
                log.error("处理 AI 标签分析结果时发生异常，日记 Id：{}", diaryId, e);
                failCount++;
            }
        }

        log.info("批量 AI 标签分析完成，总数：{}, 成功：{}, 失败：{}, 跳过：{}", 
                diaryIds.size(), successCount, failCount, skipCount);
    }
}
