package com.lyric.lyric.Controller;

import com.lyric.lyric.Dto.content.Diary;
import com.lyric.lyric.Service.diary.DiaryService;
import com.lyric.lyric.Utils.ResultUtils.Result;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/diary")
public class DiaryController {

    @Accessors
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    // 日记相关接口
    /**
     * 创建日记
     *
     * @param diary 日记DTO对象
     * @return 创建结果
     */
//    @PostMapping("/insertDiary")
//    public Result insertDiary(@RequestBody Diary diary) {
//        return diaryService.insertDiary(diary);
//    }
}
