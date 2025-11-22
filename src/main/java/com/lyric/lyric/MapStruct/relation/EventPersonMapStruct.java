package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.Dto.relation.EventPerson;
import com.lyric.lyric.Pojo.relation.EventPersonPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 事件-人物关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface EventPersonMapStruct {
    
    EventPersonMapStruct INSTANCE = Mappers.getMapper(EventPersonMapStruct.class);
    
    /**
     * 将EventPerson DTO转换为EventPersonPojo实体
     * @param eventPerson DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    EventPersonPojo toPojo(EventPerson eventPerson);
    
    /**
     * 将EventPersonPojo实体转换为EventPerson DTO
     * @param eventPersonPojo POJO对象
     * @return DTO对象
     */
    EventPerson toDto(EventPersonPojo eventPersonPojo);
    
    /**
     * 批量将EventPersonPojo实体列表转换为EventPerson DTO列表
     * @param eventPersonPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<EventPerson> toDtoList(List<EventPersonPojo> eventPersonPojoList);
}