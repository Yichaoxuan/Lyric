package com.lyric.lyric.MapStruct.tag;

import com.lyric.lyric.Dto.tag.Tag;
import com.lyric.lyric.Pojo.tag.TagPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 标签对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface TagMapStruct {
    
    TagMapStruct INSTANCE = Mappers.getMapper(TagMapStruct.class);
    
    /**
     * 将Tag DTO转换为TagPojo实体
     * @param tag DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tagType", ignore = true)
    TagPojo toPojo(Tag tag);
    
    /**
     * 将TagPojo实体转换为Tag DTO
     * @param tagPojo POJO对象
     * @return DTO对象
     */
    Tag toDto(TagPojo tagPojo);
    
    /**
     * 批量将TagPojo实体列表转换为Tag DTO列表
     * @param tagPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Tag> toDtoList(List<TagPojo> tagPojoList);
}