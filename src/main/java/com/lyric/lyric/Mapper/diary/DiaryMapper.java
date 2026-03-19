package com.lyric.lyric.Mapper.diary;

import com.lyric.lyric.POJO.diary.DiaryPojo;
import com.lyric.lyric.Service.weather.GetWeatherService;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 日记数据访问层接口
 */
@Mapper
public interface DiaryMapper {
    
    /**
     * 添加一条日记记录
     * @param diary 日记实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO diary(title, content, summary, content_type, content_format, is_deleted, is_draft, emotion_score, word_count, writing_start_time, writing_end_time, writing_duration, diary_date) " +
            "VALUES(#{title}, #{content}, #{summary}, #{contentType}, #{contentFormat}, #{isDeleted}, #{isDraft}, #{emotionScore}, #{wordCount}, #{writingStartTime}, #{writingEndTime}, #{writingDuration}, #{diaryDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DiaryPojo diary);
    
    /**
     * 根据ID查询日记
     * @param id 日记ID
     * @return 日记实体
     */
    @Select("SELECT * FROM diary WHERE id = #{id}")
    DiaryPojo selectById(Integer id);
    
    /**
     * 查询所有日记
     * @return 日记列表
     */
    @Select("SELECT * FROM diary")
    List<DiaryPojo> selectAll();

    /**
     * 查询所有日记中没有天气数据的日记
     * @return 日记列表
     */
    @Select("SELECT d.id AS diaryId, l.latitude, l.longitude, l.city " +
            "FROM diary d " +
            "LEFT JOIN weather w ON d.id = w.diary_id " +
            "LEFT JOIN (SELECT diary_id, MIN(location_id) AS location_id " +
            "FROM diary_location GROUP BY diary_id) first_loc ON d.id = first_loc.diary_id " +
            "LEFT JOIN location l ON first_loc.location_id = l.id " +
            "WHERE w.id IS NULL AND first_loc.diary_id IS NOT NULL")
    List<GetWeatherService.DiaryWeatherPending> selectDiariesWithoutWeather();
    
    /**
     * 更新日记
     * @param diary 日记实体
     * @return 影响的行数
     */
    @Update("UPDATE diary SET title=#{title}, content=#{content}, summary=#{summary}, " +
            "content_type=#{contentType}, content_format=#{contentFormat}, " +
            "is_deleted=#{isDeleted}, is_draft=#{isDraft}, emotion_score=#{emotionScore}, " +
            "word_count=#{wordCount}, writing_start_time=#{writingStartTime}, writing_end_time=#{writingEndTime}, " +
            "writing_duration=#{writingDuration}, " +
            "diary_date=#{diaryDate}, updated_at=CURRENT_TIMESTAMP WHERE id=#{id}")
    int update(DiaryPojo diary);
    
    /**
     * 根据ID删除日记
     * @param id 日记ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM diary WHERE id = #{id}")
    int deleteById(Integer id);
}