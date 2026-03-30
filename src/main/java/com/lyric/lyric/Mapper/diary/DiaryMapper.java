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
         * 
         * @param diary 日记实体
         * @return 影响的行数
         */
        @Insert("INSERT INTO diary(title, content, summary, content_type, " +
                        "content_format, is_deleted, is_draft, is_analyzed, emotional_level, " +
                        "word_count, writing_duration, diary_date) "
                        +
                        "VALUES(#{title}, #{content}, #{summary}, #{contentType}, " +
                        "#{contentFormat}, #{isDeleted}, #{isDraft}, #{isAnalyzed}, #{emotionalLevel}, " +
                        "#{wordCount}, #{writingDuration}, #{diaryDate})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(DiaryPojo diary);

        /**
         * 根据ID查询日记
         * 
         * @param id 日记ID
         * @return 日记实体
         */
        @Select("SELECT * FROM diary WHERE id = #{id}")
        DiaryPojo selectById(Integer id);

        /**
         * 查询所有日记
         * 
         * @return 日记列表
         */
        @Select("SELECT * FROM diary")
        List<DiaryPojo> selectAll();

        /**
         * 查询所有非草稿日记
         * 
         * @return 日记列表
         */
        @Select("SELECT * FROM diary WHERE is_draft = 0 AND is_deleted = 0")
        List<DiaryPojo> selectNonDraftDiaries();

        /**
         * 查询所有草稿
         * 
         * @return 草稿列表
         */
        @Select("SELECT * FROM diary WHERE is_draft = 1 AND is_deleted = 0")
        List<DiaryPojo> selectDrafts();

        /**
         * 查询所有回收站日记
         * 
         * @return 回收站日记列表
         */
        @Select("SELECT * FROM diary WHERE is_deleted = 1")
        List<DiaryPojo> selectTrashedDiaries();

        /**
         * 查询所有日记中没有天气数据的日记
         * 
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
         * 
         * @param diary 日记实体
         * @return 影响的行数
         */
        @Update("UPDATE diary SET title=#{title}, content=#{content}, summary=#{summary}, " +
                        "content_type=#{contentType}, content_format=#{contentFormat}, " +
                        "is_deleted=#{isDeleted}, is_draft=#{isDraft}, is_analyzed=#{isAnalyzed}, emotional_level=#{emotionalLevel}, "
                        +
                        "word_count=#{wordCount}, writing_duration=#{writingDuration}, " +
                        "diary_date=#{diaryDate}, updated_at=CURRENT_TIMESTAMP WHERE id=#{id}")
        int update(DiaryPojo diary);

        /**
         * 根据ID删除日记
         * 
         * @param id 日记ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM diary WHERE id = #{id}")
        int deleteById(Integer id);

        /**
         * 批量删除草稿
         *
         * @param diaryIds 草稿ID列表
         * @return 影响的行数
         */
        @Delete("<script>DELETE FROM diary WHERE is_draft = 1 AND is_deleted = 0 AND id IN <foreach collection='diaryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
        int batchDeleteDrafts(List<Integer> diaryIds);

        /**
         * 批量删除回收站中的日记
         *
         * @param diaryIds 回收站日记ID列表
         * @return 影响的行数
         */
        @Delete("<script>DELETE FROM diary WHERE is_deleted = 1 AND id IN <foreach collection='diaryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
        int batchDeleteTrashedDiaries(List<Integer> diaryIds);

        /**
         * 根据 ID 更新日记的 emotional_level 字段
         * 
         * @param diaryId 日记 ID
         * @param level   情绪等级
         */
        @Update("UPDATE diary SET emotional_level = #{level} WHERE id = #{diaryId}")
        void updateEmotionalLevel(Integer diaryId, String level);

        /**
         * 根据月份查询日记
         * 
         * @param year  年份
         * @param month 月份（1-12）
         * @return 日记列表
         */
        @Select("SELECT * FROM diary WHERE is_draft = 0 AND is_deleted = 0 " +
                        "AND strftime('%Y', diary_date) = CAST(#{year} AS TEXT) " +
                        "AND strftime('%m', diary_date) = printf('%02d', #{month}) " +
                        "ORDER BY diary_date DESC")
        List<DiaryPojo> selectByMonth(@Param("year") Integer year, @Param("month") Integer month);

        /**
         * 查询最早的日记日期
         * 
         * @return 最早的日记日期
         */
        @Select("SELECT MIN(diary_date) FROM diary WHERE is_draft = 0 AND is_deleted = 0")
        String selectEarliestDiaryDate();

        /**
         * 标记日记为已分析
         * 
         * @param diaryId 日记 ID
         * @param i       1 表示已分析，0 表示未分析
         */
        @Update("UPDATE diary SET is_analyzed = #{i} WHERE id = #{diaryId}")
        void updateIsAnalyzed(Integer diaryId, int i);

        /**
         * 根据日期查询日记
         * 
         * @param date 日记日期（格式：YYYY-MM-DD）
         * @return 日记列表
         */
        @Select("SELECT * FROM diary WHERE diary_date = #{date} AND is_draft = 0 AND is_deleted = 0")
        List<com.lyric.lyric.POJO.diary.DiaryPojo> selectByDate(String date);
}