package com.lyric.lyric.MapStruct.tag;

import com.lyric.lyric.DTO.tag.BaseTag;
import com.lyric.lyric.POJO.tag.BaseTagPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 标签对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface BaseTagMapStruct {
    
    BaseTagMapStruct INSTANCE = Mappers.getMapper(BaseTagMapStruct.class);
    
    /**
     * 将Tag DTO转换为TagPojo实体
     * @param baseTag DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tagType", ignore = true)
    BaseTagPojo toPojo(BaseTag baseTag);
    
    /**
     * 将TagPojo实体转换为Tag DTO
     * @param baseTagPojo POJO对象
     * @return DTO对象
     */
    BaseTag toDto(BaseTagPojo baseTagPojo);
    
    /**
     * 批量将TagPojo实体列表转换为Tag DTO列表
     * @param baseTagPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<BaseTag> toDtoList(List<BaseTagPojo> baseTagPojoList);
}