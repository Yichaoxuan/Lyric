package com.lyric.lyric.Service.weather;

import com.lyric.lyric.Mapper.relation.ActivityLocationMapper;
import com.lyric.lyric.Mapper.relation.DiaryActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.ActivityMapper;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.weather.WeatherPojo;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Mapper.environment.WeatherMapper;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private final ActivityMapper activityMapper;

    private final DiaryActivityMapper diaryActivityMapper;

    private final ActivityLocationMapper activityLocationMapper;

    public WeatherService(WeatherMapper weatherMapper, ActivityMapper activityMapper, DiaryActivityMapper diaryActivityMapper, ActivityLocationMapper activityLocationMapper) {
        this.weatherMapper = weatherMapper;
        this.activityMapper = activityMapper;
        this.diaryActivityMapper = diaryActivityMapper;
        this.activityLocationMapper = activityLocationMapper;
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
     * 根据地点ID和天气日期查询天气
     *
     * @param locationId 地点ID
     * @param weatherDate 天气日期
     * @return Result<Weather> 包含天气信息
     */
    public Result<WeatherPojo> getWeatherByDiaryId(Integer locationId, LocalDateTime weatherDate) {
        log.info("根据地点ID和天气日期天气：locationId={}", locationId);

        WeatherPojo weatherPojo = weatherMapper.selectByDiaryIdAndWeatherDate(locationId, weatherDate);
        if (weatherPojo == null) {
            log.warn("该地点{}的天气记录不存在，locationId={}", weatherDate, locationId);
        }

        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS,
                weatherPojo);
    }

    /**
     * 根据日记ID查询天气列表
     *
     * @param diaryId 日记ID
     * @return Result<List<Weather>> 包含天气信息的列表
     */
    public Result<List<WeatherPojo>> getWeathersByDiaryId(Integer diaryId) {
//        log.info("根据日记ID查询天气列表：diaryId={}", diaryId);
        
        // 验证参数
        if (diaryId == null) {
            log.warn("日记ID为空");
            return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS, new ArrayList<>());
        }
        
        List<Integer> activityIds = diaryActivityMapper.selectByDiaryId(diaryId);
        if (activityIds == null || activityIds.isEmpty()) {
            log.debug("日记未关联任何活动：diaryId={}", diaryId);
            return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS, new ArrayList<>());
        }
        
        ArrayList<WeatherPojo> matchWeathers = new ArrayList<>();
        for (Integer activityId : activityIds) {
            ActivityPojo activity = activityMapper.selectById(activityId);
            if (activity == null) {
                log.warn("活动不存在，跳过：activityId={}", activityId);
                continue;
            }
            
            List<Integer> locationIds = activityLocationMapper.selectLocationIdsByActivityId(activityId);
            if (locationIds == null || locationIds.isEmpty()) {
                log.debug("活动未关联地点，跳过：activityId={}", activityId);
                continue;
            }
            
            LocalDate activityDate = DateTimeUtils.toLocalDate(activity.getActivityDate());
            if (activityDate == null) {
                log.warn("活动日期为空，跳过：activityId={}", activityId);
                continue;
            }
            
            for (Integer locationId : locationIds) {
                List<WeatherPojo> weathers = weatherMapper.selectByDiaryId(locationId);
                if (weathers == null || weathers.isEmpty()) {
                    log.debug("地点无天气记录：locationId={}", locationId);
                    continue;
                }
                
                for (WeatherPojo weather : weathers) {
                    LocalDate weatherDate = DateTimeUtils.toLocalDate(weather.getWeatherDate());
                    if (activityDate.equals(weatherDate)) {
                        matchWeathers.add(weather);
                    }
                }
            }
        }
        
//        log.info("查询到 {} 条天气记录：diaryId={}", matchWeathers.size(), diaryId);
        return ResultBuilder.successWithData(SuccessMsgEnums.WEATHER_QUERY_BY_DIARY_SUCCESS, matchWeathers);
    }

    /**
     * 根据天气日期查询天气列表
     *
     * @param weatherDate 天气日期
     * @return Result<List<Weather>> 包含天气列表
     */
    public Result<List<WeatherPojo>> getWeathersByDate(LocalDate weatherDate) {
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
     * 根据地点ID和天气日期删除天气记录
     *
     * @param locationId 地点ID
     * @param weatherDate 天气日期
     * @return Result<Void> 删除结果
     */
    public Result<Void> deleteWeatherByDiaryId(Integer locationId, LocalDateTime weatherDate) {
        log.info("根据日记 ID 删除天气记录：diaryId={}", locationId);

        // 检查记录是否存在
        WeatherPojo existingWeather = weatherMapper.selectByDiaryIdAndWeatherDate(locationId, weatherDate);
        if (existingWeather == null) {
            throw new IllegalArgumentException("该日记的天气记录不存在");
        }

        weatherMapper.deleteByLocationIdAndWeatherDate(locationId, weatherDate);
        log.info("天气记录删除成功，日记 ID: {}", locationId);
        return ResultBuilder.success(SuccessMsgEnums.WEATHER_DELETE_SUCCESS);
    }
}
