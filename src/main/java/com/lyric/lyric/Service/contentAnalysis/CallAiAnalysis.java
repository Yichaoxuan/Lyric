package com.lyric.lyric.Service.contentAnalysis;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import io.reactivex.Flowable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI分析调用类
 * 用于调用DashScope SDK进行AI内容分析
 *
 * @author Yichaoxuan
 * @since 2025-11-24
 */
@Service
public class CallAiAnalysis {

    private final UserSettingsService userSettingsService;

    public CallAiAnalysis(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    /**
     * 构建AI生成参数的方法
     * 设置API密钥、模型名称等参数
     *
     * @param userMsg 用户消息对象
     * @return GenerationParam 生成参数对象
     */
    private GenerationParam buildGenerationParam(Message userMsg) {
        return GenerationParam.builder()
                .apiKey(userSettingsService.getUserSettings().getDeepseekAPIKey())
                .model("deepseek-v3.2-exp")
                .incrementalOutput(true)
                .resultFormat("message")
                .messages(List.of(userMsg))
                .build();
    }

    /**
     * 流式调用AI生成接口的方法
     * 发起流式请求并返回完整的AI回复
     *
     * @param gen Generation对象，用于发起AI生成请求
     * @param userMsg Message对象，包含用户消息
     * @return String AI的完整回复内容
     * @throws NoApiKeyException 当没有提供API密钥时抛出
     * @throws ApiException 当API调用出现错误时抛出
     * @throws InputRequiredException 当输入参数缺失时抛出
     */
    public String streamCallWithMessage(Generation gen, Message userMsg)
            throws NoApiKeyException, ApiException, InputRequiredException {
        GenerationParam param = buildGenerationParam(userMsg);
        Flowable<GenerationResult> result = gen.streamCall(param);

        StringBuilder fullResponse = new StringBuilder();
        result.blockingForEach(generationResult -> {
            String content = generationResult.getOutput().getChoices().get(0).getMessage().getContent();
            if (content != null && !content.isEmpty()) {
                fullResponse.append(content);
                System.out.print(content);
            }
        });

        return fullResponse.toString();
    }

    /**
     * 调用AI分析内容
     *
     * @param prompt 要分析的内容提示
     * @return String AI的完整回复内容
     */
    public String analyzeContent(String prompt) {
        try {
            Generation gen = new Generation();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(prompt)
                    .build();
            return streamCallWithMessage(gen, userMsg);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            System.err.println("AI分析过程中发生异常: " + e.getMessage());
            return "AI分析失败";
        }
    }
}