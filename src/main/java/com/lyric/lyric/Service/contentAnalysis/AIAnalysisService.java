package com.lyric.lyric.Service.contentAnalysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI分析服务类
 * 用于处理日记内容的AI分析，包括标签生成,响应消息生成等功能
 *
 * @author Yichaoxuan
 * @since 2025-12-11
 */
@Service
public class AIAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisService.class);

    private final UserSettingsService userSettingsService;

    private final CallAiAnalysis callAiAnalysis;

    private final DiaryMapper diaryMapper;

    private final PromptConstructionService promptConstructionService;

    /**
     * AI分析服务构造函数
     *
     * @param userSettingsService 用户设置服务
     * @param callAiAnalysis     AI调用服务
     * @param diaryMapper        日记数据访问对象
     */
    public AIAnalysisService(UserSettingsService userSettingsService, CallAiAnalysis callAiAnalysis, DiaryMapper diaryMapper, PromptConstructionService promptConstructionService) {
        this.userSettingsService = userSettingsService;
        this.callAiAnalysis = callAiAnalysis;
        this.diaryMapper = diaryMapper;
        this.promptConstructionService = promptConstructionService;
    }

    /**
     * 人物标签去重分析
     */
    public Integer personTagDeduplicationAnalysis(String newPersonName, AITagJson.PersonInfo newPersonInfo, List<PersonPojo> candidatePersons) {
        logger.info("开始对人物标签进行去重分析");

        // 判断是否开启了AI分析功能与标签生成功能
        if (!userSettingsService.isFeatureEnabled(UserFunctionEnum.AI_ANALYTICS) || !userSettingsService.isFeatureEnabled(UserFunctionEnum.SMART_LABEL_GENERATION)) {

            return -1;
        }

        //构建提示词
        Prompt prompt = promptConstructionService.buildPersonTagDeduplicationPrompt(newPersonName, newPersonInfo, candidatePersons);
        logger.info("提示词：{}", prompt.toString());
        return Integer.parseInt(callAiAnalysis.analyze(prompt));
    }

    /**
     * 对指定内容进行标签分析
     * 使用用户设置的分析规则，调用AI分析内容并输出结果
     *
     * @param content 需要分析的内容
     */
    public AITagJson  tagAnalysis(String content) throws JsonProcessingException {
        logger.info("开始对日记进行标签分析");

        // 判断是否开启了AI分析功能与标签生成功能
        if (!userSettingsService.isFeatureEnabled(UserFunctionEnum.AI_ANALYTICS) || !userSettingsService.isFeatureEnabled(UserFunctionEnum.SMART_LABEL_GENERATION)) {
            logger.info("AI分析功能或智能标签生成功能未开启，跳过标签分析");
            return null;
        }

        // 构建提示词
        Prompt prompt = promptConstructionService.buildPrompt(content);
        logger.info("提示词：{}", prompt.toString());

        // 调用AI分析内容
        AITagJson AITag = callAiAnalysis.analyzeContent(prompt);
        logger.info("AI分析结果：{}", AITag.toString());
        return AITag;
    }

    /**
     * 生成指定类型的响应消息
     *
     * @param newMessageConfigInstructions 新地响应消息配置指令
     * @return 生成的响应消息配置指令
     */
//    public MessageConfigPojo generateResponseMessage(String newMessageConfigInstructions) {
//        // 获取生成规则
//        String rules = userSettingsService.getResponseMessageGenerationRules();
//        // 将规则和消息类型组合成消息
//        String message = newMessageConfigInstructions + "\n" + rules;
//        // 调用AI生成消息
//        String result = callAiAnalysis.analyzeContent(message);
//
//        // 清理AI返回的结果，去除可能的额外字符
//        result = JsonConversionUtils.removeExtraCharacters(result);
//
//        // 验证结果是否为有效的JSON
//        if (result == null || !JsonConversionUtils.isValidJson(result)) {
//            logger.error("AI返回的结果不是有效的JSON格式: {}", result);
//            return null;
//        }
//
//        //将生成的消息解析为MessageConfigPojo
//        return JsonConversionUtils.fromJson(result, MessageConfigPojo.class);
//    }
}