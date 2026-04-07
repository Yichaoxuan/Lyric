package com.lyric.lyric.Controller.weather;

import com.lyric.lyric.POJO.weather.WeatherPojo;
import com.lyric.lyric.Service.weather.WeatherService;
import com.lyric.lyric.Utils.resultUtils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 天气控制器
 * 提供天气信息的创建、修改、查询、删除 REST API 接口
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * 根据 ID 查询天气
     *
     * @param id 天气 ID
     * @return 查询结果
     */
    @GetMapping("/queryById")
    public Result<WeatherPojo> queryWeatherById(@RequestParam Integer id) {
        return weatherService.getWeatherById(id);
    }

    /**
     * 根据日记 ID 查询天气
     *
     * @param diaryId 日记 ID
     * @return 查询结果
     */
    @GetMapping("/queryByDiaryId")
    public Result<WeatherPojo> queryWeatherByDiaryId(@RequestParam Integer diaryId) {
        return weatherService.getWeatherByDiaryId(diaryId);
    }

    /**
     * 根据城市查询天气列表
     *
     * @param city 城市名称
     * @return 查询结果
     */
    @GetMapping("/queryByCity")
    public Result<List<WeatherPojo>> queryWeathersByCity(@RequestParam String city) {
        return weatherService.getWeathersByCity(city);
    }

    /**
     * 根据日期查询天气列表
     *
     * @param weatherDate 天气日期（格式：yyyy-MM-dd）
     * @return 查询结果
     */
    @GetMapping("/queryByDate")
    public Result<List<WeatherPojo>> queryWeathersByDate(@RequestParam String weatherDate) {
        java.time.LocalDate date = java.time.LocalDate.parse(weatherDate);
        return weatherService.getWeathersByDate(date);
    }

    /**
     * 根据天气状况查询天气列表
     *
     * @param weatherCondition 天气状况
     * @return 查询结果
     */
    @GetMapping("/queryByCondition")
    public Result<List<WeatherPojo>> queryWeathersByCondition(@RequestParam String weatherCondition) {
        return weatherService.getWeathersByCondition(weatherCondition);
    }

    /**
     * 查询所有天气记录
     *
     * @return 查询结果
     */
    @GetMapping("/queryAll")
    public Result<List<WeatherPojo>> queryAllWeathers() {
        return weatherService.getAllWeathers();
    }

    /**
     * 根据 ID 删除天气记录
     *
     * @param id 天气 ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteById")
    public Result<Void> deleteWeatherById(@RequestParam Integer id) {
        return weatherService.deleteWeatherById(id);
    }

    /**
     * 根据日记 ID 删除天气记录
     *
     * @param diaryId 日记 ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteByDiaryId")
    public Result<Void> deleteWeatherByDiaryId(@RequestParam Integer diaryId) {
        return weatherService.deleteWeatherByDiaryId(diaryId);
    }
}
