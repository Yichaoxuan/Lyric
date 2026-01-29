package com.lyric.lyric.Service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lyric.lyric.DTO.content.Diary;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.content.DiaryPojo;
import com.lyric.lyric.POJO.tag.entityTag.EventPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TagParsingService {

    private final AIAnalysisService aiAnalysisService;

    private final DiaryMapper diaryMapper;


    private final EventMapper eventMapper;

    private final PersonsService personsService;

    private final LocationService locationService;

    private final BaseTagService baseTagService;

    public TagParsingService(AIAnalysisService aiAnalysisService, DiaryMapper diaryMapper,
                             EventMapper eventMapper, PersonsService personsService,
                             LocationService locationService, BaseTagService baseTagService) {
        this.aiAnalysisService = aiAnalysisService;
        this.diaryMapper = diaryMapper;
        this.eventMapper = eventMapper;
        this.personsService = personsService;
        this.locationService = locationService;
        this.baseTagService = baseTagService;
    }

    /**
     * 调用AI进行分析
     *
     * @param diary    日记对象
     */
    @Async("aiAnalysisExecutor")
    public void tagAnalysis(Diary diary) throws JsonProcessingException {
        try {
            // 调用AI进行分析
            AITagJson aiTag = aiAnalysisService.tagAnalysis(diary.getContent());

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
            // 获取事件实体标签
            Map<String, AITagJson.EventInfo> eventInfoMap = entityTag.getEvent();

            // 转换为基础主题标签对象并插入数据库
            if (themes != null) {

                log.info("---开始进行主题标签处理---");

                for (AITagJson.ThemeTag theme : themes) {
                    // 判断返回的theme是否为空
                    if (theme == null) {
                        continue;
                    }
                    // 去重并插入数据库
                    baseTagService.themeTagDeduplication(diaryId, theme);
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
                    baseTagService.moodTagDeduplication(diaryId, mood);
                }

                log.info("---情绪标签处理结束---");
            }

            // 转换为人物实体标签对象并插入数据库
            if (personInfoMapMap != null) {

                log.info("---开始进行人物实体处理---");

                personInfoMapMap.forEach((name, person) -> {
                    // 调用人物去重处理器
                    personsService.personDeduplicator(diaryId, name, person);
                });

                log.info("---人物实体标签处理结束---");
            }

            // 转换为地点实体标签对象并插入数据库
            if (locationInfoMap != null) {
                locationInfoMap.forEach((name, location) -> {
                    locationService.locationDeduplicator(diaryId, name, location);
                });
            }

            //转换为事件实体标签对象并插入数据库
            if (eventInfoMap != null) {
                eventInfoMap.forEach((name, event) -> {
                    System.out.println(name);
                    if (name != null && event != null) {
                        eventMapper.insert(new EventPojo(name, event));
                    }
                });
            }

            log.info("标签分析处理完成");
        } catch (Exception e) {
            log.error("处理AI标签时发生异常", e);
        }
    }
}
