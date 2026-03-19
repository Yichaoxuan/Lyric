package com.lyric.lyric.Mapper.tag;

import com.lyric.lyric.POJO.tag.BaseTagPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 标签数据访问层接口
 *
 * @author Yichaoxuan
 */
@Mapper
public interface TagMapper {
    
    /**
     * 插入一条标签记录
     * @param tag 标签实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO tag(name, tag_type, level, color, icon, usage_count) " +
            "VALUES(#{name}, #{tagType}, #{level}, #{color}, #{icon}, #{usageCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BaseTagPojo tag);
    
    /**
     * 根据ID查询标签
     * @param id 标签ID
     * @return 标签实体
     */
    @Select("SELECT * FROM tag WHERE id = #{id}")
    BaseTagPojo selectById(Integer id);
    
    /**
     * 查询所有标签
     * @return 标签列表
     */
    @Select("SELECT * FROM tag")
    List<BaseTagPojo> selectAll();

    /**
     * 根据标签类型查询标签
     * @param tagType 标签类型
     * @return 标签列表
     */
    @Select("SELECT * FROM tag WHERE tag_type = #{tagType}")
    List<BaseTagPojo> selectByTagType(BaseTagPojo.TagType tagType);

    /**
     * 更新标签
     * @param tag 标签实体
     * @return 影响的行数
     */
    @Update("UPDATE tag SET name=#{name}, tag_type=#{tagType}, level=#{level}, color=#{color}, icon=#{icon}, " +
            "usage_count=#{usageCount} WHERE id=#{id}")
    int update(BaseTagPojo tag);
    
    /**
     * 根据ID删除标签
     * @param id 标签ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM tag WHERE id = #{id}")
    int deleteById(Integer id);
}