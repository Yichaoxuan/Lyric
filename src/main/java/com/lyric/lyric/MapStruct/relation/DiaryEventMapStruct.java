package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.DTO.relation.DiaryEvent;
import com.lyric.lyric.POJO.relation.DiaryEventPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 日记-事件关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface DiaryEventMapStruct {
    
    DiaryEventMapStruct INSTANCE = Mappers.getMapper(DiaryEventMapStruct.class);
    
    /**
     * 将DiaryEvent DTO转换为DiaryEventPojo实体
     * @param diaryEvent DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    DiaryEventPojo toPojo(DiaryEvent diaryEvent);
    
    /**
     * 将DiaryEventPojo实体转换为DiaryEvent DTO
     * @param diaryEventPojo POJO对象
     * @return DTO对象
     */
    DiaryEvent toDto(DiaryEventPojo diaryEventPojo);
    
    /**
     * 批量将DiaryEventPojo实体列表转换为DiaryEvent DTO列表
     * @param diaryEventPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<DiaryEvent> toDtoList(List<DiaryEventPojo> diaryEventPojoList);
}