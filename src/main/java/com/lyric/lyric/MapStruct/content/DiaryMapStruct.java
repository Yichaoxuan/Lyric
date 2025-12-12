package com.lyric.lyric.MapStruct.content;

import com.lyric.lyric.DTO.content.Diary;
import com.lyric.lyric.POJO.content.DiaryPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 日记对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface DiaryMapStruct {
    
    DiaryMapStruct INSTANCE = Mappers.getMapper(DiaryMapStruct.class);
    
    /**
     * 将Diary DTO转换为DiaryPojo实体
     * @param diary DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DiaryPojo toPojo(Diary diary);
    
    /**
     * 将DiaryPojo实体转换为Diary DTO
     * @param diaryPojo POJO对象
     * @return DTO对象
     */
    Diary toDto(DiaryPojo diaryPojo);
    
    /**
     * 批量将DiaryPojo实体列表转换为Diary DTO列表
     * @param diaryPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Diary> toDtoList(List<DiaryPojo> diaryPojoList);
}