package com.lyric.lyric.MapStruct.content;

import com.lyric.lyric.Dto.content.MediaFile;
import com.lyric.lyric.Pojo.content.MediaFilePojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 多媒体文件对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface MediaFileMapStruct {
    
    MediaFileMapStruct INSTANCE = Mappers.getMapper(MediaFileMapStruct.class);
    
    /**
     * 将MediaFile DTO转换为MediaFilePojo实体
     * @param mediaFile DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "fileType", ignore = true)
    MediaFilePojo toPojo(MediaFile mediaFile);
    
    /**
     * 将MediaFilePojo实体转换为MediaFile DTO
     * @param mediaFilePojo POJO对象
     * @return DTO对象
     */
    MediaFile toDto(MediaFilePojo mediaFilePojo);
    
    /**
     * 批量将MediaFilePojo实体列表转换为MediaFile DTO列表
     * @param mediaFilePojoList POJO对象列表
     * @return DTO对象列表
     */
    List<MediaFile> toDtoList(List<MediaFilePojo> mediaFilePojoList);
}