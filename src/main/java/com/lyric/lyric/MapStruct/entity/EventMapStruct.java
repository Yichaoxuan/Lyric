package com.lyric.lyric.MapStruct.entity;

import com.lyric.lyric.Dto.entity.Event;
import com.lyric.lyric.Pojo.entity.EventPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface EventMapStruct {
    
    EventMapStruct INSTANCE = Mappers.getMapper(EventMapStruct.class);
    
    /**
     * 将Event DTO转换为EventPojo实体
     * @param event DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appearanceCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventPojo toPojo(Event event);
    
    /**
     * 将EventPojo实体转换为Event DTO
     * @param eventPojo POJO对象
     * @return DTO对象
     */
    @Mapping(target = "eventDate", source = "eventDate")
    Event toDto(EventPojo eventPojo);
    
    /**
     * 批量将EventPojo实体列表转换为Event DTO列表
     * @param eventPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Event> toDtoList(List<EventPojo> eventPojoList);
}