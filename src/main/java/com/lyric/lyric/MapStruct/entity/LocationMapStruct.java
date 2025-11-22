package com.lyric.lyric.MapStruct.entity;

import com.lyric.lyric.Dto.entity.Location;
import com.lyric.lyric.Pojo.entity.LocationPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 地点对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface LocationMapStruct {
    
    LocationMapStruct INSTANCE = Mappers.getMapper(LocationMapStruct.class);
    
    /**
     * 将Location DTO转换为LocationPojo实体
     * @param location DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appearanceCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LocationPojo toPojo(Location location);
    
    /**
     * 将LocationPojo实体转换为Location DTO
     * @param locationPojo POJO对象
     * @return DTO对象
     */
    Location toDto(LocationPojo locationPojo);
    
    /**
     * 批量将LocationPojo实体列表转换为Location DTO列表
     * @param locationPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Location> toDtoList(List<LocationPojo> locationPojoList);
}