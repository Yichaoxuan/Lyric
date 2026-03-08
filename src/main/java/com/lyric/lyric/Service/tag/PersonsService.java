package com.lyric.lyric.Service.tag;

import com.lyric.lyric.Mapper.relation.DiaryPersonMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
public class PersonsService {

    private final PersonMapper personMapper;
    private final AIAnalysisService aiAnalysisService;
    private final DiaryPersonMapper diaryPersonMapper;

    public PersonsService(PersonMapper personMapper, AIAnalysisService aiAnalysisService, DiaryPersonMapper diaryPersonMapper) {
        this.personMapper = personMapper;
        this.aiAnalysisService = aiAnalysisService;
        this.diaryPersonMapper = diaryPersonMapper;
    }

    /**
     * 人物去重处理器
     * 通过多级匹配策略判断新出现的人物是否与已有人员重复,并更新或添加数据库
     * 匹配顺序：性别匹配 -> 名称匹配 -> 关系匹配 -> AI匹配
     *
     * @param diaryId 日记ID
     * @param newPersonInfoHashMap 包含新人物的名称和信息
     * @return 保存了新人物的在数据库中的主键ID与索引的Map
     */
    @Transactional
    public Map<Integer, Integer> personDeduplication(Integer diaryId,
                                                     Map<String, AITagJson.PersonInfo> newPersonInfoHashMap) {

        Map<Integer, Integer> resultMap = new HashMap<>();

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
                // 1. 按性别筛选候选人物
                List<PersonPojo> genderCandidates = findByGender(newPersonInfo.getGender());
                logMatchingProcess(newPersonName, genderCandidates, "性别匹配");

                if (genderCandidates.isEmpty()) {
                    // 新人物
                    addNewPerson(resultMap, diaryId, newPersonName, newPersonInfo);
                    continue;
                }

                // 2. 按名称匹配
                log.info("开始二级匹配：名称与别名：{}", newPersonName);
                List<PersonPojo> nameCandidates = findExactMatch(newPersonName, genderCandidates);
                logMatchingProcess(newPersonName, nameCandidates, "名称匹配");

                if (nameCandidates.isEmpty()) {
                    // 新人物
                    addNewPerson(resultMap, diaryId, newPersonName, newPersonInfo);
                    continue;
                }

                // 3. 按关系匹配
                log.info("开始三级匹配：关系：{}", newPersonInfo.getRelationship());
                List<PersonPojo> relationCandidates = findByNameAndRelation(
                        newPersonInfo.getRelationship(), nameCandidates);
                logMatchingProcess(newPersonName, relationCandidates, "关系匹配");

                // 处理匹配结果
                handleMatchResult(resultMap, diaryId, newPersonName, newPersonInfo, relationCandidates);

            } catch (Exception e) {
                log.error("处理人物去重时发生异常，人物名称: {}", newPersonName, e);
                // 异常情况下，按新人物处理，避免丢失数据
                addNewPerson(resultMap, diaryId, newPersonName, newPersonInfo);
            }
        }

        return resultMap;
    }

    /**
     * 根据性别查找人物
     *
     * @param gender 性别
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
     * @param name 人物名称
     * @param candidatePersons 候选人物列表
     * @return 匹配成功的人物列表
     */
    private List<PersonPojo> findExactMatch(String name, List<PersonPojo> candidatePersons) {
        if (name == null || candidatePersons == null || candidatePersons.isEmpty()) {
            return Collections.emptyList();
        }

        return candidatePersons.stream()
                .filter(candidate -> {
                    // 1. 直接名称匹配
                    if (name.equals(candidate.getName())) {
                        return true;
                    }

                    // 2. 别名匹配
                    if (candidate.getAlias() != null) {
                        List<String> aliases = stringToList(candidate.getAlias());
                        return aliases.stream()
                                .anyMatch(alias -> alias.equals(name) || alias.contains(name));
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据关系匹配人物
     * 在候选人物列表中筛选出指定关系相匹配的人物
     *
     * @param relation 与用户的关系
     * @param candidatePersons 候选人物列表
     * @return 匹配到的人物列表
     */
    private List<PersonPojo> findByNameAndRelation(String relation, List<PersonPojo> candidatePersons) {
        if (relation == null || candidatePersons == null || candidatePersons.isEmpty()) {
            return Collections.emptyList();
        }

        return candidatePersons.stream()
                .filter(candidate -> {
                    if (candidate.getRelation() == null) {
                        return false;
                    }

                    List<String> relations = stringToList(candidate.getRelation());
                    return relations.stream()
                            .anyMatch(relationName -> relationName.equals(relation) || relationName.contains(relation));
                })
                .collect(Collectors.toList());
    }

    /**
     * 处理匹配结果
     */
    private void handleMatchResult(Map<Integer, Integer> resultMap, Integer diaryId,
                                   String newName, AITagJson.PersonInfo newInfo,
                                   List<PersonPojo> candidates) {

        if (candidates.isEmpty()) {
            log.info("无匹配人物，判定为新人物: {}", newName);
            addNewPerson(resultMap, diaryId, newName, newInfo);
            return;
        }

        if (candidates.size() == 1) {
            PersonPojo matchedPerson = candidates.getFirst();
            log.info("候选列表只剩下一人，判定为同一人，开始更新数据库: {}", matchedPerson.getName());
            updateAndReturn(resultMap, diaryId, newName, newInfo, matchedPerson);
            return;
        }

        // 多候选，使用AI匹配
        handleAiMatching(resultMap, diaryId, newName, newInfo, candidates);
    }

    /**
     * AI匹配处理
     */
    private void handleAiMatching(Map<Integer, Integer> resultMap, Integer diaryId,
                                  String newName, AITagJson.PersonInfo newInfo,
                                  List<PersonPojo> candidates) {

        log.info("开始四级匹配：AI 姓名：{}", newName);
        Integer aiMatchIndex = aiAnalysisService.personTagDeduplicationAnalysis(
                newName, newInfo, candidates);

        if (aiMatchIndex == null || aiMatchIndex < 0 || aiMatchIndex >= candidates.size()) {
            log.warn("AI匹配失败或未启用，使用保守策略: 视为新人物");
            // 保守策略：视为新人物，避免错误合并
            addNewPerson(resultMap, diaryId, newName, newInfo);
            return;
        }

        // AI匹配成功
        PersonPojo matchedPerson = candidates.get(aiMatchIndex);
        log.info("AI匹配成功，匹配到人物: {}", matchedPerson.getName());
        updateAndReturn(resultMap, diaryId, newName, newInfo, matchedPerson);
    }

    /**
     * 更新人物信息并返回结果
     */
    private void updateAndReturn(Map<Integer, Integer> resultMap, Integer diaryId,
                                 String newName, AITagJson.PersonInfo newInfo,
                                 PersonPojo existingPerson) {

        personUpdater(existingPerson, newName, newInfo);
        createDiaryPersonRelation(diaryId, existingPerson.getId(), newInfo);

        int index = newInfo.getIndex() != null ? Integer.parseInt(newInfo.getIndex()) : 0;
        resultMap.put(existingPerson.getId(), index);
    }

    /**
     * 添加新人物
     */
    private void addNewPerson(Map<Integer, Integer> resultMap, Integer diaryId,
                              String name, AITagJson.PersonInfo info) {

        log.info("判定为新人物，添加数据库: {}", name);
        PersonPojo person = new PersonPojo(name, info);
        personMapper.insert(person);

        createDiaryPersonRelation(diaryId, person.getId(), info);

        int index = info.getIndex() != null ? Integer.parseInt(info.getIndex()) : 0;
        resultMap.put(person.getId(), index);
    }

    /**
     * 人物更新处理器
     * 更新人物信息，并将更新后的人物信息保存到数据库中
     *
     * @param person 待更新人物
     * @param newPersonName 新人物名称
     * @param newPersonInfo 新人物信息
     */
    private void personUpdater(PersonPojo person, String newPersonName, AITagJson.PersonInfo newPersonInfo) {

        // 更新别名
        updateField(person::getAlias, person::setAlias,
                newPersonName, "别名", false);

        // 更新关系
        updateField(person::getRelation, person::setRelation,
                newPersonInfo.getRelationship(), "关系", true);

        // 更新性格
        updateField(person::getPersonality, person::setPersonality,
                newPersonInfo.getPersonality(), "性格", true);

        // 更新出现次数
        int currentAppearanceCount = person.getAppearanceCount() != null
                ? person.getAppearanceCount() : 0;
        person.setAppearanceCount(currentAppearanceCount + 1);

        // 更新人物信息
        personMapper.update(person);
    }

    /**
     * 通用字段更新方法
     */
    private void updateField(java.util.function.Supplier<String> getter,
                             java.util.function.Consumer<String> setter,
                             String newValue, String fieldName,
                             boolean allowEmpty) {

        if (newValue == null || (!allowEmpty && newValue.trim().isEmpty())) {
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
     * 创建日记-人物关联关系
     */
    private void createDiaryPersonRelation(Integer diaryId, Integer personId,
                                           AITagJson.PersonInfo personInfo) {

        try {
            DiaryPersonPojo.MentionType mentionType = personInfo.getMentionType();

            if (diaryPersonMapper.selectByDiaryIdAndPersonId(diaryId, personId) == null) {
                diaryPersonMapper.insert(new DiaryPersonPojo(diaryId, personId,
                        mentionType));
                log.debug("创建日记-人物关联: diaryId={}, personId={}", diaryId, personId);
            }
        } catch (Exception e) {
            log.error("创建日记-人物关联失败，diaryId={}, personId={}", diaryId, personId, e);
        }
    }

    /**
     * 记录匹配过程日志
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