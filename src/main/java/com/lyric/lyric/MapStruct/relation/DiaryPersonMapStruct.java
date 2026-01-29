package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.DTO.relation.DiaryPerson;
import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 日记-人物关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface DiaryPersonMapStruct {
    
    DiaryPersonMapStruct INSTANCE = Mappers.getMapper(DiaryPersonMapStruct.class);
    
    /**
     * 将DiaryPerson DTO转换为DiaryPersonPojo实体
     * @param diaryPerson DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    DiaryPersonPojo toPojo(DiaryPerson diaryPerson);
    
    /**
     * 将DiaryPersonPojo实体转换为DiaryPerson DTO
     * @param diaryPersonPojo POJO对象
     * @return DTO对象
     */
    DiaryPerson toDto(DiaryPersonPojo diaryPersonPojo);
    
    /**
     * 批量将DiaryPersonPojo实体列表转换为DiaryPerson DTO列表
     * @param diaryPersonPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<DiaryPerson> toDtoList(List<DiaryPersonPojo> diaryPersonPojoList);
}