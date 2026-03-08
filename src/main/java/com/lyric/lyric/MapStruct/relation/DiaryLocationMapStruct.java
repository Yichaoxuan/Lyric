package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.DTO.relation.DiaryLocation;
import com.lyric.lyric.POJO.relation.DiaryLocationPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 日记-地点关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface DiaryLocationMapStruct {
    
    DiaryLocationMapStruct INSTANCE = Mappers.getMapper(DiaryLocationMapStruct.class);
    
    /**
     * 将DiaryLocation DTO转换为DiaryLocationPojo实体
     * @param diaryLocation DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    DiaryLocationPojo toPojo(DiaryLocation diaryLocation);
    
    /**
     * 将DiaryLocationPojo实体转换为DiaryLocation DTO
     * @param diaryLocationPojo POJO对象
     * @return DTO对象
     */
    DiaryLocation toDto(DiaryLocationPojo diaryLocationPojo);
    
    /**
     * 批量将DiaryLocationPojo实体列表转换为DiaryLocation DTO列表
     * @param diaryLocationPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<DiaryLocation> toDtoList(List<DiaryLocationPojo> diaryLocationPojoList);
}