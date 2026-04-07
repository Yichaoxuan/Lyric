package com.lyric.lyric.Service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyric.lyric.Mapper.environment.WeatherMapper;
import com.lyric.lyric.Mapper.relation.ActivityLocationMapper;
import com.lyric.lyric.Mapper.tag.entity.ActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.POJO.tag.entityTag.event.ActivityPojo;
import com.lyric.lyric.POJO.weather.WeatherPojo;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 天气获取服务类
 * 提供天气信息查询和处理功能，包括获取LocationId、查询天气信息、保存天气数据到数据库等
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
@Slf4j
@Service
public class GetWeatherService {

    // ==================== 常量定义 ====================

    /** 和风天气地理位置查询 API 路径 */
    private static final String LOCATION_LOOKUP_PATH = "/geo/v2/city/lookup";

    /** 和风天气实时天气查询 API 路径 */
    private static final String WEATHER_NOW_PATH = "/v7/historical/weather";

    /** API 响应成功状态码 */
    private static final String SUCCESS_STATUS = "200";

    /** API 响应中的 location 数组字段名 */
    private static final String LOCATION_ARRAY_FIELD = "location";

    /** API 响应中的每日天气数据字段名 */
    private static final String WEATHER_DAILY_FIELD = "weatherDaily";

    /** API 响应中的每小时天气数据字段名 */
    private static final String WEATHER_HOURLY_FIELD = "weatherHourly";

    /** API 响应中的状态码字段名 */
    private static final String CODE_FIELD = "code";

    /** API 响应中的消息字段名 */
    private static final String MESSAGE_FIELD = "message";

    /** API 要求的时间格式 */
    private static final String TIME_FORMAT = "yyyyMMdd";

    // ==================== 成员变量 ====================

    private final WeatherMapper weatherMapper;
    private final UserSettingsService userSettingsService;
    private final ActivityMapper activityMapper;
    private final LocationMapper locationMapper;
    private final ActivityLocationMapper activityLocationMapper;

    /** JSON 对象映射器，用于解析 API 返回的 JSON 数据 */
    private final ObjectMapper objectMapper;

    public GetWeatherService(WeatherMapper weatherMapper, UserSettingsService userSettingsService,
            ActivityMapper activityMapper, LocationMapper locationMapper,
            ActivityLocationMapper activityLocationMapper, ObjectMapper objectMapper) {
        this.weatherMapper = weatherMapper;
        this.userSettingsService = userSettingsService;
        this.objectMapper = objectMapper;
        this.activityMapper = activityMapper;
        this.locationMapper = locationMapper;
        this.activityLocationMapper = activityLocationMapper;
    }

    /**
     * 处理日记天气：获取天气信息并保存到数据库
     * 主入口方法，直接获取前一天的活动，根据活动关联的地点获取天气
     */
    public void processWeatherForDiary() {
        String yesterday = DateTimeUtils.format(LocalDate.now().minusDays(1), "yyyy-MM-dd");
        log.info("开始处理前一天的活动天气，日期：{}", yesterday);
        List<ActivityPojo> activities = activityMapper.selectByDate(yesterday);
        if (activities == null || activities.isEmpty()) {
            log.info("前一天没有活动，跳过天气处理");
            return;
        }
        log.info("找到 {} 个活动，开始收集去重地点", activities.size());
        Map<String, Set<Integer>> dateLocationMap = collectUniqueLocationsByDate(activities);
        for (Map.Entry<String, Set<Integer>> entry : dateLocationMap.entrySet()) {
            String dateStr = entry.getKey();
            LocalDate date = LocalDate.parse(dateStr);
            for (Integer locationId : entry.getValue()) {
                fetchAndSaveDailyWeather(locationId, date);
            }
        }
    }

