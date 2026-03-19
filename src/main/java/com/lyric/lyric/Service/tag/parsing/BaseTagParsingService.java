package com.lyric.lyric.Service.tag.parsing;

import com.lyric.lyric.Mapper.relation.DiaryTagMapper;
import com.lyric.lyric.Mapper.tag.TagMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import com.lyric.lyric.POJO.tag.BaseTagPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签处理服务类
 * 提供基础标签处理的相关方法
 *
 * @author Yichaoxuan
 * @since 2025-12-12
 */
@Slf4j
@Service
public class BaseTagParsingService {

    private final TagMapper tagMapper;

    private final DiaryTagMapper diaryTagMapper;

    public BaseTagParsingService(TagMapper tagMapper, DiaryTagMapper diaryTagMapper) {
        this.tagMapper = tagMapper;
        this.diaryTagMapper = diaryTagMapper;
    }
    /**
     * 主题标签去重器
     * 处理主题标签的去重逻辑
     * 
     * @param diaryId  日记ID
     * @param newThemeTag 主题标签对象
     */
    public void themeTagDeduplication(Integer diaryId, AITagJson.ThemeTag newThemeTag) {

        //获取已经存在的主题标签
        List<BaseTagPojo> themeTags = tagMapper.selectByTagType(BaseTagPojo.TagType.THEME);

        //标记标签是否存在
        boolean tagExist = false;

        for (BaseTagPojo baseTagPojo : themeTags) {
            //判断标签是否存在
            if (baseTagPojo.getName().equals(newThemeTag.getName())) {
                log.info("{} 主题标签已存在,更新使用次数", newThemeTag.getName());

                //更新使用次数,并添加关联
                updateTag(diaryId, baseTagPojo);

                //更新标识
                tagExist = true;
                break;
            }
        }

        if (!tagExist) {

            //不存在则插入标签
            log.info("[{}]主题标签不存在,插入新主题标签", newThemeTag.getName());

            // 插入标签,并获取标签ID
            BaseTagPojo baseTagPojo = new BaseTagPojo(newThemeTag);
            tagMapper.insert(baseTagPojo);
            int themeTagId = baseTagPojo.getId();

            //关联日记与主题标签
            diaryTagMapper.insert(new DiaryTagPojo(diaryId, themeTagId));
        }
    }

    /**
     * 情绪标签去重器
     * 处理情绪标签的去重逻辑
     * 
     * @param diaryId 日记ID
     * @param moodTag 情绪标签对象
     */
    public void moodTagDeduplication(Integer diaryId, AITagJson.MoodTag moodTag) {

        //获取已经存在的情绪标签
        List<BaseTagPojo> moodsTags = tagMapper.selectByTagType(BaseTagPojo.TagType.MOOD);

        //标记标签是否存在
        boolean tagExist = false;

        for (BaseTagPojo baseTagPojo : moodsTags) {
            //判断标签是否存在
            if (baseTagPojo.getName().equals(moodTag.getName())) {
                log.info("{} 情绪标签已存在,更新使用次数", moodTag.getName());

                //更新使用次数，并添加关联
                updateTag(diaryId, baseTagPojo);

                //更新标识
                tagExist = true;
                break;
            }
        }

        if (!tagExist) {

            // 不存在则插入新标签
            log.info("[{}]情绪标签不存在,插入新情绪标签", moodTag.getName());

            // 插入标签,并获取标签ID
            BaseTagPojo baseTagPojo = new BaseTagPojo(moodTag);
            tagMapper.insert(baseTagPojo);
            int moodTagId = baseTagPojo.getId();

            //关联日记与情绪标签
            diaryTagMapper.insert(new DiaryTagPojo(diaryId, moodTagId));
        }
    }

    /**
     * 更新标签，并添加关联
     *
     * @param diaryId 日记ID
     * @param tag 标签对象
     */
    private void updateTag(Integer diaryId, BaseTagPojo tag) {
        //更新标签使用次数
       tag.setUsageCount(tag.getUsageCount() + 1);
       //更新标签
        tagMapper.update(tag);

        int themeTagId = tag.getId();
        //判断是否已经关联过，如果已经关联过，则返回，避免重复关联
        if (diaryTagMapper.selectByDiaryIdAndTagId(diaryId, themeTagId) == null) {
            diaryTagMapper.insert(new DiaryTagPojo(diaryId, themeTagId));
        } else {
            log.info("{}标签已经与日记Id为{}的日记关联过,跳过关联", tag.getName(), diaryId);
        }
    }
}
