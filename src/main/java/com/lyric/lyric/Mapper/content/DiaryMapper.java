package com.lyric.lyric.Mapper.content;

import com.lyric.lyric.Pojo.content.DiaryPojo;
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
    @Insert("INSERT INTO diary(title, content, content_type, content_format, is_deleted, is_draft, emotion_score, word_count, writing_start_time, writing_end_time, writing_duration, diary_date) " +
            "VALUES(#{title}, #{content}, #{contentType.value}, #{contentFormat.value}, #{isDeleted}, #{isDraft}, #{emotionScore}, #{wordCount}, #{writingStartTime}, #{writingEndTime}, #{writingDuration}, #{diaryDate})")
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
     * 更新日记
     * @param diary 日记实体
     * @return 影响的行数
     */
    @Update("UPDATE diary SET title=#{title}, content=#{content}, content_type=#{contentType.value}, content_format=#{contentFormat.value}, " +
            "is_deleted=#{isDeleted}, is_draft=#{isDraft}, emotion_score=#{emotionScore}, word_count=#{wordCount}, " +
            "writing_start_time=#{writingStartTime}, writing_end_time=#{writingEndTime}, writing_duration=#{writingDuration}, " +
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