    /**
     * 收集活动中按日期分组的去重地点ID
     * 遍历活动列表，根据活动时间确定日期，并收集每个日期关联的所有唯一地点ID
     *
     * @param activities 活动列表
     * @return 按日期分组的地点ID集合，Key为日期字符串（yyyy-MM-dd），Value为该日期的唯一地点ID集合
     */
    private Map<String, Set<Integer>> collectUniqueLocationsByDate(List<ActivityPojo> activities) {
        Map<String, Set<Integer>> map = new LinkedHashMap<>();
        for (ActivityPojo activity : activities) {
            LocalDateTime activityDateTime = determineActivityDateTime(activity);
            if (activityDateTime == null)
                continue;
            String dateStr = DateTimeUtils.format(activityDateTime.toLocalDate(), "yyyy-MM-dd");
            List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> locationRelations = activityLocationMapper
                    .selectByActivityId(activity.getId());
            if (locationRelations == null || locationRelations.isEmpty())
                continue;
            map.computeIfAbsent(dateStr, k -> new HashSet<>())
                    .addAll(locationRelations.stream()
                            .map(com.lyric.lyric.POJO.relation.ActivityLocationPojo::getLocationId)
                            .collect(Collectors.toSet()));
        }
        log.info("收集到 {} 个日期-地点组合", map.values().stream().mapToInt(Set::size).sum());
        return map;
    }

