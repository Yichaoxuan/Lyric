package com.lyric.lyric.Service.contentAnalysis;

import com.lyric.lyric.Mapper.content.DiaryMapper;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import org.springframework.stereotype.Service;

@Service
public class AIAnalysisService {

    private final UserSettingsService userSettingsService;

    private final CallAiAnalysis callAiAnalysis;

    private final DiaryMapper diaryMapper;

    private String rules = "请严格按照以下规则分析日记内容，并返回指定JSON格式的结果：\n" +
            "1. **总结描述**：\n" +
            "   - 添加一个\"summary\"字段，从日记作者的视角（第一人称）简要总结日记内容，突出核心事件和情感。总结应简洁，不超过100字。\n" +
            "2. **标签分类**：\n" +
            "   - 主题标签：描述日记核心主题，如学习、工作、情感、家庭、娱乐、健康、旅行、思考等。限制1-3个，优先使用常见主题词。\n" +
            "   - 情感标签：描述日记中表达的情感，如开心、难过、平静、生气、焦虑、感动、疲惫、思考等。限制1-3个，优先使用常见情感词。\n" +
            "   - 实体标签：描述日记中提到的具体实体，如人物、地点、事件等。数量不限，但每个实体必须有定义。\n" +
            "\uFEFF\n" +
            "3. **标签命名规则**：\n" +
            "   - 所有标签名称必须简短、明确、合理，使用中文词语（不超过4个汉字）。\n" +
            "   - 如果现有标签不匹配，可创建新标签，但需基于日记内容（如“暗恋”“校园生活”），避免主观臆断。\n" +
            "   - 主题和情感标签优先从常见词中选择，实体标签以日记中具体名称为准。\n" +
            "\uFEFF\n" +
            "4. **实体定义规则**：\n" +
            "   - 仅实体标签包含“定义”字段，是一个字符串数组。\n" +
            "   - 定义数组根据实体类型动态包含以下元素（如果适用）：\n" +
            "     - \"关系: [描述与日记作者的关系，如同学、老师、陌生人等]\"\n" +
            "     - \"地点: [描述相关地点，如学校、家等]\"\n" +
            "     - \"事件: [描述发生的事件，如考试、分手等]\"\n" +
            "     - \"日期: [描述事件发生的日期，基于日记内容。使用日记开头日期作为基准推断所有时间。规则：如果是日记当天发生或提及的事件（包括计划、回忆或当前行动），日期设置为日记当天（使用YYYY-MM-DD格式）；如果是回忆过去事件，使用推测的具体日期（例如，日记日期为2025-11-20，则'昨天'为2025-11-19，'上星期'为2025-11-13左右）。对于计划中的未来事件，但提及发生在日记当天，日期设置为日记当天，而不是计划执行日期。]\"\n" +
            "   - 只包含与实体直接相关的信息（例如，人物实体通常包含关系、事件和日期；地点实体通常包含地点、事件和日期）。\n" +
            "\uFEFF\n" +
            "5. **分析步骤**：\n" +
            "   - 首先，编写总结描述，从作者视角概括日记核心内容和情感。\n" +
            "   - 然后，识别日记核心主题和情感基调，选择1-3个主题标签和1-3个情感标签。\n" +
            "   - 接着，提取所有关键实体（人物、地点、事件），为每个实体创建标签并填写定义，包括日期信息。日期推断基于日记开头日期：所有在日记当天提及的事件（包括计划、回忆或当前行动）都使用日记当天日期；如果提到相对时间（如'昨天'、'上星期'），使用基准日期推算具体日期。优先使用YYYY-MM-DD格式。\n" +
            "   - 确保标签准确反映日记内容，避免冗余或无关标签。\n" +
            "\uFEFF\n" +
            "6. **返回格式**：\n" +
            "   - 必须返回纯JSON对象，无额外文本。\n" +
            "   - JSON结构：{\n" +
            "        \"summary\": \"总结描述\",\n" +
            "        \"labels\": [\n" +
            "            {\"标签名字\": \"标签名称\", \"标签类型\": \"主题\"},\n" +
            "            {\"标签名字\": \"标签名称\", \"标签类型\": \"情感\"},\n" +
            "            {\"标签名字\": \"实体名称\", \"标签类型\": \"实体\", \"定义\": [\"关系: ...\", \"地点: ...\", \"事件: ...\", \"日期: ...\"]}\n" +
            "        ]\n" +
            "     }\n" +
            "   - 标签顺序：先主题标签，再情感标签，最后实体标签（按在日记中出现的顺序或重要性排列）。\n" +
            "\uFEFF\n" +
            "注意：所有分析必须基于日记文本，不得添加外部知识。如果日记内容模糊，优先选择最明显的标签。日期推断必须使用日记开头日期作为基准。";


    public AIAnalysisService(UserSettingsService userSettingsService, CallAiAnalysis callAiAnalysis, DiaryMapper diaryMapper) {
        this.userSettingsService = userSettingsService;
        this.callAiAnalysis = callAiAnalysis;
        this.diaryMapper = diaryMapper;
    }

    public void tagAnalysis(Integer diaryId) {
       tagAnalysis(diaryId, diaryMapper.selectById(diaryId).getContent());
    }

    public void tagAnalysis(Integer diaryId, String content) {
        String message = content + "\n" + rules;
        String result = callAiAnalysis.analyzeContent(message);
        System.out.println(result);
    }
}
