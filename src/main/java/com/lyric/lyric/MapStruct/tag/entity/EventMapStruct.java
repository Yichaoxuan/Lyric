package com.lyric.lyric.MapStruct.tag.entity;

import com.lyric.lyric.DTO.tag.entityTag.Event;
import com.lyric.lyric.POJO.tag.entityTag.EventPojo;
import com.lyric.lyric.Utils.stringProcessing.stringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 事件对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface EventMapStruct {
    
    EventMapStruct INSTANCE = Mappers.getMapper(EventMapStruct.class);

    /**
     * 将Map<String, String>转换为特定格式的String
     */
    @Named("mapToString")
    default String mapToString(Map<String, String> map) {
        return stringUtils.mapToString(map);
    }

     /**
     * 将特定格式的String转换为Map<String, String>
     */
     @Named("stringToMap")
     default Map<String, String> stringToMap(String str) {
         return stringUtils.stringToMap(str);
     }

    /**
     * 将Event DTO转换为EventPojo实体
     * @param event DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "persons", target = "persons", qualifiedByName = "mapToString")
    EventPojo toPojo(Event event);
    
    /**
     * 将EventPojo实体转换为Event DTO
     * @param eventPojo POJO对象
     * @return DTO对象
     */
    @Mapping(target = "persons", source = "persons", qualifiedByName = "stringToMap")
    Event toDto(EventPojo eventPojo);
    
    /**
     * 批量将EventPojo实体列表转换为Event DTO列表
     * @param eventPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Event> toDtoList(List<EventPojo> eventPojoList);
}