    /**
     * 获取并保存指定地点和日期的天气数据
     * 验证地点信息完整性，获取和风天气LocationId，查询天气数据并匹配到相关活动
     *
     * @param locationId 地点ID（数据库主键）
     * @param date       需要查询天气的日期
     */
    private void fetchAndSaveDailyWeather(Integer locationId, LocalDate date) {
        com.lyric.lyric.POJO.tag.entityTag.LocationPojo location = locationMapper.selectById(locationId);
        if (location == null) {
            log.warn("地点不存在，跳过：locationId={}", locationId);
            return;
        }
        if (location.getLongitude() == null || location.getLatitude() == null) {
            log.warn("地点缺少经纬度信息，跳过：locationId={}, name={}", locationId, location.getName());
            return;
        }
        String city = location.getCity();
        if (!StringUtils.hasText(city)) {
            log.warn("地点缺少城市信息，跳过：locationId={}, name={}", locationId, location.getName());
            return;
        }
        List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> relations = activityLocationMapper
                .selectByLocationId(locationId);
        boolean hasUnprocessedActivity = false;
        if (relations != null) {
            for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : relations) {
                ActivityPojo activity = activityMapper.selectById(relation.getActivityId());
                if (activity == null)
                    continue;
                LocalDateTime activityDateTime = determineActivityDateTime(activity);
                if (activityDateTime == null)
                    continue;
                if (!activityDateTime.toLocalDate().equals(date))
                    continue;
                WeatherPojo existing = weatherMapper.selectByDiaryIdAndWeatherDate(locationId, activityDateTime);
                if (existing == null) {
                    hasUnprocessedActivity = true;
                    break;
                }
            }
        }
        if (!hasUnprocessedActivity) {
            log.info("地点 {} 在 {} 所有活动已有关联天气记录，跳过API调用", locationId, date);
            return;
        }
        String heWeatherLocationId = getLocationId(
                formatToTwoDecimalPlaces(location.getLongitude()),
                formatToTwoDecimalPlaces(location.getLatitude()));
        if (heWeatherLocationId == null) {
            log.error("获取和风天气 LocationId 失败，locationId={}", locationId);
            return;
        }
        DailyWeatherData dailyWeather = fetchFullDayWeather(heWeatherLocationId, date);
        if (dailyWeather == null) {
            log.error("获取全天天气数据失败，locationId={}, date={}", locationId, date);
            return;
        }
        matchWeatherForActivities(locationId, date, dailyWeather);
    }

    /**
     * 获取指定地点和日期的完整天气数据
     * 调用和风天气历史天气 API，获取包含每日汇总和每小时详细数据的完整天气信息
     *
     * @param locationId 和风天气系统的地点唯一标识
     * @param date       需要查询的日期
     * @return 完整天气数据对象（包含最高/最低温度、天气状况、图标及小时数据），如果请求失败则返回 null
     */
    private DailyWeatherData fetchFullDayWeather(String locationId, LocalDate date) {
        // 从用户配置中获取 API 密钥和服务地址
        String apiKey = userSettingsService.getLatestApiConfig().getWeatherApiKey();
        String apiHost = userSettingsService.getLatestApiConfig().getWeatherApiHost();
        // 格式化日期为 API 要求的格式（yyyyMMdd）
        String formattedDate = DateTimeUtils.format(date, TIME_FORMAT);
        // 构造完整的 API 请求 URL
        String url = apiHost + WEATHER_NOW_PATH + "?location=" + locationId + "&date=" + formattedDate + "&key="
                + apiKey;
        log.info(url);
        HttpGet httpGet = new HttpGet(url);
        // 执行 HTTP 请求并处理响应
        try (CloseableHttpClient httpClient = HttpClients.createSystem();
                ClassicHttpResponse response = httpClient.executeOpen(null, httpGet, null)) {
            if (isHttpFailure(response)) {
                log.error("天气 API HTTP 错误，状态码：{}, LocationId: {}", response.getCode(), locationId);
                return null;
            }
            return parseFullDayWeatherResponse(response, locationId, date);
        } catch (Exception e) {
            log.error("调用天气 API 异常，LocationId: {}", locationId, e);
            return null;
        }
    }

    /**
     * 解析完整天气数据的API响应
     * 从HTTP响应中提取每日汇总天气数据和每小时详细天气数据
     *
     * @param response   HTTP响应对象
     * @param locationId 和风天气系统的地点唯一标识，用于日志记录
     * @param date       查询的日期，用于日志记录
     * @return 完整天气数据对象，如果解析失败则返回 null
     * @throws Exception 当读取响应实体或解析JSON失败时抛出
     */
    private DailyWeatherData parseFullDayWeatherResponse(ClassicHttpResponse response, String locationId,
            LocalDate date) throws Exception {
        String result = extractResponseEntity(response);
        if (result == null) {
            log.error("天气 API 响应为空，LocationId: {}, 日期为{}", locationId, date);
            return null;
        }
        JsonNode root = objectMapper.readTree(result);
        if (isApiFailure(root)) {
            logApiError("天气 API 业务错误", root, String.valueOf(locationId));
            return null;
        }
        return parseDailyWeatherData(root, locationId);
    }

    /**
     * 将天气数据匹配并保存到相关活动
     * 查找与指定地点和日期相关的活动，根据活动时间匹配对应的天气数据，并保存到数据库
     *
     * @param locationId   地点ID（数据库主键）
     * @param date         活动日期
     * @param dailyWeather 完整天气数据对象
     */
    private void matchWeatherForActivities(Integer locationId, LocalDate date, DailyWeatherData dailyWeather) {
        List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> relations = activityLocationMapper
                .selectByLocationId(locationId);
        if (relations == null || relations.isEmpty()) {
            log.debug("地点 {} 没有关联任何活动", locationId);
            return;
        }
        for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : relations) {
            ActivityPojo activity = activityMapper.selectById(relation.getActivityId());
            if (activity == null)
                continue;
            LocalDateTime activityDateTime = determineActivityDateTime(activity);
            if (activityDateTime == null)
                continue;
            if (!activityDateTime.toLocalDate().equals(date))
                continue;
            WeatherInformation matchedWeather = matchWeatherByTime(activityDateTime, dailyWeather);
            WeatherPojo existing = weatherMapper.selectByDiaryIdAndWeatherDate(locationId, activityDateTime);
            if (existing != null) {
                log.debug("天气记录已存在，跳过：locationId={}, date={}", locationId, activityDateTime);
                continue;
            }
            com.lyric.lyric.POJO.tag.entityTag.LocationPojo location = locationMapper.selectById(locationId);
            String city = location != null ? location.getCity() : "";
            WeatherPojo weatherPojo = new WeatherPojo(locationId, activityDateTime, matchedWeather);
            weatherMapper.insert(weatherPojo);
            log.info("天气保存成功：locationId={}, city={}, activity={}, weather={}",
                    locationId, city, activity.getName(), matchedWeather.getWeatherCondition());
        }
    }

    /**
     * 根据活动时间匹配最接近的天气数据
     * 优先使用小时级天气数据进行精确匹配，若无小时数据则使用每日汇总数据
     *
     * @param activityTime 活动的时间
     * @param dailyWeather 完整天气数据对象
     * @return 匹配的天气信息对象（保证非null，至少返回每日汇总数据）
     */
    private WeatherInformation matchWeatherByTime(LocalDateTime activityTime, DailyWeatherData dailyWeather) {
        // 尝试从小时数据中匹配最接近的温度
        Double matchedTemp = null;
        String matchedText = null;
        String matchedIcon = null;

        if (dailyWeather.hourlyRecords != null && !dailyWeather.hourlyRecords.isEmpty()) {
            HourlyWeatherRecord closest = findClosestHourlyRecord(activityTime, dailyWeather.hourlyRecords);
            if (closest != null) {
                matchedTemp = closest.temp;
                matchedText = closest.text;
                matchedIcon = closest.icon;
                log.info("匹配到小时天气：activityTime={}, matchedTime={}, temp={}, weather={}",
                        activityTime, closest.time, closest.temp, closest.text);
            }
        }

        // 如果没有匹配到小时数据，使用每日平均温度
        if (matchedTemp == null) {
            matchedTemp = calculateAverageTemperature(dailyWeather.tempMax, dailyWeather.tempMin);
            matchedText = dailyWeather.textDay;
            matchedIcon = dailyWeather.iconDay;
            log.info("无小时数据，使用每日汇总天气：activityTime={}, avgTemp={}", activityTime, matchedTemp);
        }

        return new WeatherInformation(matchedTemp, dailyWeather.tempMax, dailyWeather.tempMin, matchedText,
                matchedIcon);
    }

    /**
     * 查找最接近活动时间的小时天气记录
     *
     * @param activityTime  活动时间
     * @param hourlyRecords 小时天气记录列表
     * @return 最接近的小时天气记录，如果列表为空则返回 null
     */
    private HourlyWeatherRecord findClosestHourlyRecord(LocalDateTime activityTime,
            List<HourlyWeatherRecord> hourlyRecords) {
        HourlyWeatherRecord closest = null;
        long minDiff = Long.MAX_VALUE;

        for (HourlyWeatherRecord record : hourlyRecords) {
            long diff = Math.abs(java.time.Duration.between(record.time, activityTime).toMinutes());
            if (diff < minDiff) {
                minDiff = diff;
                closest = record;
            }
        }

        return closest;
    }

    /**
     * 计算平均温度
     *
     * @param tempMax 最高温度
     * @param tempMin 最低温度
     * @return 平均温度
     */
    private double calculateAverageTemperature(double tempMax, double tempMin) {
        return (tempMax + tempMin) / 2.0;
    }

    /**
     * 确定活动的日期时间
     * 根据以下规则推断：
     * 1. 如果 activityDate 时间部分不是 00:00:00，直接使用该时间
     * 2. 如果 activityDate 时间部分是 00:00:00 且 timePeriod 为 EARLY_MORNING，则使用 00:00:00
     * 3. 如果 activityDate 时间部分是 00:00:00 且 timePeriod 不为 EARLY_MORNING，则根据
     * timePeriod 推算
     *
     * @param activity 活动对象
     * @return 活动的日期时间，若无法确定则返回 null
     */
    private LocalDateTime determineActivityDateTime(ActivityPojo activity) {
        LocalDateTime activityDate = activity.getActivityDate();
        ActivityPojo.TimePeriod timePeriod = activity.getTimePeriod();

        // 情况 1: 如果没有日期信息，无法确定时间
        if (activityDate == null) {
            log.warn("活动缺少日期信息：activityId={}", activity.getId());
            return null;
        }

        // 检查时间部分是否为 00:00:00（占位符）
        boolean isMidnightPlaceholder = activityDate.toLocalTime().equals(java.time.LocalTime.MIDNIGHT);

        if (!isMidnightPlaceholder) {
            // 规则 1: 时间部分不是 00:00:00，直接使用
            log.debug("使用活动的精确时间：activityId={}, time={}", activity.getId(), activityDate);
            return activityDate;
        }

        // 时间部分是 00:00:00，需要根据 timePeriod 判断
        if (timePeriod == null) {
            log.warn("活动时间为占位符但缺少时间段信息：activityId={}", activity.getId());
            return null;
        }

        if (timePeriod == ActivityPojo.TimePeriod.EARLY_MORNING) {
            // 规则 2: timePeriod 为 EARLY_MORNING，00:00:00 是真实时间
            log.debug("活动时间为凌晨零点：activityId={}", activity.getId());
            return activityDate;
        } else {
            // 规则 3: timePeriod 不为 EARLY_MORNING，00:00:00 是占位符，根据时间段推算
            LocalDate baseDate = activityDate.toLocalDate();
            LocalDateTime inferredTime = baseDate.atTime(getTimeFromPeriod(timePeriod));
            log.debug("根据时间段推算活动时间：activityId={}, timePeriod={}, inferredTime={}",
                    activity.getId(), timePeriod, inferredTime);
            return inferredTime;
        }
    }

    /**
     * 根据时间段获取具体时间
     *
     * @param timePeriod 时间段枚举
     * @return 对应的时间（小时:分钟）
     */
    private LocalTime getTimeFromPeriod(ActivityPojo.TimePeriod timePeriod) {
        return switch (timePeriod) {
            case EARLY_MORNING -> LocalTime.of(0, 0);   // 凌晨 00:00
            case MORNING -> LocalTime.of(9, 0);         // 上午 9:00
            case NOON -> LocalTime.of(12, 0);           // 中午 12:00
            case AFTERNOON -> LocalTime.of(15, 0);      // 下午 15:00
            case EVENING -> LocalTime.of(20, 0);        // 晚上 19:00
        };
    }

    /**
     * 根据经纬度获取和风天气系统的LocationId
     * 调用和风天气地理位置查询API，将经纬度转换为和风天气系统的城市唯一标识
     *
     * @param longitude 经度（保留两位小数）
     * @param latitude  纬度（保留两位小数）
     * @return 和风天气LocationId，如果请求失败则返回 null
     */
    public String getLocationId(String longitude, String latitude) {
        // 从用户配置中获取 API 密钥和服务地址
        String apiKey = userSettingsService.getLatestApiConfig().getWeatherApiKey();
        String apiHost = userSettingsService.getLatestApiConfig().getWeatherApiHost();
        // 构造经纬度字符串
        String location = longitude + "," + latitude;

        try {
            // 对经纬度进行 URL 编码，防止特殊字符（如逗号）导致请求格式错误
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            // 构造完整的 API 请求 URL
            String url = apiHost + LOCATION_LOOKUP_PATH + "?location=" + encodedLocation + "&key=" + apiKey;
            // 执行 HTTP 请求并获取 LocationId
            return executeLocationRequest(url, location);
        } catch (Exception e) {
            log.error("URL 编码失败，经纬度：{}", location, e);
            return null;
        }
    }

    /**
     * 获取天气信息
     *
     * @param locationId LocationId，和风天气系统的城市唯一标识
     * @param date       日记日期，用于查询对应日期的天气
     * @return 天气信息对象，包含温度、天气状况等；如果查询失败则返回 null
     */
    public WeatherInformation getWeather(String locationId, LocalDateTime date) {
        // 从用户配置中获取 API 密钥和服务地址
        String apiKey = userSettingsService.getLatestApiConfig().getWeatherApiKey();
        String apiHost = userSettingsService.getLatestApiConfig().getWeatherApiHost();

        // 将日期格式化为 API 要求的格式（yyyyMMdd）
        String formattedDate = DateTimeUtils.format(DateTimeUtils.toLocalDate(date), TIME_FORMAT);
        // 构造完整的 API 请求 URL
        String url = apiHost + WEATHER_NOW_PATH + "?location=" + locationId + "&date=" + formattedDate + "&key="
                + apiKey;
        log.info(url);
        // 执行 HTTP 请求并获取天气信息
        return executeWeatherRequest(url, locationId, date);
    }

    // ==================== 私有辅助方法 ====================

    private String executeLocationRequest(String url, String location) {
        // 创建 HTTP GET 请求对象
        HttpGet httpGet = new HttpGet(url);
        // 使用 try-with-resources 自动关闭资源
        try (CloseableHttpClient httpClient = HttpClients.createSystem();
                ClassicHttpResponse response = httpClient.executeOpen(null, httpGet, null)) {

            // 检查 HTTP 响应状态码，如果不在 200-299 范围内表示失败
            if (isHttpFailure(response)) {
                // HTTP 失败，记录错误日志
                log.error("LocationId API HTTP 错误，状态码：{}, 错误信息：{}", response.getCode(), response);
                return null;
            }
            // HTTP 成功，解析 JSON 响应获取 LocationId
            return parseLocationResponse(response, location);

        } catch (Exception e) {
            // 捕获网络异常、IO 异常等
            log.error("调用 LocationId API 异常，经纬度：{}", location, e);
            return null;
        }
    }

    /**
     * 解析位置查询响应
     *
     * @param response HTTP 响应对象
     * @param location 经纬度字符串，用于日志记录
     * @return LocationId，如果解析失败则返回 null
     */
    private String parseLocationResponse(ClassicHttpResponse response, String location) throws Exception {
        // 提取 HTTP 响应实体内容
        String result = extractResponseEntity(response);
        if (result == null) {
            log.error("LocationId API 响应为空，经纬度：{}", location);
            return null;
        }

        // 解析 JSON 响应
        JsonNode root = objectMapper.readTree(result);
        // 检查业务状态码，判断 API 调用是否失败
        if (isApiFailure(root)) {
            logApiError("LocationId API 业务错误", root, location);
            return null;
        }

        // 获取 location 数组字段
        JsonNode locationArrayNode = root.path(LOCATION_ARRAY_FIELD);
        if (locationArrayNode.isMissingNode() || locationArrayNode.isNull() || !locationArrayNode.isArray()) {
            log.error("LocationId API 响应中缺少 {} 数组字段，经纬度：{}", LOCATION_ARRAY_FIELD, location);
            return null;
        }

        // 检查数组是否为空
        if (locationArrayNode.isEmpty()) {
            log.error("LocationId API 返回的 location 数组为空，经纬度：{}", location);
            return null;
        }

        // 获取第一个 location 对象
        JsonNode firstLocation = locationArrayNode.get(0);
        if (firstLocation == null || firstLocation.isMissingNode()) {
            log.error("LocationId API 响应中第一个 location 对象为空，经纬度：{}", location);
            return null;
        }

        // 获取 id 字段（locationId）
        JsonNode locationIdNode = firstLocation.path("id");
        if (locationIdNode.isMissingNode() || locationIdNode.isNull()) {
            log.error("LocationId API 响应中 location 对象缺少 id 字段，经纬度：{}", location);
            return null;
        }

        // 将获取的ID返回
        return locationIdNode.asText();
    }

    /**
     * 执行天气查询请求
     *
     * @param url        完整的 API 请求 URL
     * @param locationId LocationId，用于日志记录
     * @param date       日记日期，用于日志记录
     * @return WeatherInformation 天气信息对象，如果请求或解析失败则返回 null
     */
    private WeatherInformation executeWeatherRequest(String url, String locationId, LocalDateTime date) {
        // 创建 HTTP GET 请求对象
        HttpGet httpGet = new HttpGet(url);
        // 使用 try-with-resources 自动关闭资源
        try (CloseableHttpClient httpClient = HttpClients.createSystem();
                ClassicHttpResponse response = httpClient.executeOpen(null, httpGet, null)) {

            // 检查 HTTP 响应状态码，如果不在 200-299 范围内表示失败
            if (isHttpFailure(response)) {
                // HTTP 失败，记录错误日志
                log.error("天气 API HTTP 错误，状态码：{}, LocationId: {}", response.getCode(), locationId);
                return null;
            }
            // HTTP 成功，解析 JSON 响应获取天气信息
            return parseWeatherResponseWrapper(response, locationId, date);

        } catch (Exception e) {
            // 捕获网络异常、IO 异常等
            log.error("调用天气 API 异常，LocationId: {}", locationId, e);
            return null;
        }
    }

    /**
     * 解析天气查询响应
     *
     * @param response   HTTP 响应对象
     * @param locationId LocationId，用于日志记录
     * @param date       日记日期，用于日志记录
     * @return WeatherInformation 天气信息对象，如果解析失败则返回 null
     * @throws Exception 当读取响应实体或解析 JSON 失败时抛出
     */
    private WeatherInformation parseWeatherResponseWrapper(ClassicHttpResponse response, String locationId,
            LocalDateTime date) throws Exception {
        // 提取 HTTP 响应实体内容
        String result = extractResponseEntity(response);
        if (result == null) {
            log.error("天气 API 响应为空，LocationId: {}", locationId);
            return null;
        }

        // 解析 JSON 响应
        JsonNode root = objectMapper.readTree(result);
        // 检查业务状态码，判断 API 调用是否失败
        if (isApiFailure(root)) {
            logApiError("天气 API 业务错误", root, String.valueOf(locationId));
            return null;
        }

        // 解析具体的天气数据
        return parseWeatherResponse(root, locationId, date);
    }

    /**
     * 提取 HTTP 响应实体内容
     *
     * @param response HTTP 响应对象
     * @return 响应体字符串，如果实体为空则返回 null
     * @throws Exception 当读取实体失败时抛出
     */
    private String extractResponseEntity(ClassicHttpResponse response) throws Exception {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        // 使用 UTF-8 编码解码响应内容
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    /**
     * 检查 API 业务状态码是否失败
     *
     * @param root JSON 根节点
     * @return true 表示业务失败，false 表示业务成功
     */
    private boolean isApiFailure(JsonNode root) {
        JsonNode codeNode = root.path(CODE_FIELD);
        return !codeNode.isTextual() || !SUCCESS_STATUS.equals(codeNode.asText());
    }

    /**
     * 记录 API 业务错误日志
     *
     * @param message 错误描述前缀
     * @param root    JSON 根节点
     * @param context 上下文信息，如经纬度或 LocationId
     */
    private void logApiError(String message, JsonNode root, String context) {
        // 提取错误消息，如果不存在则使用默认值
        String errorMsg = root.path(MESSAGE_FIELD).asText("未知错误");
        // 记录详细的错误日志，包括状态码、错误消息和上下文
        log.error("{}, 状态码：{}, 消息：{}, 上下文：{}", message, root.path(CODE_FIELD).asText("未知"), errorMsg, context);
    }

    /**
     * 解析天气数据响应
     *
     * @param root       JSON 根节点
     * @param locationId LocationId，用于日志记录
     * @param date       日记日期，用于匹配小时数据
     * @return WeatherInformation 天气信息对象，如果解析失败则返回 null
     */
    private WeatherInformation parseWeatherResponse(JsonNode root, String locationId, LocalDateTime date) {
        DailyWeatherData dailyWeather = parseDailyWeatherData(root, locationId);
        if (dailyWeather == null) {
            return null;
        }

        // 初始化天气状况和图标
        Double weathertemp = null;
        String weatherCondition = null;
        String weatherIcon = null;

        // 遍历小时数据，查找匹配的小时记录
        if (dailyWeather.hourlyRecords != null && !dailyWeather.hourlyRecords.isEmpty()) {
            for (HourlyWeatherRecord record : dailyWeather.hourlyRecords) {
                log.debug("比较时间：API 时间={}, 日记时间={}", record.time, date);

                // 改进：使用前缀匹配确保日期格式正确（避免 "2020-07-25" 匹配到 "2020-07-250"）
                if (DateTimeUtils.isSameTime(record.time, date)) {
                    weathertemp = record.temp;
                    weatherCondition = record.text;
                    weatherIcon = record.icon;
                    log.info("找到匹配的时间点：{}, 天气状况：{}, 天气图标：{}", record.time, weatherCondition, weatherIcon);
                    break;
                }
            }
        }

        // 如果没有找到匹配的小时数据，使用每日数据作为备选
        if (weatherCondition == null || weatherIcon == null) {
            log.warn("未找到匹配的小时天气数据，使用每日数据作为备选，LocationId: {}, 日记时间：{}", locationId, date);
            weatherIcon = dailyWeather.iconDay;
            weatherCondition = dailyWeather.textDay;
        }

        // 创建并返回天气信息对象
        return new WeatherInformation(weathertemp, dailyWeather.tempMax, dailyWeather.tempMin, weatherCondition,
                weatherIcon);
    }

    /**
     * 从JSON根节点解析每日天气数据
     * 提取每日汇总数据和每小时详细数据
     *
     * @param root       JSON 根节点
     * @param locationId LocationId，用于日志记录
     * @return 每日天气数据对象，如果解析失败则返回 null
     */
    private DailyWeatherData parseDailyWeatherData(JsonNode root, String locationId) {
        // 获取每日天气数据节点
        JsonNode dailyNode = root.path(WEATHER_DAILY_FIELD);
        if (dailyNode.isMissingNode() || dailyNode.isNull()) {
            log.error("天气 API 响应缺少 {}，LocationId: {}", WEATHER_DAILY_FIELD, locationId);
            return null;
        }

        // 提取温度数据
        double tempMax = dailyNode.path("tempMax").asDouble();
        double tempMin = dailyNode.path("tempMin").asDouble();
        String textDay = dailyNode.has("textDay") ? dailyNode.path("textDay").asText() : null;
        String iconDay = dailyNode.has("iconDay") ? dailyNode.path("iconDay").asText() : null;

        // 解析每小时天气数据
        List<HourlyWeatherRecord> hourlyRecords = new ArrayList<>();
        JsonNode hourlyNode = root.path(WEATHER_HOURLY_FIELD);
        if (!hourlyNode.isMissingNode() && !hourlyNode.isNull() && hourlyNode.isArray()) {
            for (JsonNode hour : hourlyNode) {
                LocalDateTime time = DateTimeUtils.parseDateTimeWithOffset(hour.path("time").asText());
                double temp = hour.path("temp").asDouble();
                String text = hour.path("text").asText();
                String icon = hour.path("icon").asText();
                hourlyRecords.add(new HourlyWeatherRecord(time, temp, text, icon));
            }
        }

        return new DailyWeatherData(tempMax, tempMin, textDay, iconDay, hourlyRecords);
    }

    /**
     * 判断 HTTP 请求是否失败
     *
     * @param response HTTP 响应对象
     * @return true 表示失败，false 表示成功
     */
    private boolean isHttpFailure(ClassicHttpResponse response) {
        int code = response.getCode();
        return code < 200 || code >= 300;
    }

    // ==================== 私有辅助方法 ==================

    /**
     * 将数值保留到小数点后两位（四舍五入，不足补 0）
     *
     * @param value 待处理的数值
     * @return 保留两位小数后的字符串表示（不足补 0）
     */
    private String formatToTwoDecimalPlaces(double value) {
        return String.format("%.2f", value);
    }

    // ==================== 内部类 ====================

    /**
     * 天气信息内部类
     * 用于封装从 API 获取的天气数据，包括温度范围、天气状况和天气图标
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherInformation {
        /** 温度（摄氏度），可能为 null（当无法获取具体温度时） */
        private Double temp;
        /** 最高温度（摄氏度） */
        private Double tempMax;
        /** 最低温度（摄氏度） */
        private Double tempMin;
        /** 天气状况描述，如"晴"、"多云"、"雨"等 */
        private String weatherCondition;
        /** 天气图标代码，用于前端展示 */
        private String weatherIcon;
    }

    /**
     * 待处理天气的日记信息内部类
     * 用于封装需要查询天气的日记相关信息，包括日记ID、经纬度和城市
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryWeatherPending {
        /** 日记ID */
        private Integer diaryId;
        /** 纬度 */
        private double latitude;
        /** 经度 */
        private double longitude;
        /** 城市名称 */
        private String city;
    }

    /**
     * 完整天气数据内部类
     * 用于封装从 API 获取的完整天气信息，包括每日汇总数据和每小时详细数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyWeatherData {
        /** 最高温度（摄氏度） */
        private double tempMax;
        /** 最低温度（摄氏度） */
        private double tempMin;
        /** 白天天气状况描述 */
        private String textDay;
        /** 白天天气图标代码 */
        private String iconDay;
        /** 每小时天气记录列表，可能为空 */
        private List<HourlyWeatherRecord> hourlyRecords;
    }

    /**
     * 每小时天气记录内部类
     * 用于封装特定时间点的天气信息，包括时间、天气状况和图标
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyWeatherRecord {
        /** 时间点 */
        private LocalDateTime time;
        /** 温度（摄氏度） */
        private double temp;
        /** 天气状况描述 */
        private String text;
        /** 天气图标代码 */
        private String icon;
    }

}