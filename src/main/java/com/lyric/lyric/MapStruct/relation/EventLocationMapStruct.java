package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.DTO.relation.EventLocation;
import com.lyric.lyric.POJO.relation.EventLocationPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 事件-地点关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface EventLocationMapStruct {
    
    EventLocationMapStruct INSTANCE = Mappers.getMapper(EventLocationMapStruct.class);
    
    /**
     * 将EventLocation DTO转换为EventLocationPojo实体
     * @param eventLocation DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventLocationPojo toPojo(EventLocation eventLocation);
    
    /**
     * 将EventLocationPojo实体转换为EventLocation DTO
     * @param eventLocationPojo POJO对象
     * @return DTO对象
     */
    EventLocation toDto(EventLocationPojo eventLocationPojo);
    
    /**
     * 批量将EventLocationPojo实体列表转换为EventLocation DTO列表
     * @param eventLocationPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<EventLocation> toDtoList(List<EventLocationPojo> eventLocationPojoList);
}