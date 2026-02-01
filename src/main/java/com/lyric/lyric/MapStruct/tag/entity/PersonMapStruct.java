package com.lyric.lyric.MapStruct.tag.entity;

import com.lyric.lyric.DTO.tag.entityTag.Person;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Utils.stringProcessing.stringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
     * 将String转换为List<String>
     * @param str 字符串
     * @return 字符串列表
     */
    @Named("stringToList")
    default List<String> stringToList(String str) {
        return stringUtils.stringToList(str);
    }

    /**
     * 将List<String>转换为String
     * @param list 字符串列表
     * @return 字符串
     */
    @Named("listToString")
    default String listToString(List<String> list) {
        return stringUtils.listToString(list);
    }

    /**
     * 将Person DTO转换为PersonPojo实体
     * @param person DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appearanceCount", ignore = true)
    @Mapping(source = "alias", target = "alias", qualifiedByName = "listToString")
    PersonPojo toPojo(Person person);
    
    /**
     * 将PersonPojo实体转换为Person DTO
     * @param personPojo POJO对象
     * @return DTO对象
     */
    @Mapping(source = "alias", target = "alias", qualifiedByName = "stringToList")
    Person toDto(PersonPojo personPojo);
    
    /**
     * 批量将PersonPojo实体列表转换为Person DTO列表
     * @param personPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Person> toDtoList(List<PersonPojo> personPojoList);
}