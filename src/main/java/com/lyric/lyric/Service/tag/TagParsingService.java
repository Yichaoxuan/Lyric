package com.lyric.lyric.Service.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lyric.lyric.Dto.content.Diary;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Mapper.relation.*;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.Mapper.tag.TagMapper;
import com.lyric.lyric.Pojo.AI.AITagJson;
import com.lyric.lyric.Pojo.content.DiaryPojo;
import com.lyric.lyric.Pojo.relation.DiaryPersonPojo;
import com.lyric.lyric.Pojo.relation.DiaryTagPojo;
import com.lyric.lyric.Pojo.relation.EventPersonPojo;
import com.lyric.lyric.Pojo.tag.TagPojo;
import com.lyric.lyric.Pojo.tag.entityTag.EventPojo;
import com.lyric.lyric.Pojo.tag.entityTag.LocationPojo;
import com.lyric.lyric.Pojo.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TagParsingService {

    private static final Logger logger = LoggerFactory.getLogger(TagParsingService.class);

    private final AIAnalysisService aiAnalysisService;

    private final DiaryMapper diaryMapper;

    private final TagMapper tagMapper;

    private final PersonMapper personMapper;

    private final LocationMapper locationMapper;

    private final EventMapper eventMapper;

    private final DiaryTagMapper diaryTagMapper;

    private final DiaryPersonMapper diaryPersonMapper;

    private final DiaryLocationMapper diaryLocationMapper;

    private  final DiaryEventMapper diaryEventMapper;

    private  final EventPersonMapper eventPersonMapper;

    private final EventLocationMapper eventLocationMapper;

    public TagParsingService(AIAnalysisService aiAnalysisService, DiaryMapper diaryMapper,
                             TagMapper tagMapper, PersonMapper personMapper, LocationMapper locationMapper,
                             EventMapper eventMapper, DiaryTagMapper diaryTagMapper, DiaryPersonMapper diaryPersonMapper,
                             DiaryLocationMapper diaryLocationMapper, DiaryEventMapper diaryEventMapper,
                             EventPersonMapper eventPersonMapper, EventLocationMapper eventLocationMapper) {
        this.aiAnalysisService = aiAnalysisService;
        this.diaryMapper = diaryMapper;
        this.tagMapper = tagMapper;
        this.personMapper = personMapper;
        this.locationMapper = locationMapper;
        this.eventMapper = eventMapper;
        this.diaryTagMapper = diaryTagMapper;
        this.diaryPersonMapper = diaryPersonMapper;
        this.diaryLocationMapper = diaryLocationMapper;
        this.diaryEventMapper = diaryEventMapper;
        this.eventPersonMapper = eventPersonMapper;
        this.eventLocationMapper = eventLocationMapper;
    }

    /**
     * 调用AI进行分析并解析接收到的json格式的标签数据
     *
      * @param diaryId    日记ID
     */
    @Async("aiAnalysisExecutor")
    public void tagAnalysis(Integer diaryId, String content) throws JsonProcessingException {
        try {
            // 调用AI进行分析
            AITagJson aiTag = aiAnalysisService.tagAnalysis(diaryId, content);

            // 验证结果是否为空
            if (aiTag != null) {
                processAITag(diaryId, aiTag);
            } else {
                logger.info("标签分析结果为空，可能是功能未开启");
            }
        } catch (Exception e) {
            logger.error("处理AI标签分析结果时发生异常", e);
        }
    }

    /**
     * 处理AI标签分析结果
     * @param diaryId 日记ID
     * @param aiTag AI分析结果
     */
    private void processAITag(Integer diaryId, AITagJson aiTag) {
        try {
            //获取总结描述
            String summary = aiTag.getSummary();

            //获取日记
            DiaryPojo diary = diaryMapper.selectById(diaryId);

            //更新总结描述
            diary.setSummary(summary);

            //更新日记
            diaryMapper.update(diary);

            // 获取标签集合
            AITagJson.Labels labels = aiTag.getLabels();
            if (labels == null) {
                logger.warn("标签集合为空");
                return;
            }

            // 获取基础标签
            AITagJson.Tag tag = labels.getTag();
            if (tag == null) {
                logger.warn("基础标签为空");
                return;
            }

            List<AITagJson.ThemeLabel> themes = tag.getThemes();
            List<AITagJson.MoodLabel> moods = tag.getMoods();

            //获取实体标签
            AITagJson.EntityTag entityTag = labels.getEntityTag();
            if (entityTag == null) {
                logger.warn("实体标签为空");
                return;
            }

            // 获取人物实体标签
            Map<String, AITagJson.PersonInfo> persons = entityTag.getPerson();
            // 获取地点实体标签
            Map<String, AITagJson.LocationInfo> locations = entityTag.getLocation();
            // 获取事件实体标签
            Map<String, AITagJson.EventInfo> events = entityTag.getEvent();

            //转换为基础主题标签对象并插入数据库
            if (themes != null) {
                for (AITagJson.ThemeLabel theme : themes) {
                    if (theme != null) {
                        List<TagPojo> themeTags = tagMapper.selectByTagType(TagPojo.TagType.THEME);
                        for (TagPojo tagPojo : themeTags) {
                            //判断标签是否存在
                            if (tagPojo.getName().equals(theme.getName())) {
                                //更新使用次数
                                tagPojo.setUsageCount(tagPojo.getUsageCount() + 1);
                                tagMapper.update(tagPojo);
                            }
                        }
                        int themeTagId = tagMapper.insert(new TagPojo(theme));
                        //关联日记与主题标签
                        diaryTagMapper.insert(new DiaryTagPojo(diaryId, themeTagId));
                    }
                }
            }

            //转换为基础情绪标签对象并插入数据库
            if (moods != null) {
                for (AITagJson.MoodLabel mood : moods) {
                    List<TagPojo> moodsTags = tagMapper.selectByTagType(TagPojo.TagType.MOOD);
                    for (TagPojo tagPojo : moodsTags) {
                        //判断标签是否存在
                        if (tagPojo.getName().equals(mood.getName())) {
                            //更新使用次数
                            tagPojo.setUsageCount(tagPojo.getUsageCount() + 1);
                            tagMapper.update(tagPojo);
                        }
                    }
                    int moodTagId = tagMapper.insert(new TagPojo(mood));
                    //关联日记与情绪标签
                    diaryTagMapper.insert(new DiaryTagPojo(diaryId, moodTagId));

                }
            }

            //转换为人物实体标签对象并插入数据库
            if (persons != null) {
                persons.forEach((name, person) -> {
                    System.out.println(name);
                    if (name != null && person != null) {

                        PersonPojo personPojo = personMapper.selectByName(name);
                        //判断人物标签是否存在
                        if (personPojo != null && !personPojo.getRelation().equals(person.getRelationship())) {
                            //关联日记与人物标签
                            diaryPersonMapper.insert(new DiaryPersonPojo(diaryId, personPojo.getId()));
                        } else if (personPojo != null) {
                            personMapper.insert(new PersonPojo(name, person));
                        } else if (personPojo == null) {

                        }
                    }
                });
            }

            //
            if (locations != null) {
                locations.forEach((name, location) -> {
                    System.out.println(name);
                    if (name != null && location != null) {
                        locationMapper.insert(new LocationPojo(name, location));
                    }
                });
            }

            //转换为事件实体标签对象并插入数据库
            if (events != null) {
                events.forEach((name, event) -> {
                    System.out.println(name);
                    if (name != null && event != null) {
                        eventMapper.insert(new EventPojo(name, event));
                    }
                });
            }

            logger.info("标签分析处理完成");
            logger.debug("标签描述：{}", summary);
            logger.debug("基础标签数量 - 主题: {}, 心情: {}",
                themes != null ? themes.size() : 0,
                moods != null ? moods.size() : 0);
            logger.debug("实体标签数量 - 人物: {}, 地点: {}, 事件: {}",
                persons != null ? persons.size() : 0,
                locations != null ? locations.size() : 0,
                events != null ? events.size() : 0);
        } catch (Exception e) {
            logger.error("处理AI标签时发生异常", e);
        }
    }
}
