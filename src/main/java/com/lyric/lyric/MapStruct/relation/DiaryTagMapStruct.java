package com.lyric.lyric.MapStruct.relation;

import com.lyric.lyric.DTO.relation.DiaryTag;
import com.lyric.lyric.POJO.relation.DiaryTagPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 日记-标签关联对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface DiaryTagMapStruct {
    
    DiaryTagMapStruct INSTANCE = Mappers.getMapper(DiaryTagMapStruct.class);
    
    /**
     * 将DiaryTag DTO转换为DiaryTagPojo实体
     * @param diaryTag DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    DiaryTagPojo toPojo(DiaryTag diaryTag);
    
    /**
     * 将DiaryTagPojo实体转换为DiaryTag DTO
     * @param diaryTagPojo POJO对象
     * @return DTO对象
     */
    DiaryTag toDto(DiaryTagPojo diaryTagPojo);
    
    /**
     * 批量将DiaryTagPojo实体列表转换为DiaryTag DTO列表
     * @param diaryTagPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<DiaryTag> toDtoList(List<DiaryTagPojo> diaryTagPojoList);
}