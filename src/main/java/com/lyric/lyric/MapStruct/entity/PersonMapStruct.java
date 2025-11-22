package com.lyric.lyric.MapStruct.entity;

import com.lyric.lyric.Dto.entity.Person;
import com.lyric.lyric.Pojo.entity.PersonPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 人物对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface PersonMapStruct {
    
    PersonMapStruct INSTANCE = Mappers.getMapper(PersonMapStruct.class);
    
    /**
     * 将Person DTO转换为PersonPojo实体
     * @param person DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appearanceCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PersonPojo toPojo(Person person);
    
    /**
     * 将PersonPojo实体转换为Person DTO
     * @param personPojo POJO对象
     * @return DTO对象
     */
    Person toDto(PersonPojo personPojo);
    
    /**
     * 批量将PersonPojo实体列表转换为Person DTO列表
     * @param personPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Person> toDtoList(List<PersonPojo> personPojoList);
}