package com.lyric.lyric.Service.diary;


import com.lyric.lyric.Dto.content.Diary;
import com.lyric.lyric.MapStruct.content.DiaryMapStruct;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Pojo.content.DiaryPojo;
import com.lyric.lyric.Utils.DateTime.DateTimeUtils;
import com.lyric.lyric.Utils.ResultUtils.Result;
import com.lyric.lyric.Utils.WordCount.WordCountUtils;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DiaryService {

    @Accessors
    private final DiaryMapper diaryMapper;

    @Accessors
    private final DiaryMapStruct diaryMapStruct;

    public DiaryService(DiaryMapper diaryMapper, DiaryMapStruct diaryMapStruct) {
        this.diaryMapper = diaryMapper;
        this.diaryMapStruct = diaryMapStruct;
    }

    public Result insertDiary(Diary diary) {

        //创建DiaryPojo对象并赋值
        DiaryPojo diaryPojo = diaryMapStruct.toPojo(diary);

        //统计字数
        diaryPojo.setWordCount(WordCountUtils.calculateWordCount(diary.getContent(), diary.getContentFormat()));

        //计算写作时长
        diaryPojo.setWritingDuration(DateTimeUtils.timeBetween(diary.getWritingStartTime(), diary.getWritingEndTime()));

        //判断内容类型，如果是文章或笔记，直接保存
        if (diary.getContentType() == Diary.ContentType.ARTICLE || diary.getContentType() == Diary.ContentType.NOTE) {
            diaryMapper.insert(diaryPojo);
        }

        //判断是否开启AI分析

        diaryMapper.insert(diaryPojo);

        return new Result();

    }
}
