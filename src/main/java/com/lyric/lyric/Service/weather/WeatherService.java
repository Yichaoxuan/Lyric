package com.lyric.lyric.Service.weather;

import com.lyric.lyric.DTO.weather.Weather;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Mapper.environment.WeatherMapper;
import com.lyric.lyric.MapStruct.environment.WeatherMapStruct;
import com.lyric.lyric.POJO.weather.WeatherPojo;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 天气服务类
 * 提供天气信息的增删改查功能
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class WeatherService {

    private final WeatherMapper weatherMapper;
    private final WeatherMapStruct weatherMapStruct;

    public WeatherService(WeatherMapper weatherMapper, WeatherMapStruct weatherMapStruct) {
        this.weatherMapper = weatherMapper;
        this.weatherMapStruct = weatherMapStruct;
    }

    /**
     * 创建天气记录
     *
     * @param weather 天气 DTO 对象
     * @return Result<Void> 创建结果
     */
    public Result<Void> createWeather(Weather weather) {
        log.info("创建天气记录：city={}, weatherDate={}, weatherCondition={}",
                weather.getCity(), weather.getWeatherDate(), weather.getWeatherCondition());

        // 检查必要字段
        if (weather.getDiaryId() == null) {
            throw new IllegalArgumentException("日记 ID 不能为空");
        }
        if (weather.getCity() == null || weather.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }

        // 转换为 POJO 并插入数据库
        WeatherPojo weatherPojo = weatherMapStruct.toPojo(weather);
        weatherMapper.insert(weatherPojo);

        log.info("天气记录创建成功，ID: {}", weatherPojo.getId());
        return ResultBuilder.success(SuccessMsgEnums.WEATHER_CREATE_SUCCESS);
    }

    /**
     * 根据 ID 查询天气
     *
     * @param id 天气 ID
     * @return Result<Weather> 包含天气信息
     */
    public Result<Weather> getWeatherById(Integer id) {
        log.info("查询天气记录：id={}", id);

        WeatherPojo weatherPojo = weatherMapper.selectById(id);
        if (weatherPojo == null) {
            log.warn("天气记录不存在，id={}", id);
            throw new IllegalArgumentException("天气记录不存在");
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_SUCCESS, weatherMapStruct.toDto(weatherPojo));
    }

    /**
     * 根据日记 ID 查询天气
     *
     * @param diaryId 日记 ID
     * @return Result<Weather> 包含天气信息
     */
    public Result<Weather> getWeatherByDiaryId(Integer diaryId) {
        log.info("根据日记 ID 查询天气：diaryId={}", diaryId);

        WeatherPojo weatherPojo = weatherMapper.selectByDiaryId(diaryId);
        if (weatherPojo == null) {
            log.warn("日记的天气记录不存在，diaryId={}", diaryId);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS, weatherMapStruct.toDto(weatherPojo));
    }

    /**
     * 根据城市查询天气列表
     *
     * @param city 城市名称
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<Weather>> getWeathersByCity(String city) {
        log.info("根据城市查询天气：city={}", city);

        List<WeatherPojo> weatherPojos = weatherMapper.selectByCity(city);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_CITY_SUCCESS, weatherMapStruct.toDtoList(weatherPojos));
    }

    /**
     * 根据天气日期查询天气列表
     *
     * @param weatherDate 天气日期
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<Weather>> getWeathersByDate(java.time.LocalDate weatherDate) {
        log.info("根据日期查询天气：weatherDate={}", weatherDate);

        List<WeatherPojo> weatherPojos = weatherMapper.selectByWeatherDate(weatherDate);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DATE_SUCCESS, weatherMapStruct.toDtoList(weatherPojos));
    }

    /**
     * 根据天气状况查询天气列表
     *
     * @param weatherCondition 天气状况
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<Weather>> getWeathersByCondition(String weatherCondition) {
        log.info("根据天气状况查询天气：weatherCondition={}", weatherCondition);

        List<WeatherPojo> weatherPojos = weatherMapper.selectByWeatherCondition(weatherCondition);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_CONDITION_SUCCESS, weatherMapStruct.toDtoList(weatherPojos));
    }

    /**
     * 查询所有天气记录
     *
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<Weather>> getAllWeathers() {
        log.info("查询所有天气记录");

        List<WeatherPojo> weatherPojos = weatherMapper.selectAll();
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_SUCCESS, weatherMapStruct.toDtoList(weatherPojos));
    }

    /**
     * 更新天气记录
     *
     * @param weather 天气 DTO 对象（必须包含 id）
     * @return Result<Void> 更新结果
     */
    public Result<Void> updateWeather(Weather weather) {
        log.info("更新天气记录：id={}, city={}", weather.getId(), weather.getCity());

        // 检查 ID 是否存在
        if (weather.getId() == null) {
            throw new IllegalArgumentException("天气 ID 不能为空");
        }

        // 检查记录是否存在
        WeatherPojo existingWeather = weatherMapper.selectById(weather.getId());
        if (existingWeather == null) {
            throw new IllegalArgumentException("天气记录不存在");
        }

        // 转换为 POJO 并更新
        WeatherPojo weatherPojo = weatherMapStruct.toPojo(weather);
        weatherMapper.update(weatherPojo);

        log.info("天气记录更新成功，ID: {}", weather.getId());
        return ResultBuilder.success(SuccessMsgEnums.WEATHER_UPDATE_SUCCESS);
    }

    /**
     * 根据 ID 删除天气记录
     *
     * @param id 天气 ID
     * @return Result<Void> 删除结果
     */
    public Result<Void> deleteWeatherById(Integer id) {
        log.info("删除天气记录：id={}", id);

        // 检查记录是否存在
        WeatherPojo existingWeather = weatherMapper.selectById(id);
        if (existingWeather == null) {
            throw new IllegalArgumentException("天气记录不存在");
        }

        weatherMapper.deleteById(id);
        log.info("天气记录删除成功，ID: {}", id);
        return ResultBuilder.success(SuccessMsgEnums.WEATHER_DELETE_SUCCESS);
    }

    /**
     * 根据日记 ID 删除天气记录
     *
     * @param diaryId 日记 ID
     * @return Result<Void> 删除结果
     */
    public Result<Void> deleteWeatherByDiaryId(Integer diaryId) {
        log.info("根据日记 ID 删除天气记录：diaryId={}", diaryId);

        // 检查记录是否存在
        WeatherPojo existingWeather = weatherMapper.selectByDiaryId(diaryId);
        if (existingWeather == null) {
            throw new IllegalArgumentException("该日记的天气记录不存在");
        }

        weatherMapper.deleteByDiaryId(diaryId);
        log.info("天气记录删除成功，日记 ID: {}", diaryId);
        return ResultBuilder.success(SuccessMsgEnums.WEATHER_DELETE_SUCCESS);
    }
}
