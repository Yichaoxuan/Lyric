package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.POJO.AI.AITagJson;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

/**
 * AI分析调用类
 * 用于调用SpringAI使用deepseek进行AI内容分析
 *
 * @author Yichaoxuan
 * @since 2025-12-11
 */
@Service
public class CallAiAnalysis {

    private final ChatClient chatClient;

    public CallAiAnalysis(ChatClient.Builder chatModel) {
        this.chatClient = chatModel.build();
    }

    /**
     * 调用AI进行内容分析,返回结果为标签对象
     * @param prompt 封装了待分析的内容和分析规则的提示词
     * @return AI分析结果
     */
    public AITagJson analyzeContent(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .entity(AITagJson.class);
    }

    /**
     * 调用AI进行内容分析,返回结果为字符串
     * @param prompt 封装了待去重的人物标签和分析规则的提示词
     * @return AI分析结果 0为无匹配人物标签，>0为有匹配人物标签
     */
    public String analyze(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }
}