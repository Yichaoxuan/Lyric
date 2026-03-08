package com.lyric.lyric.Mapper.content;

import com.lyric.lyric.POJO.content.MediaFilePojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文件数据访问层接口（对应数据库表: file）
 */
@Mapper
public interface MediaFileMapper {

        /**
         * 插入一条文件记录
         * 
         * @param mediaFile 文件实体（对应数据库表: file）
         * @return 影响的行数
         */
        @Insert("INSERT INTO file(diary_id, file_name, file_path, file_type, file_size, upload_time) " +
                        "VALUES(#{diaryId}, #{fileName}, #{filePath}, #{fileType}, #{fileSize}, #{uploadTime})")
        @Options(useGeneratedKeys = true, keyProperty = "id")
        int insert(MediaFilePojo mediaFile);

        /**
         * 根据ID查询文件
         * 
         * @param id 文件ID
         * @return 文件实体（对应数据库表: file）
         */
        @Select("SELECT * FROM file WHERE id = #{id}")
        MediaFilePojo selectById(Integer id);

        /**
         * 根据日记ID查询文件列表
         * 
         * @param diaryId 日记ID
         * @return 文件列表
         */
        @Select("SELECT * FROM file WHERE diary_id = #{diaryId}")
        List<MediaFilePojo> selectByDiaryId(Integer diaryId);

        /**
         * 查询所有文件
         * 
         * @return 文件列表
         */
        @Select("SELECT * FROM file")
        List<MediaFilePojo> selectAll();

        /**
         * 更新文件
         * 
         * @param mediaFile 文件实体（对应数据库表: file）
         * @return 影响的行数
         */
        @Update("UPDATE file SET diary_id=#{diaryId}, file_name=#{fileName}, file_path=#{filePath}, " +
                        "file_type=#{fileType}, file_size=#{fileSize}, upload_time=#{uploadTime} WHERE id=#{id}")
        int update(MediaFilePojo mediaFile);

        /**
         * 根据ID删除文件
         * 
         * @param id 文件ID
         * @return 影响的行数
         */
        @Delete("DELETE FROM file WHERE id = #{id}")
        int deleteById(Integer id);
}