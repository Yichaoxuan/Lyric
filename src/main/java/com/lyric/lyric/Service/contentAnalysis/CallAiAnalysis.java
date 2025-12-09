package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.Pojo.AI.AITagJson;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI分析调用类
 * 用于调用SpringAI使用deepseek进行AI内容分析
 *
 * @author Yichaoxuan
 * @since 2025-12-08
 */
@Service
public class CallAiAnalysis {

    private final ChatClient chatClient;

    private final UserSettingsService userSettingsService;

    public CallAiAnalysis(ChatClient.Builder chatModel, UserSettingsService userSettingsService) {
        this.chatClient = chatModel.build();
        this.userSettingsService = userSettingsService;
    }

    /**
     * 调用AI进行内容分析,返回结果为标签对象
     * @param message 需要分析的内容
     * @return AI分析结果
     */
    public AITagJson analyzeContent(String message) {
        return chatClient.prompt()
                .user(message)
                .system(userSettingsService.getAnalysisRules())
                .call()
                .entity(AITagJson.class);
    }
}