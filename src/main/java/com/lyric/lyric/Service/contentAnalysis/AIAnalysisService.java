package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AI分析服务类
 * 用于处理日记内容的AI分析，包括标签生成等功能
 *
 * @author Yichaoxuan
 * @since 2025-11-24
 */
@Service
public class AIAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsService.class);

    private final UserSettingsService userSettingsService;

    private final CallAiAnalysis callAiAnalysis;

    private final DiaryMapper diaryMapper;

    /**
     * AI分析服务构造函数
     *
     * @param userSettingsService 用户设置服务
     * @param callAiAnalysis     AI调用服务
     * @param diaryMapper        日记数据访问对象
     */
    public AIAnalysisService(UserSettingsService userSettingsService, CallAiAnalysis callAiAnalysis, DiaryMapper diaryMapper) {
        this.userSettingsService = userSettingsService;
        this.callAiAnalysis = callAiAnalysis;
        this.diaryMapper = diaryMapper;
    }

    /**
     * 对指定日记进行标签分析
     * 通过日记ID获取日记内容，并调用AI分析生成标签
     *
     * @param diaryId 日记ID
     */
    public void tagAnalysis(Integer diaryId) {
       tagAnalysis(diaryId, diaryMapper.selectById(diaryId).getContent());
    }

    /**
     * 对指定内容进行标签分析
     * 使用用户设置的分析规则，调用AI分析内容并输出结果
     *
     * @param diaryId 日记ID
     * @param content 需要分析的内容
     * @return AI分析结果
     */
    public void  tagAnalysis(Integer diaryId, String content) {
        logger.info("开始对日记进行标签分析");

        // 判断是否开启了AI分析功能与标签生成功能
        if (!userSettingsService.isFeatureEnabled(UserFunctionEnum.AI_ANALYTICS) || !userSettingsService.isFeatureEnabled(UserFunctionEnum.SMART_LABEL_GENERATION)) {
            logger.info("AI分析功能或智能标签生成功能未开启，跳过标签分析");
        }

        // 获取用户设置的分析规则
        String rules = userSettingsService.getUserSettings().getAnalysisRules();
        // 将内容和规则组合成消息
        String message = content + "\n" + rules;
        // 调用AI分析内容
        String result = callAiAnalysis.analyzeContent(message);
        // 输出分析结果
        System.out.println(result);
    }
}