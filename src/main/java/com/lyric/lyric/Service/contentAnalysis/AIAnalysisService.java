package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.AI.EventDeduplicationResult;
import com.lyric.lyric.POJO.message.MessageConfigPojo;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.POJO.AI.EventDeduplicationData;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.json.JsonConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI分析服务类
 * 用于处理日记内容的AI分析，包括标签生成,响应消息生成等功能
 *
 * @author Yichaoxuan
 * @since 2026-02-08
 */
@Slf4j
@Service
public class AIAnalysisService {

    private final UserSettingsService userSettingsService;

    private final CallAiAnalysis callAiAnalysis;

    private final PromptConstructionService promptConstructionService;

    /**
     * AI分析服务构造函数
     *
     * @param userSettingsService 用户设置服务
     * @param callAiAnalysis     AI调用服务
     * @param promptConstructionService 提示词构造服务
     */
    public AIAnalysisService(UserSettingsService userSettingsService, CallAiAnalysis callAiAnalysis, PromptConstructionService promptConstructionService) {
        this.userSettingsService = userSettingsService;
        this.callAiAnalysis = callAiAnalysis;
        this.promptConstructionService = promptConstructionService;
    }

    /**
     * 对指定内容进行标签分析
     * 使用用户设置的分析规则，调用AI分析内容并输出结果
     *
     * @param content 需要分析的内容
     */
    public AITagJson  tagAnalysis(String content, String  diaryDate) {
        log.info("开始对日记进行标签分析");

        // 检查是否开启了 AI 分析功能
        if (!isAiAnalyticsEnabled()) {
            return null;
        }

        // 构建提示词
        Prompt prompt = promptConstructionService.buildPrompt(diaryDate + " " +content);

        // 调用AI分析内容
        AITagJson AITag = callAiAnalysis.analyzeContent(prompt);
        log.info("AI分析完毕，结果为：{}", AITag.toString());
        return AITag;
    }

    /**
     * 人物标签去重分析
     *
     * @param newPersonName 新人物名称
     * @param newPersonInfo 新人物信息
     * @param candidatePersons 候选人物列表
     * @return 如果存在重复的标签，则返回重复标签的索引，否则返回-1
     */
    public Integer personTagDeduplicationAnalysis(String newPersonName, AITagJson.PersonInfo newPersonInfo, List<PersonPojo> candidatePersons) {
        log.info("开始对人物标签进行AI去重分析");

        // 检查是否开启了 AI 分析功能
        if (!isAiAnalyticsEnabled()) {
            return -1;
        }

        //构建提示词
        Prompt prompt = promptConstructionService.buildPersonTagDeduplicationPrompt(newPersonName, newPersonInfo, candidatePersons);
        log.info("提示词：{}", prompt.toString());
        return Integer.parseInt(callAiAnalysis.analyze(prompt));
    }

    /**
     * 地点标签去重分析
     *
     * @param newLocationName 新地点名称
     * @param newLocationInfo 新地点信息
     * @param candidateLocations 候选地点列表
     * @return 如果存在重复的标签，则返回重复标签的索引，否则返回-1
     */
    public Integer locationTagDeduplicationAnalysis(String newLocationName, AITagJson.LocationInfo newLocationInfo, List<LocationPojo> candidateLocations) {
        log.info("开始对地点标签进行AI去重分析");

        // 检查是否开启了 AI 分析功能
        if (!isAiAnalyticsEnabled()) {
            return -1;
        }

        // 构建提示词
        Prompt prompt = promptConstructionService.buildLocationTagDeduplicationPrompt(newLocationName, newLocationInfo, candidateLocations);
        log.info("提示词：{}", prompt.toString());
        return Integer.parseInt(callAiAnalysis.analyze(prompt));
    }

    /**
     * 事件标签去重分析
     *
     * @param eventDeduplicationData 事件标签去重数据
     *
     * @return 返回匹配的事件的Id，如果没有匹配的，则返回-1
     */
    public EventDeduplicationResult eventTagDeduplicationAnalysis(EventDeduplicationData eventDeduplicationData) {
        log.info("开始对事件标签进行 AI 去重分析");
    
        // 检查是否开启了 AI 分析功能
        if (!isAiAnalyticsEnabled()) {
            return null;
        }

        // 构建提示词
        Prompt prompt = promptConstructionService.buildEventDeduplicationPrompt(eventDeduplicationData);
        log.info("事件标签去重提示词{}", prompt);

        EventDeduplicationResult EventDeduplication = callAiAnalysis.deduplication(prompt);

        log.info("Ai分析完毕，结果为{}", EventDeduplication);

        return EventDeduplication;
    }

    /**
     * 生成指定类型的响应消息
     *
     * @param responseStyleInstructions 响应消息的角色设定
     * @return 生成的响应消息配置指令
     */
    public MessageConfigPojo generateResponseMessage(String responseStyleInstructions) {
        // 构建提示词
        Prompt prompt = promptConstructionService.buildResponseMessagePrompt(responseStyleInstructions);

        // 调用AI生成消息
        String result = callAiAnalysis.analyze(prompt);

        // 清理AI返回的结果，去除可能的额外字符
        result = JsonConversionUtils.removeExtraCharacters(result);

        // 验证结果是否为有效的JSON
        if (result == null || !JsonConversionUtils.isValidJson(result)) {
            log.error("AI返回的结果不是有效的JSON格式: {}", result);
            return null;
        }

        log.info("AI返回的结果为：{}", result);

        //将生成的消息解析为MessageConfigPojo
        return JsonConversionUtils.fromJson(result, MessageConfigPojo.class);
    }

    /**
     * 检查是否开启了 AI 分析功能
     * <p>
     * 验证用户设置中是否同时启用了 AI 分析和智能标签生成功能
     * </p>
     *
     * @return true-已开启；false-未开启
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAiAnalyticsEnabled() {
        if (userSettingsService.isFeatureEnabled(UserFunctionEnum.AI_ANALYTICS)
                && userSettingsService.isFeatureEnabled(UserFunctionEnum.SMART_LABEL_GENERATION)) {
            return true;
        }  else {
            log.info("AI分析功能或智能标签生成功能未开启，跳过标签分析");
            return false;
        }
    }
}