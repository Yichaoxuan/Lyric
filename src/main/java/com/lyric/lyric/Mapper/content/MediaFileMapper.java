package com.lyric.lyric.Mapper.content;

import com.lyric.lyric.Pojo.content.MediaFilePojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 多媒体文件数据访问层接口
 */
@Mapper
public interface MediaFileMapper {
    
    /**
     * 插入一条多媒体文件记录
     * @param mediaFile 多媒体文件实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO media_file(diary_id, file_name, file_path, file_type, file_size, upload_time) " +
            "VALUES(#{diaryId}, #{fileName}, #{filePath}, #{fileType}, #{fileSize}, #{uploadTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MediaFilePojo mediaFile);
    
    /**
     * 根据ID查询多媒体文件
     * @param id 多媒体文件ID
     * @return 多媒体文件实体
     */
    @Select("SELECT * FROM media_file WHERE id = #{id}")
    MediaFilePojo selectById(Long id);
    
    /**
     * 根据日记ID查询多媒体文件列表
     * @param diaryId 日记ID
     * @return 多媒体文件列表
     */
    @Select("SELECT * FROM media_file WHERE diary_id = #{diaryId}")
    List<MediaFilePojo> selectByDiaryId(Long diaryId);
    
    /**
     * 查询所有多媒体文件
     * @return 多媒体文件列表
     */
    @Select("SELECT * FROM media_file")
    List<MediaFilePojo> selectAll();
    
    /**
     * 更新多媒体文件
     * @param mediaFile 多媒体文件实体
     * @return 影响的行数
     */
    @Update("UPDATE media_file SET diary_id=#{diaryId}, file_name=#{fileName}, file_path=#{filePath}, " +
            "file_type=#{fileType}, file_size=#{fileSize}, upload_time=#{uploadTime} WHERE id=#{id}")
    int update(MediaFilePojo mediaFile);
    
    /**
     * 根据ID删除多媒体文件
     * @param id 多媒体文件ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM media_file WHERE id = #{id}")
    int deleteById(Long id);
}