package com.lyric.lyric.MapStruct.tag.entity;

import com.lyric.lyric.DTO.tag.entityTag.event.SubEvent;
import com.lyric.lyric.DTO.tag.entityTag.event.TogEvent;
import com.lyric.lyric.POJO.tag.entityTag.event.SubEventPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.TogEventPojo;
import com.lyric.lyric.Utils.stringProcessing.stringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 事件对象映射器
 * 支持父事件和子事件的DTO与POJO之间的映射
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

    // ==================== 父事件映射 ====================

    /**
     * 将TogEvent DTO转换为TogEventPojo实体
     * @param togEvent DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "diaryId", ignore = true)
    TogEventPojo toTogEventPojo(TogEvent togEvent);

    /**
     * 将TogEventPojo实体转换为TogEvent DTO
     * @param togEventPojo POJO对象
     * @return DTO对象
     */
    TogEvent toTogEventDto(TogEventPojo togEventPojo);

    /**
     * 批量将TogEventPojo实体列表转换为TogEvent DTO列表
     * @param togEventPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<TogEvent> toTogEventDtoList(List<TogEventPojo> togEventPojoList);

    // ==================== 子事件映射 ====================
    
    /**
     * 将SubEvent DTO转换为SubEventPojo实体
     * @param subEvent DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "togEventId", ignore = true)
    SubEventPojo toSubEventPojo(SubEvent subEvent);
    
    /**
     * 将SubEventPojo实体转换为SubEvent DTO
     * @param subEventPojo POJO对象
     * @return DTO对象
     */
    SubEvent toSubEventDto(SubEventPojo subEventPojo);
    
    /**
     * 批量将SubEventPojo实体列表转换为SubEvent DTO列表
     * @param subEventPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<SubEvent> toSubEventDtoList(List<SubEventPojo> subEventPojoList);
}