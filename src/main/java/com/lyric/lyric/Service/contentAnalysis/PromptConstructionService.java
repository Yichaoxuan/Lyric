package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.AI.EventDeduplicationData;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.message.MessageService;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.json.JsonConversionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.lyric.lyric.Utils.text.PlaceholderUtils.replacePlaceholder;

/**
 * 提示词构建类
 *
 * @author Yichaoxuan
 * @since 2026-03-16
 */
@Slf4j
@Service
public class PromptConstructionService {

    private final UserSettingsService userSettingsService;
    private final MessageService messageService;

    public PromptConstructionService(UserSettingsService userSettingsService, @Lazy MessageService messageService) {
        this.userSettingsService = userSettingsService;
        this.messageService = messageService;
    }

    /**
     * 日记分析提示词构建
     * 构建分析日记内容并生成标签的提示词
     * 
     * @param content 日记内容
     *
     * @return 提示词
     */
    public Prompt buildPrompt(String content) {
        // 构建用户提示词
        Message userMessage = new UserMessage(content);
        // 获取用户设置的分析规则,构建系统提示词
        String systemMessageGender =
                replacePlaceholder(userSettingsService.getLatestAnalysisRulesConfig(),
                        "基本信息",
                userSettingsService.getLatestUserInfoConfig().getUserInfoStr(true));
        Message systemMessage = new SystemMessage(systemMessageGender);
        return new Prompt(List.of(userMessage, systemMessage));
    }

    /**
     * 人物标签去重提示词构建
     * 
     * @param newPersonName    新人物名称
     * @param newPersonInfo    新人物信息
     * @param candidatePersons 候选人物列表
     *
     * @return 提示词
     */
    public Prompt buildPersonTagDeduplicationPrompt(String newPersonName, AITagJson.PersonInfo newPersonInfo,
            List<PersonPojo> candidatePersons) {
        // 构建用户提示词
        StringBuilder sb = new StringBuilder();
        sb.append("新人物信息：\n");
        sb.append("姓名：").append(newPersonName).append("\n");
        sb.append("性别：").append(newPersonInfo.getGender()).append("\n");
        sb.append("关系：").append(newPersonInfo.getRelationship()).append("\n");
        sb.append("性格：").append(newPersonInfo.getPersonality()).append("\n");

        sb.append("候选人列表：\n");
        for (PersonPojo candidatePerson : candidatePersons) {
            sb.append("索引：").append(candidatePersons.indexOf(candidatePerson) + 1).append("\n");
            sb.append("姓名：").append(candidatePerson.getName()).append("\n");
            sb.append("性别：").append(candidatePerson.getGender()).append("\n");
            sb.append("关系：").append(candidatePerson.getRelation()).append("\n");
            sb.append("性格：").append(candidatePerson.getPersonality()).append("\n");
        }
        Message userMessage = new UserMessage(sb.toString());

        // 获取用户设置的去重规则，构建系统提示词
        Message systemMessage = new SystemMessage(userSettingsService.getPersonTagDuplicationRules());

        return new Prompt(List.of(userMessage, systemMessage));
    }

    /**
     * 地点标签去重提示词构建
     * 
     * @param newLocationName    新地点名称
     * @param newLocationInfo    新地点信息
     * @param candidateLocations 候选地点列表
     *
     * @return 提示词
     */
    public Prompt buildLocationTagDeduplicationPrompt(String newLocationName, AITagJson.LocationInfo newLocationInfo,
            List<LocationPojo> candidateLocations) {
        // 构建用户提示词
        StringBuilder sb = new StringBuilder();
        sb.append("新地点信息：\n");
        sb.append("名称：").append(newLocationName).append("\n");
        sb.append("描述：").append(newLocationInfo.getDescription()).append("\n");

        sb.append("候选地点列表：\n");
        for (LocationPojo candidateLocation : candidateLocations) {
            sb.append("索引：").append(candidateLocations.indexOf(candidateLocation) + 1).append("\n");
            sb.append("名称：").append(candidateLocation.getName()).append("\n");
            sb.append("地点描述：").append(candidateLocation.getDescription()).append("\n");
        }
        Message userMessage = new UserMessage(sb.toString());

        // 获取用户设置的去重规则，构建系统提示词
        Message systemMessage = new SystemMessage(userSettingsService.getResponseMessageGenerationRules());

        return new Prompt(List.of(userMessage, systemMessage));
    }

    /**
     * 事件标签去重提示词构建
     *
     * @param eventDeduplicationData 事件去重数据
     *
     * @return 提示词
     */
    public Prompt buildEventDeduplicationPrompt(EventDeduplicationData eventDeduplicationData) {

        // 将 EventDeduplicationData 转换为格式化的 JSON 字符串
        String jsonData = JsonConversionUtils.toJson(eventDeduplicationData);
        
        // 格式化 JSON，使其具有良好的可读性
        String formattedJson = JsonConversionUtils.formatJson(jsonData);

        // 构建用户提示词
        Message userMessage = new UserMessage(formattedJson);

        // 获取用户设置的去重规则，构建系统提示词
        Message systemMessage = new SystemMessage(userSettingsService.getEventDuplicationRules());
        
        return new Prompt(List.of(userMessage, systemMessage));
    }

    /**
     * 响应消息生成提示词构建
     * 构建生成响应消息的提示词
     * 
     * @param newMessageConfigInstructions 新地响应消息配置指令
     *
     * @return 提示词
     */
    public Prompt buildResponseMessagePrompt(String newMessageConfigInstructions) {
        // 获取用户设置的响应消息生成规则，构建系统提示词
        String systemMessageContent = userSettingsService.getResponseMessageGenerationRules();

        // 替换角色描述占位符
        systemMessageContent = replacePlaceholder(systemMessageContent, "角色描述", newMessageConfigInstructions);
        
        // 获取 YAML 配置文件内容作为模板发送给 AI
        String yamlConfigContent = messageService.getYamlConfigContent();
        systemMessageContent = replacePlaceholder(systemMessageContent, "当前响应消息配置", yamlConfigContent);

        Message systemMessage = new SystemMessage(systemMessageContent);

        return new Prompt(List.of(systemMessage));
    }
}
