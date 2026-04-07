package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.AI.EventDeduplicationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

/**
 * AI分析调用类
 * 用于调用SpringAI使用AI大模型行AI内容分析
 *
 * @author Yichaoxuan
 * @since 2026-02-17
 */
@Slf4j
@Service
public class CallAiAnalysis {

    private final ChatClient chatClient;

    public CallAiAnalysis(ChatClient.Builder chatModel) {
        this.chatClient = chatModel.build();
    }

    /**
     * 调用AI进行内容分析,返回结果为标签对象、
     *
     * @param prompt 封装了待分析的内容和分析规则的提示词
     * @return AI分析结果
     */
    public AITagJson analyzeContent(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .entity(AITagJson.class);
    }

    /**
     * 调用AI进行人物或地点标签去重分析,返回结果为字符串
     *
     * @param prompt 封装了待去重的人物或地点标签和分析规则的提示词
     * @return AI分析结果 -1为无匹配人物或地点标签，>0为有匹配人物或地点标签
     */
    public String analyze(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    /**
     * 调用AI进行事件去重分析,返回结果为包含事件Id和事件描述的结果对象
     *
     * @param prompt 封装了待去重的事件标签和分析规则的提示词
     * @return 包含事件Id（AI分析结果 -1为无匹配事件标签，>0为有匹配事件标签）和事件描述的结果对象
     */
    public EventDeduplicationResult deduplication(Prompt prompt) {
        return chatClient.prompt(prompt)
                .call()
                .entity(EventDeduplicationResult.class);
    }
}