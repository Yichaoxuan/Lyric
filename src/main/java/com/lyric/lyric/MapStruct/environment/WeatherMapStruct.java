package com.lyric.lyric.MapStruct.environment;

import com.lyric.lyric.Dto.environment.Weather;
import com.lyric.lyric.Pojo.environment.WeatherPojo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 天气对象映射器
 *
 */
@Mapper(componentModel = "spring")
public interface WeatherMapStruct {
    
    WeatherMapStruct INSTANCE = Mappers.getMapper(WeatherMapStruct.class);
    
    /**
     * 将Weather DTO转换为WeatherPojo实体
     * @param weather DTO对象
     * @return POJO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    WeatherPojo toPojo(Weather weather);
    
    /**
     * 将WeatherPojo实体转换为Weather DTO
     * @param weatherPojo POJO对象
     * @return DTO对象
     */
    Weather toDto(WeatherPojo weatherPojo);
    
    /**
     * 批量将WeatherPojo实体列表转换为Weather DTO列表
     * @param weatherPojoList POJO对象列表
     * @return DTO对象列表
     */
    List<Weather> toDtoList(List<WeatherPojo> weatherPojoList);
}