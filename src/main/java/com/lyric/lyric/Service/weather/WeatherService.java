package com.lyric.lyric.Service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyric.lyric.Mapper.diary.DiaryMapper;
import com.lyric.lyric.Mapper.environment.WeatherMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
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
import java.util.List;

/**
 * 天气服务类
 * 提供天气信息查询和处理功能，包括获取LocationId、查询天气信息、保存天气数据到数据库等
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
@Slf4j
@Service
public class WeatherService {

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

    private final DiaryMapper diaryMapper;
    private final WeatherMapper weatherMapper;
    private final UserSettingsService userSettingsService;
    private final EventMapper eventMapper;

    /** JSON 对象映射器，用于解析 API 返回的 JSON 数据 */
    private final ObjectMapper objectMapper;

    public WeatherService(DiaryMapper diaryMapper, WeatherMapper weatherMapper, UserSettingsService userSettingsService,
                          EventMapper eventMapper, ObjectMapper objectMapper) {
        this.diaryMapper = diaryMapper;
        this.weatherMapper = weatherMapper;
        this.userSettingsService = userSettingsService;
        this.objectMapper = objectMapper;
        this.eventMapper = eventMapper;
    }

    /**
     * 处理日记天气：获取天气信息并保存到数据库
     * 主入口方法，处理未关联天气的日记，获取并保存天气数据
     */
    public void processWeatherForDiary() {
        // 步骤 1: 获取未关联天气信息的日记
        List<DiaryWeatherPending> diaryDiaries = diaryMapper.selectDiariesWithoutWeather();

        // 判断列表是否为空
        if (diaryDiaries.isEmpty()) {
            log.info("没有待处理的日记，跳过天气处理");
            return;
        }

        for (DiaryWeatherPending diary : diaryDiaries) {

            // 从 diary 对象中提取属性
            Integer diaryId = diary.getDiaryId();
            String city = diary.getCity();
            LocalDateTime date = eventMapper.selectMinSubEventDateByDiaryId(diary.getDiaryId()); // 日记日期（包含时间，但实际只有日期部分）
            double latitude = diary.getLatitude();
            double longitude = diary.getLongitude();

            // 步骤 2: 验证实体类的有效性
            if (diaryId == null || !StringUtils.hasText(city) || date == null) {
                log.error("参数无效，日记 ID: {}, 城市：{}, 日期：{}", diaryId, city, date);
                continue; // 跳过当前日记，继续处理下一个
            }

            // 步骤 3: 根据经纬度获取 LocationId
            String locationId = getLocationId(formatToTwoDecimalPlaces(longitude), formatToTwoDecimalPlaces(latitude));
            if (locationId == null) {
                log.error("获取 LocationId 失败，无法查询天气，日记 ID: {}, 经纬度：{},{}", diaryId, longitude, latitude);
                continue;
            }

            // 步骤 4: 使用 LocationId 查询天气信息
            WeatherInformation weatherInfo = getWeather(locationId, date);
            if (weatherInfo == null) {
                log.error("获取天气信息失败，日记 ID: {}, LocationId: {}", diaryId, locationId);
                continue;
            }

            // 步骤 5: 保存天气信息到数据库
            saveWeatherToDatabase(diaryId, city, date, weatherInfo);
        }
    }

    /**
     * 根据经纬度获取和风天气 LocationId
     *
     * @param longitude 经度，范围 -180 到 180
     * @param latitude  纬度，范围 -90 到 90
     * @return LocationId，用于后续天气查询；如果查询失败则返回 null
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

        // 执行 HTTP 请求并获取天气信息
        return executeWeatherRequest(url, locationId, date);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 保存天气信息到数据库
     *
     * @param diaryId     日记 ID，用于关联天气信息到具体日记
     * @param city        城市名称，用于显示和日志记录
     * @param date        日记日期，用于记录天气对应的日期
     * @param weatherInfo 天气信息对象，包含温度、天气状况等详细数据
     */
    private void saveWeatherToDatabase(Integer diaryId, String city, LocalDateTime date,
                                       WeatherInformation weatherInfo) {
        // 将 LocalDateTime 转换为 LocalDate，适配数据库字段类型
        LocalDate localDate = DateTimeUtils.toLocalDate(date);
        // 创建 WeatherPojo 对象，封装天气数据
        WeatherPojo weatherPojo = new WeatherPojo(diaryId, city, localDate, weatherInfo);
        // 调用 Mapper 将天气信息插入数据库
        weatherMapper.insert(weatherPojo);
        // 记录成功日志，便于后续追踪
        log.info("天气信息保存成功，日记 ID: {}, 城市：{}, 日期：{}", diaryId, city, date);
    }

    /**
     * 执行位置查询请求
     *
     * @param url      完整的 API 请求 URL
     * @param location 经纬度字符串，用于日志记录
     * @return LocationId，如果请求或解析失败则返回 null
     */
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
        // 获取每日天气数据节点
        JsonNode resultNode = root.path(WEATHER_DAILY_FIELD);
        if (resultNode.isMissingNode() || resultNode.isNull()) {
            log.error("天气 API 响应缺少 {}，LocationId: {}", WEATHER_DAILY_FIELD, locationId);
            return null;
        }

        // 提取最高温度和最低温度
        double tempMax = resultNode.path("tempMax").asDouble();
        double tempMin = resultNode.path("tempMin").asDouble();

        // 检查温度逻辑关系：最低温度不应高于最高温度
        if (tempMin > tempMax) {
            log.warn("最低温度高于最高温度，LocationId: {}, tempMax: {}, tempMin: {}", locationId, tempMax, tempMin);
        }

        // 初始化天气状况和图标
        String weatherCondition = null;
        String weatherIcon = null;

        // 获取每小时天气数据节点
        JsonNode hourlyNode = root.path(WEATHER_HOURLY_FIELD);
        if (hourlyNode.isMissingNode() || hourlyNode.isNull()) {
            log.error("天气 API 响应缺少 {}，LocationId: {}", WEATHER_HOURLY_FIELD, locationId);
            return null;
        }

        // 遍历小时数据，查找匹配的小时记录
        for (JsonNode hour : hourlyNode) {
            LocalDateTime time = DateTimeUtils.parseDateTimeWithOffset(hour.path("time").asText()); // 格式如 "2020-07-25
            // 00:00"
            // 改进：使用前缀匹配确保日期格式正确（避免 "2020-07-25" 匹配到 "2020-07-250"）
            if (DateTimeUtils.isSameTime(time, date)) {
                weatherCondition = hour.path("text").asText();
                weatherIcon = hour.path("icon").asText();
                break;
            }
        }

        // 创建并返回天气信息对象
        return new WeatherInformation(tempMax, tempMin, weatherCondition, weatherIcon);
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
        /** 最高温度（摄氏度） */
        private double tempMax; // 最高温度
        /** 最低温度（摄氏度） */
        private double tempMin; // 最低温度
        /** 天气状况描述，如"晴"、"多云"、"雨"等 */
        private String weatherCondition; // 天气描述
        /** 天气图标代码，用于前端展示 */
        private String weatherIcon; // 天气图标
    }

    /**
     * 待处理天气的日记信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiaryWeatherPending {
        private Integer diaryId;
        private double latitude;
        private double longitude;
        private String city;
    }

}