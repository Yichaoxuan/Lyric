package com.lyric.lyric.Service.tag.parsing;

import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lyric.lyric.Utils.stringProcessing.stringUtils.listToString;
import static com.lyric.lyric.Utils.stringProcessing.stringUtils.stringToList;

/**
 * 人物标签处理服务类
 *
 * @author Yichaoxuan
 * @since 2026-02-08
 */
@Slf4j
@Service
public class PersonsParsingService {

    private final PersonMapper personMapper;
    private final AIAnalysisService aiAnalysisService;

    public PersonsParsingService(PersonMapper personMapper, AIAnalysisService aiAnalysisService) {
        this.personMapper = personMapper;
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * 人物去重处理器
     * 通过多级匹配策略判断新出现的人物是否与已有人员重复，并更新或添加数据库
     * 匹配顺序：性别匹配 -> 名称匹配 -> 关系匹配 -> AI 匹配
     *
     * @param newPersonInfoHashMap 包含新人物的名称和信息，键为人物名称，值为人物详细信息
     *                             （包括关系、性别、性格、提及类型、颜色代码、索引等）
     * @return 保存了新人物的在数据库中的主键 ID 与索引的 Map，键为数据库中的主键 ID，值为原始索引
     *         用于后续处理时保持人物顺序
     */
    @Transactional
    public Map<Integer, Integer> personDeduplication(Map<String, AITagJson.PersonInfo> newPersonInfoHashMap) {

        Map<Integer, Integer> personIdIndexMap = new HashMap<>();

        for (Map.Entry<String, AITagJson.PersonInfo> entry : newPersonInfoHashMap.entrySet()) {
            String newPersonName = entry.getKey();

            AITagJson.PersonInfo newPersonInfo = entry.getValue();

            if (newPersonInfo == null) {
                log.warn("人物信息为空，跳过处理: {}", newPersonName);
                continue;
            }
            logMatchingProcess(newPersonName, null, "开始处理");
            log.info("开始一级匹配：性别：{}", newPersonInfo.getGender());
            try {

                //  按性别筛选候选人物
                List<PersonPojo> genderCandidates = findByGender(newPersonInfo.getGender());
                logMatchingProcess(newPersonName, genderCandidates, "性别匹配");

                if (genderCandidates.isEmpty()) {
                    // 新人物
                    addNewPerson(personIdIndexMap, newPersonName, newPersonInfo);
                    continue;
                }

                //  按名称匹配（名称/别名匹配到即合并，关系用于累加而非过滤）
                log.info("开始二级匹配：名称与别名：{}", newPersonName);
                List<PersonPojo> nameCandidates = findExactMatch(newPersonName, genderCandidates);
                logMatchingProcess(newPersonName, nameCandidates, "名称匹配");

                if (nameCandidates.isEmpty()) {
                    addNewPerson(personIdIndexMap, newPersonName, newPersonInfo);
                    continue;
                }

                if (nameCandidates.size() == 1) {
                    PersonPojo matchedPerson = nameCandidates.getFirst();
                    log.info("名称/别名匹配到唯一人物，判定为同一人，开始更新数据库: {}", matchedPerson.getName());
                    updateAndReturn(personIdIndexMap, newPersonName, newPersonInfo, matchedPerson);
                    continue;
                }

                handleAiMatching(personIdIndexMap, newPersonName, newPersonInfo, nameCandidates);
            } catch (Exception e) {
                log.error("处理人物去重时发生异常，人物名称: {}", newPersonName, e);
                // 异常情况下，按新人物处理，避免丢失数据
                addNewPerson(personIdIndexMap, newPersonName, newPersonInfo);
            }
        }

        return personIdIndexMap;
    }

    /**
     * 根据性别查找人物
     * 从数据库中查询指定性别的所有人物，作为初步筛选的候选集
     *
     * <p>
     * 说明:
     * </p>
     * <ul>
     * <li>如果性别参数为 null，直接返回空列表</li>
     * <li>将性别字符串转换为数据库编码（男=1, 女=2, 未知=0）</li>
     * <li>通过 Mapper 层查询数据库</li>
     * <li>如果查询结果为 null，转换为空列表避免空指针异常</li>
     * </ul>
     *
     * @param gender 性别字符串，可选值："男"、"女" 或其他（将转换为 0）
     * @return 符合性别条件的候选人物列表，如果没有匹配项则返回空列表
     */
    private List<PersonPojo> findByGender(String gender) {
        if (gender == null) {
            return Collections.emptyList();
        }

        List<PersonPojo> candidatePersons = personMapper.selectByGender(PersonPojo.genderName(gender));
        return candidatePersons != null ? candidatePersons : Collections.emptyList();
    }

    /**
     * 根据名称精确匹配人物
     * 在候选人物列表中查找与给定名称匹配的人物
     *
     * <p>
     * 匹配规则:
     * </p>
     * <ol>
     * <li>直接名称匹配：候选人的 name 字段与给定名称完全相同</li>
     * <li>包含匹配：候选人的 name 字段与给定名称存在包含关系</li>
     * <li>别名匹配：候选人的 alias 字段（逗号分隔的字符串）中包含给定名称</li>
     * <ul>
     * <li>支持完全匹配（alias.equals(name)）</li>
     * <li>支持包含匹配（alias.contains(name)）</li>
     * </ul>
     * </ol>
     *
     * @param name             待匹配的人物名称
     * @param candidatePersons 候选人物列表，通常是从性别匹配阶段过滤后的结果
     * @return 匹配成功的人物列表，可能为空
     */
    private List<PersonPojo> findExactMatch(String name, List<PersonPojo> candidatePersons) {
        if (name == null || candidatePersons == null || candidatePersons.isEmpty()) {
            return Collections.emptyList();
        }

        return candidatePersons.stream()
                .filter(candidate -> {
                    String candidateName = candidate.getName();

                    if (name.equals(candidateName)) {
                        return true;
                    }

                    if (candidateName != null && (name.contains(candidateName) || candidateName.contains(name))) {
                        return true;
                    }

                    if (candidate.getAlias() != null) {
                        List<String> aliases = stringToList(candidate.getAlias());
                        return aliases.stream()
                                .anyMatch(alias -> alias.equals(name)
                                        || alias.contains(name)
                                        || name.contains(alias));
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 查找名称完全匹配的人物（用于优先匹配）
     * 当名称完全相同时，应优先合并，不应因关系不同而判定为新人物
     *
     * @param name             待匹配的人物名称
     * @param candidatePersons 候选人物列表
     * @return 名称完全匹配的人物列表，如果没有则返回空列表
     */
    private List<PersonPojo> findPerfectNameMatch(String name, List<PersonPojo> candidatePersons) {
        if (name == null || candidatePersons == null || candidatePersons.isEmpty()) {
            return Collections.emptyList();
        }

        return candidatePersons.stream()
                .filter(candidate -> name.equals(candidate.getName()))
                .collect(Collectors.toList());
    }

    /**
     * 根据关系匹配人物
     * 在候选人物列表中筛选出同指定关系相匹配的人物
     *
     * <p>
     * 匹配规则:
     * </p>
     * <ol>
     * <li>提取候选人的 relation 字段（逗号分隔的关系字符串）</li>
     * <li>检查给定关系是否存在于关系列表中</li>
     * <ul>
     * <li>支持完全匹配（relationName.equals(relation)）</li>
     * <li>支持包含匹配（relationName.contains(relation)）</li>
     * </ul>
     * </ol>
     *
     * <p>
     * 示例:
     * </p>
     * <ul>
     * <li>如果候选人的关系为 "朋友，同学"，给定关系为 "朋友"，则匹配成功</li>
     * <li>如果候选人的关系为 "好朋友"，给定关系为 "朋友"，也会匹配成功（包含匹配）</li>
     * </ul>
     *
     * <p>
     * 注意:
     * </p>
     * <ul>
     * <li>如果关系参数或候选列表为 null，直接返回空列表</li>
     * <li>候选人的 relation 字段为 null 时，该候选人不会被匹配</li>
     * </ul>
     *
     * @param relation         与用户的关系，如 "朋友"、"家人"、"同事" 等
     * @param candidatePersons 候选人物列表，通常是从名称匹配阶段过滤后的结果
     * @return 匹配到的人物列表，可能为空
     */
    private List<PersonPojo> findByNameAndRelation(String relation, List<PersonPojo> candidatePersons) {
        if (relation == null || candidatePersons == null || candidatePersons.isEmpty()) {
            return candidatePersons;
        }

        return candidatePersons.stream()
                .filter(candidate -> {
                    if (candidate.getRelation() == null) {
                        return true;
                    }

                    List<String> relations = stringToList(candidate.getRelation());
                    return relations.stream()
                            .anyMatch(relationName -> relationName.equals(relation)
                                    || relationName.contains(relation)
                                    || relation.contains(relationName));
                })
                .collect(Collectors.toList());
    }

    /**
     * 处理匹配结果
     * 根据候选列表的大小决定下一步操作：视为新人物、直接更新或使用 AI 匹配
     *
     * <p>
     * 处理逻辑:
     * </p>
     * <ol>
     * <li>候选列表为空：视为新人物，调用 addNewPerson 添加到数据库</li>
     * <li>候选列表只有 1 人：判定为同一人，直接调用 updateAndReturn 更新人物信息</li>
     * <li>候选列表有多人：启用 AI 匹配，调用 handleAiMatching 进行智能判断</li>
     * </ol>
     *
     * <p>
     * 设计意图:
     * </p>
     * <ul>
     * <li>单候选直接合并，减少不必要的 AI 调用，提高效率</li>
     * <li>多候选时使用 AI 判断，确保匹配的准确性</li>
     * <li>无候选时保守处理，避免错误合并导致数据混乱</li>
     * </ul>
     *
     * @param resultMap  结果映射表，用于存储人物 ID 与索引的对应关系
     * @param newName    新人物名称
     * @param newInfo    新人物详细信息
     * @param candidates 经过三级匹配后的候选人物列表
     */
    private void handleMatchResult(Map<Integer, Integer> resultMap, String newName, AITagJson.PersonInfo newInfo,
            List<PersonPojo> candidates) {

        if (candidates.isEmpty()) {
            log.info("无匹配人物，判定为新人物: {}", newName);
            addNewPerson(resultMap, newName, newInfo);
            return;
        }

        if (candidates.size() == 1) {
            PersonPojo matchedPerson = candidates.getFirst();
            log.info("候选列表只剩下一人，判定为同一人，开始更新数据库: {}", matchedPerson.getName());
            updateAndReturn(resultMap, newName, newInfo, matchedPerson);
            return;
        }

        // 多候选，使用AI匹配
        handleAiMatching(resultMap, newName, newInfo, candidates);
    }

    /**
     * AI 匹配处理
     * 当存在多个候选人物时，调用 AI 分析服务进行智能匹配
     *
     * <p>
     * 处理流程:
     * </p>
     * <ol>
     * <li>调用 AIAnalysisService.personTagDeduplicationAnalysis 方法进行 AI 分析</li>
     * <li>AI 返回最匹配的候选人在列表中的索引</li>
     * <li>验证 AI 返回的索引是否有效（不为 null 且在列表范围内）</li>
     * <li>如果 AI 匹配失败或未启用，采用保守策略：视为新人物</li>
     * <li>如果 AI 匹配成功，更新匹配人物的信息并建立关联</li>
     * </ol>
     *
     * <p>
     * AI 分析内容:
     * </p>
     * <ul>
     * <li>综合分析人物的名称、别名、关系、性格等多维度信息</li>
     * <li>基于语义理解判断新旧人物是否为同一人</li>
     * <li>返回匹配置信度最高的候选人索引</li>
     * </ul>
     *
     * <p>
     * 注意:
     * </p>
     * <ul>
     * <li>AI 匹配是最后一级匹配，仅当前面三级匹配都无法确定唯一人选时才调用</li>
     * <li>AI 匹配失败时，为避免错误合并，默认按新人物处理</li>
     * </ul>
     *
     * @param resultMap  结果映射表，用于存储人物 ID 与索引的对应关系
     * @param newName    新人物名称
     * @param newInfo    新人物详细信息
     * @param candidates 经过三级匹配后的候选人物列表（多人）
     */
    private void handleAiMatching(Map<Integer, Integer> resultMap, String newName, AITagJson.PersonInfo newInfo,
            List<PersonPojo> candidates) {

        log.info("开始四级匹配：AI 姓名：{}", newName);
        Integer aiMatchIndex = aiAnalysisService.personTagDeduplicationAnalysis(
                newName, newInfo, candidates);

        if (aiMatchIndex == null || aiMatchIndex < 0 || aiMatchIndex >= candidates.size()) {
            log.warn("AI匹配失败或未启用，使用保守策略: 视为新人物");
            // 保守策略：视为新人物，避免错误合并
            addNewPerson(resultMap, newName, newInfo);
            return;
        }

        // AI匹配成功
        PersonPojo matchedPerson = candidates.get(aiMatchIndex);
        log.info("AI匹配成功，匹配到人物: {}", matchedPerson.getName());
        updateAndReturn(resultMap, newName, newInfo, matchedPerson);
    }

    /**
     * 更新人物信息并返回结果
     * 更新现有人物的信息，创建日记与人物的关联，并将结果记录到映射表中
     *
     * @param resultMap      结果映射表，键为数据库中的人物 ID，值为原始索引
     * @param newName        新人物名称（可能成为现有人物的别名）
     * @param newInfo        新人物详细信息
     * @param existingPerson 已存在的人物对象，需要被更新
     */
    private void updateAndReturn(Map<Integer, Integer> resultMap,
            String newName, AITagJson.PersonInfo newInfo,
            PersonPojo existingPerson) {

        personUpdater(existingPerson, newName, newInfo);

        int index = newInfo.getIndex() != null ? Integer.parseInt(newInfo.getIndex()) : 0;
        resultMap.put(existingPerson.getId(), index);
    }

    /**
     * 添加新人物
     * 创建新的人物记录并保存到数据库，同时建立日记与人物的关联
     *
     * <p>
     * 处理步骤:
     * </p>
     * <ol>
     * <li>创建新的 PersonPojo 对象，设置初始信息</li>
     * <ul>
     * <li>名称：使用传入的名称</li>
     * <li>首次出现时间：当前时间</li>
     * <li>关系、性别、性格等：从 AI 分析结果中获取</li>
     * <li>出现次数：初始化为 1</li>
     * <li>重要性：初始化为 LOW</li>
     * </ul>
     * <li>调用 personMapper.insert 保存到数据库</li>
     * <li>调用 createDiaryPersonRelation 创建日记与人物的关联关系</li>
     * <li>将新人物 ID 和索引存入 resultMap</li>
     * </ol>
     *
     * <p>
     * 说明:
     * </p>
     * <ul>
     * <li>此方法仅在确认是新人物时调用（所有匹配级别都未找到匹配）</li>
     * <li>新人物的首次出现时间和最后出现时间相同</li>
     * <li>索引从新人物信息中提取，如果不存在则默认为 0</li>
     * </ul>
     *
     * @param resultMap 结果映射表，用于存储新人物 ID 与索引的对应关系
     * @param name      新人物名称
     * @param info      新人物详细信息
     */
    private void addNewPerson(Map<Integer, Integer> resultMap,
            String name, AITagJson.PersonInfo info) {

        log.info("判定为新人物，添加数据库: {}", name);
        PersonPojo person = new PersonPojo(name, info);
        personMapper.insert(person);

        int index = info.getIndex() != null ? Integer.parseInt(info.getIndex()) : 0;
        resultMap.put(person.getId(), index);
    }

    /**
     * 人物更新处理器
     * 更新人物信息，并将更新后的人物信息保存到数据库中
     *
     * <p>
     * 更新内容:
     * </p>
     * <ol>
     * <li>别名：将新名称添加到别名列表中（如果不存在），使用 listToString 序列化</li>
     * <li>关系：将新关系添加到关系列表中（如果不存在），允许多个关系并存</li>
     * <li>性格：将新性格描述添加到性格列表中（如果不存在）</li>
     * <li>最后出现时间：更新为当前时间，记录人物最新出现的时间点</li>
     * <li>出现次数：累加 1，统计人物在多少篇日记中出现过</li>
     * </ol>
     *
     * <p>
     * 更新策略:
     * </p>
     * <ul>
     * <li>使用 updateField 通用方法进行字段更新，避免重复代码</li>
     * <li>对于允许为空的字段（如别名），当新值为空时不更新</li>
     * <li>对于不允许为空的字段（如关系、性格），即使为空也会尝试更新</li>
     * <li>所有字段都支持多值存储，以逗号分隔的字符串形式保存</li>
     * </ul>
     *
     * <p>
     * 注意:
     * </p>
     * <ul>
     * <li>更新后会调用 personMapper.update 将变更持久化到数据库</li>
     * <li>日志记录使用 debug 级别，避免在生产环境产生过多日志</li>
     * </ul>
     *
     * @param person        待更新的人物对象
     * @param newPersonName 新人物名称（可能成为现有人物的别名）
     * @param newPersonInfo 新人物信息，包含关系、性格等详细数据
     */
    private void personUpdater(PersonPojo person, String newPersonName, AITagJson.PersonInfo newPersonInfo) {

        // 更新别名
        updateField(person::getAlias, person::setAlias,
                newPersonName, "别名");

        // 更新关系
        updateField(person::getRelation, person::setRelation,
                newPersonInfo.getRelationship(), "关系");

        // 更新性格
        updateField(person::getPersonality, person::setPersonality,
                newPersonInfo.getPersonality(), "性格");

        // 更新最后出现时间
        person.setLastAppearance(DateTimeUtils.now());

        // 更新出现次数
        int currentAppearanceCount = person.getAppearanceCount() != null
                ? person.getAppearanceCount()
                : 0;
        person.setAppearanceCount(currentAppearanceCount + 1);

        // 更新人物信息
        personMapper.update(person);
    }

    /**
     * 通用字段更新方法
     * 处理支持多值的字符串字段的更新逻辑
     *
     * <p>
     * 处理逻辑:
     * </p>
     * <ol>
     * <li>检查新值：如果为 null，则跳过更新</li>
     * <li>检查当前值：如果为 null，直接设置新值</li>
     * <li>如果当前值不为 null，将其解析为列表（使用 stringToList）</li>
     * <li>检查新值是否已存在于列表中</li>
     * <ul>
     * <li>如果不存在，添加到列表并使用 listToString 转回字符串</li>
     * <li>如果已存在，不做任何操作（避免重复）</li>
     * </ul>
     * </ol>
     *
     * <p>
     * 设计优势:
     * </p>
     * <ul>
     * <li>使用函数式接口（Supplier 和 Consumer），使方法具有通用性，可处理各种字段</li>
     * <li>统一的去重逻辑，避免相同值重复添加</li>
     * <li>支持多值存储，字段值以逗号分隔的字符串形式保存</li>
     * <li>灵活的空值处理策略，通过 allowEmpty 参数控制</li>
     * </ul>
     *
     * <p>
     * 使用示例:
     * </p>
     * 
     * <pre>{@code
     * // 更新别名
     * updateField(person::getAlias, person::setAlias, newPersonName, "别名", false);
     * }</pre>
     *
     * @param getter    取值函数，用于获取字段的当前值
     * @param setter    设值函数，用于设置字段的新值
     * @param newValue  新值，待添加到字段中的值
     * @param fieldName 字段名称，用于日志记录
     */
    private void updateField(java.util.function.Supplier<String> getter,
            java.util.function.Consumer<String> setter,
            String newValue, String fieldName) {

        if (newValue == null || (newValue.trim().isEmpty())) {
            return;
        }

        String currentValue = getter.get();
        if (currentValue == null) {
            setter.accept(newValue);
            log.debug("设置{}: {}", fieldName, newValue);
            return;
        }

        List<String> values = stringToList(currentValue);
        boolean exists = values.stream()
                .anyMatch(value -> value.equals(newValue));

        if (!exists) {
            values.add(newValue);
            setter.accept(listToString(values));
            log.debug("添加新{}: {}", fieldName, newValue);
        }
    }

    /**
     * 记录匹配过程日志
     * 在调试模式下输出每一级匹配的详细信息，便于追踪和排查问题
     *
     * <p>
     * 日志内容:
     * </p>
     * <ul>
     * <li>人物名称：正在处理的人物名称</li>
     * <li>匹配阶段：当前所处的匹配级别（性别匹配、名称匹配、关系匹配等）</li>
     * <li>候选人数量：通过当前匹配级别筛选后的候选人数</li>
     * <li>候选人列表：所有候选人的名称，逗号分隔；如果没有候选人则显示"无"</li>
     * </ul>
     *
     * <p>
     * 使用说明:
     * </p>
     * <ul>
     * <li>仅在 debug 级别日志开启时输出，避免生产环境性能损耗</li>
     * <li>如果候选列表为 null 或空，显示"无"</li>
     * <li>使用 Stream API 将候选人对象流转换为名称字符串</li>
     * </ul>
     *
     * <p>
     * 示例输出:
     * </p>
     * 
     * <pre>
     * 张三 - 性别匹配：候选人数量=3, 列表=[张三，李四，王五]
     * </pre>
     * 
     * <pre>
     * 李四 - 名称匹配：候选人数量=1, 列表=[李四]
     * </pre>
     * 
     * <pre>
     * 王五 - 关系匹配：候选人数量=0, 列表=[无]
     * </pre>
     *
     * @param name       人物名称
     * @param candidates 候选人物列表，可能为 null
     * @param stage      匹配阶段描述，如"开始处理"、"性别匹配"、"名称匹配"、"关系匹配"等
     */
    private void logMatchingProcess(String name, List<PersonPojo> candidates,
            String stage) {
        if (log.isDebugEnabled()) {
            int candidateCount = candidates != null ? candidates.size() : 0;
            String candidateNames = candidates != null && !candidates.isEmpty()
                    ? candidates.stream()
                            .map(PersonPojo::getName)
                            .collect(Collectors.joining(", "))
                    : "无";

            log.debug("{} - {}: 候选人数量={}, 列表=[{}]",
                    name, stage, candidateCount, candidateNames);
        }
    }
}