package com.lyric.lyric.Service.tag;

import com.lyric.lyric.Mapper.relation.DiaryTagMapper;
import com.lyric.lyric.Mapper.tag.TagMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import com.lyric.lyric.POJO.tag.BaseTagPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 提供基础标签处理的相关方法
 *
 * @author Yichaoxuan
 * @since 2025-12-12
 */
@Slf4j
@Service
public class BaseTagService {

    private final TagMapper tagMapper;

    private final DiaryTagMapper diaryTagMapper;

    public BaseTagService(TagMapper tagMapper, DiaryTagMapper diaryTagMapper) {
        this.tagMapper = tagMapper;
        this.diaryTagMapper = diaryTagMapper;
    }

    /**
     * 主题标签去重器
     * 处理主题标签的去重逻辑
     * 
     * @param diaryId  日记ID
     * @param themeTag 主题标签对象
     */
    public void themeTagDeduplication(Integer diaryId, AITagJson.ThemeTag themeTag) {
        //获取已经存在的主题标签
        List<BaseTagPojo> themeTags = tagMapper.selectByTagType(BaseTagPojo.TagType.THEME);

        //标记标签是否存在
        boolean tagExist = false;

        for (BaseTagPojo baseTagPojo : themeTags) {
            //判断标签是否存在
            if (baseTagPojo.getName().equals(themeTag.getName())) {
                log.info("{}标签已存在,更新使用次数", themeTag.getName());
                //更新使用次数
                baseTagPojo.setUsageCount(baseTagPojo.getUsageCount() + 1);
                tagMapper.update(baseTagPojo);
                int themeTagId = baseTagPojo.getId();
                //关联日记与主题标签
                if (diaryTagMapper.selectByDiaryIdAndTagId(diaryId, themeTagId) != null) {
                    diaryTagMapper.insert(new DiaryTagPojo(diaryId, themeTagId));
                }
                //更新标识
                tagExist = true;
                break;
            }
        }

        if (!tagExist) {
            //不存在则插入标签
            log.info("{}标签不存在,插入标签", themeTag.getName());
            int themeTagId = tagMapper.insert(new BaseTagPojo(themeTag));
            if(diaryTagMapper.selectByDiaryIdAndTagId(diaryId, themeTagId) != null) {
                return; // 如果已经关联过，则返回，避免重复关联
            }
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
                //更新使用次数
                baseTagPojo.setUsageCount(baseTagPojo.getUsageCount() + 1);
                tagMapper.update(baseTagPojo);
                int moodTagId = baseTagPojo.getId();
                //关联日记与情绪标签
                if (diaryTagMapper.selectByDiaryIdAndTagId(diaryId, moodTagId) == null) {
                    diaryTagMapper.insert(new DiaryTagPojo(diaryId, moodTagId));
                }
                //更新标识
                tagExist = true;
                break;
            }
        }

        //不存在则插入新标签
        if (!tagExist) {
            int moodTagId = tagMapper.insert(new BaseTagPojo(moodTag));
            if(diaryTagMapper.selectByDiaryIdAndTagId(diaryId, moodTagId) != null) {
                return; // 如果已经关联过，则返回，避免重复关联
            }
            //关联日记与情绪标签
            diaryTagMapper.insert(new DiaryTagPojo(diaryId, moodTagId));
        }
    }
}
