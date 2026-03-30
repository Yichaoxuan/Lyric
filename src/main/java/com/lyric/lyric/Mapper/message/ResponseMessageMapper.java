package com.lyric.lyric.Mapper.message;

import com.lyric.lyric.POJO.message.ResponseMessagePojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 响应消息数据访问层接口
 */
@Mapper
public interface ResponseMessageMapper {

    /**
     * 根据类型查询响应消息
     * @param type 消息类型（SUCCESS、business-error、system-error）
     * @return 响应消息列表
     */
    @Select("SELECT * FROM response_message WHERE message_type = #{type}")
    List<ResponseMessagePojo> selectByType(String type);

    /**
     * 根据消息键查询响应消息
     * @param key 消息键
     * @return 响应消息实体
     */
    @Select("SELECT * FROM response_message WHERE message_key = #{key}")
    ResponseMessagePojo selectByKey(String key);

    /**
     * 查询所有响应消息
     * @return 响应消息列表
     */
    @Select("SELECT * FROM response_message")
    List<ResponseMessagePojo> selectAll();

    /**
     * 插入响应消息
     * @param pojo 响应消息实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO response_message(message_key, message_type, code, message) " +
            "VALUES(#{messageKey}, #{messageType}, #{code}, #{message})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResponseMessagePojo pojo);

    /**
     * 更新响应消息
     * @param pojo 响应消息实体
     * @return 影响的行数
     */
    @Update("UPDATE response_message SET code=#{code}, message=#{message} WHERE id=#{id}")
    int update(ResponseMessagePojo pojo);

    /**
     * 根据 ID 删除响应消息
     * @param id 响应消息 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM response_message WHERE id = #{id}")
    int deleteById(Integer id);

    /**
     * 根据消息键更新或插入（upsert）
     * @param pojo 响应消息实体
     * @return 影响的行数
     */
    @Insert("INSERT OR REPLACE INTO response_message(message_key, message_type, code, message) " +
            "VALUES(#{messageKey}, #{messageType}, #{code}, #{message})")
    int upsert(ResponseMessagePojo pojo);
}
