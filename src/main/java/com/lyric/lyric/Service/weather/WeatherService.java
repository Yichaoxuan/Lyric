package com.lyric.lyric.Service.weather;

import com.lyric.lyric.POJO.weather.WeatherPojo;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Mapper.environment.WeatherMapper;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 天气服务类
 * 提供天气信息的删查功能
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class WeatherService {

    private final WeatherMapper weatherMapper;

    public WeatherService(WeatherMapper weatherMapper) {
        this.weatherMapper = weatherMapper;
    }

    /**
     * 根据 ID 查询天气
     *
     * @param id 天气 ID
     * @return Result<Weather> 包含天气信息
     */
    public Result<WeatherPojo> getWeatherById(Integer id) {
        log.info("查询天气记录：id={}", id);

        WeatherPojo weatherPojo = weatherMapper.selectById(id);
        if (weatherPojo == null) {
            log.warn("天气记录不存在，id={}", id);
            throw new IllegalArgumentException("天气记录不存在");
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_SUCCESS,
                weatherPojo);
    }

    /**
     * 根据日记 ID 查询天气
     *
     * @param diaryId 日记 ID
     * @return Result<Weather> 包含天气信息
     */
    public Result<WeatherPojo> getWeatherByDiaryId(Integer diaryId) {
        log.info("根据日记 ID 查询天气：diaryId={}", diaryId);

        WeatherPojo weatherPojo = weatherMapper.selectByDiaryId(diaryId);
        if (weatherPojo == null) {
            log.warn("日记的天气记录不存在，diaryId={}", diaryId);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS,
                weatherPojo);
    }

    /**
     * 根据城市查询天气列表
     *
     * @param city 城市名称
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<WeatherPojo>> getWeathersByCity(String city) {
        log.info("根据城市查询天气：city={}", city);

        List<WeatherPojo> weathers = weatherMapper.selectByCity(city);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_CITY_SUCCESS,
                weathers);
    }

    /**
     * 根据天气日期查询天气列表
     *
     * @param weatherDate 天气日期
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<WeatherPojo>> getWeathersByDate(java.time.LocalDate weatherDate) {
        log.info("根据日期查询天气：weatherDate={}", weatherDate);

        List<WeatherPojo> weathers = weatherMapper.selectByWeatherDate(weatherDate);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DATE_SUCCESS,
                weathers);
    }

    /**
     * 根据天气状况查询天气列表
     *
     * @param weatherCondition 天气状况
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<WeatherPojo>> getWeathersByCondition(String weatherCondition) {
        log.info("根据天气状况查询天气：weatherCondition={}", weatherCondition);

        List<WeatherPojo> weathers = weatherMapper.selectByWeatherCondition(weatherCondition);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_CONDITION_SUCCESS,
                weathers);
    }

    /**
     * 查询所有天气记录
     *
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<WeatherPojo>> getAllWeathers() {
        log.info("查询所有天气记录");

        List<WeatherPojo> weathers = weatherMapper.selectAll();
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_SUCCESS,
                weathers);
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
