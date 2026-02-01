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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.lyric.lyric.Utils.stringProcessing.stringUtils.listToString;
import static com.lyric.lyric.Utils.stringProcessing.stringUtils.stringToList;

/**
 * 人物标签处理服务类
 *
 * @author Yichaoxuan
 * @since 2026-01-30
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
     * @param newPersonName 新人物名称
     * @param newPersonInfo 新人物信息
     */
    public void personDeduplicator(Integer diaryId, String newPersonName, AITagJson.PersonInfo newPersonInfo) {

        //一级匹配 匹配性别
        log.info("开始一级匹配：性别：{}", newPersonInfo.getGender());
        List<PersonPojo> candidatePersons = findByGender(newPersonInfo.getGender());
        log.info("一级匹配结束，候选人物列表：{}", candidatePersons);
        if (candidatePersons != null && !candidatePersons.isEmpty()) {

            //二级匹配 匹配名字和别称
            log.info("开始二级匹配：名称与别名：{}", newPersonName);
            candidatePersons = findExactMatch(newPersonName, candidatePersons);
            log.info("二级匹配结束，候选人物列表：{}", candidatePersons);
            if (!candidatePersons.isEmpty()) {

                //三级匹配 匹配关系
                log.info("开始三级匹配：关系：{}", newPersonInfo.getRelationship());
                candidatePersons = findByNameAndRelation(newPersonInfo.getRelationship(), candidatePersons);
                log.info("三级匹配结束，候选人物列表：{}", candidatePersons);
                if (!candidatePersons.isEmpty()) {

                    // 如果只剩下一个候选人物，则更新数据库
                    if (candidatePersons.size() == 1) {
                        log.info("候选列表只剩下一人，判定为同一人，开始更新数据库：{}", candidatePersons.getFirst());
                        // 为同一人物,更新人物信息
                        personUpdater(candidatePersons.getFirst(), newPersonName, newPersonInfo);
                        // 添加日记-人物关联
                        checkConstraint(diaryId, candidatePersons.getFirst().getId(), DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
                        return;
                    }

                    // 如果候选列表大于1，则进行四级匹配
                    // 四级匹配 AI匹配
                    log.info("开始四级匹配：AI 姓名：{}", newPersonName);
                    Integer matchTheCharacterIndex = aiAnalysisService.personTagDeduplicationAnalysis(newPersonName, newPersonInfo, candidatePersons);
                    if(matchTheCharacterIndex == -1) {
                        log.info("未开启AI分析功能,全部添加数据库");
                        for (PersonPojo person : candidatePersons) {
                            personMapper.insert(person);
                        }
                    }
                    if (matchTheCharacterIndex == 0 ) {
                        // 判断为新人物
                        PersonPojo person = new PersonPojo(newPersonName, newPersonInfo);
                        log.info("AI评定为新人物，添加数据库：{}", person);

                        Integer personId = personMapper.insert(person);

                        // 添加日记-人物关联
                        checkConstraint(diaryId, personId, DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
                    }

                    // 为同一人物,更新人物信息
                    PersonPojo personPojo = candidatePersons.get(matchTheCharacterIndex);
                    personUpdater(personPojo, newPersonName, newPersonInfo);
                    // 添加日记-人物关联
                    checkConstraint(diaryId, personPojo.getId(), DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
                }
            }
        }
        // 判断为新人物
        PersonPojo person = new PersonPojo(newPersonName, newPersonInfo);
        log.info("判定为新人物,添加数据库：{}", person);
        personMapper.insert(person);
        Integer personId = person.getId();

        // 添加日记-人物关联
        checkConstraint(diaryId, personId, DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
    }

    /**
     * 根据性别查找人物
     *
     * @param gender 性别
     * @return List<PersonPojo> 符合性别条件的候选人物列表，如果没有匹配项则返回null
     */
    private List<PersonPojo> findByGender(String gender) {
        List<PersonPojo> candidatePersons = personMapper.selectByGender(PersonPojo.genderName(gender));
        //判断是否为空
        if (!candidatePersons.isEmpty()) {
            return candidatePersons;
        }
        return null;
    }

    /**
     * 根据名称精确匹配人物
     * 在候选人物列表中查找与给定名称匹配的人物
     *
     * @param name 人物名称
     * @param candidatePersons 候选人物列表
     * @return List<PersonPojo> 匹配成功的人物列表
     */
    private List<PersonPojo> findExactMatch(String name, List<PersonPojo> candidatePersons) {

        //定义一个空列表，记录匹配成功的人物
        List<PersonPojo> newCandidatePersons = new ArrayList<>();

        for (PersonPojo candidatePerson : candidatePersons) {
            //查询该具有相同名字的人物,判断是否存在相同名字
            if (candidatePerson.getName().equals(name)) {
                newCandidatePersons.add(candidatePerson);
                continue;  //跳过当前循环，继续下一次循环
            }

            //查询该名称是否与别称相同或别称包含该名称
            if (candidatePerson.getAlias() != null) {
                List<String> strings = stringToList(candidatePerson.getAlias());
                boolean exists = false;
                for (String alias : strings) {
                    if (alias.equals(name)) {
                        exists = true;
                        break;
                    } else {
                        if (alias.contains(name)) {
                            exists = true;
                            break;
                        }
                    }
                }

                if (exists) {
                    newCandidatePersons.add(candidatePerson);
                }
            }
        }

        //返回候选人物列表
        return newCandidatePersons;
    }

    /**
     * 根据关系匹配人物
     * 在候选人物列表中筛选出指定关系相匹配的人物
     *
     * @param relation 与用户的关系
     * @param candidatePersons 候选人物列表
     * @return List<PersonPojo> 匹配到的人物列表
     */
    private List<PersonPojo> findByNameAndRelation(String relation, List<PersonPojo> candidatePersons) {
        // 定义一个空列表，记录候选人物
        List<PersonPojo> newCandidatePersons = new ArrayList<>();

        // 查询该具有相同关系的人物
        for (PersonPojo candidatePerson : candidatePersons) {
            // 判断关系是否相同
            String candidateRelations = candidatePerson.getRelation();
            if (candidateRelations != null) {
                List<String> relations = stringToList(candidateRelations);
                boolean exists = false;
                for (String relationName : relations) {
                    if (relationName.equals(relation)) {
                        exists = true;
                        break;
                    } else {
                        if (relationName.contains(relation)) {
                            exists = true;
                            break;
                        }
                    }
                }

                if (exists) {
                    newCandidatePersons.add(candidatePerson);
                }
            }
        }
        // 返回候选人物列表
        return newCandidatePersons;
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

        // 添加新的人物别名
        if (person.getAlias() == null) {
            person.setAlias(newPersonName); // 如果别名为空,添加别名
        } else {
            List<String> alias = stringToList(person.getAlias());
            boolean exists = false;
            // 判断是否已存在相同别名
            for (String aliasName : alias) {
                if (aliasName.equals(newPersonName)) {
                    exists = true;
                    break;  // 如果已存在相同别名,则跳出循环
                }
            }

            if (!exists) {
                alias.add(newPersonName);  // 添加新的别名
            }

            person.setAlias(listToString(alias));
        }


        // 添加新关系
        if (person.getRelation() == null) {
            person.setRelation(newPersonInfo.getRelationship());  // 如果关系为空,添加关系
        } else {
            List<String> relations = stringToList(person.getRelation());
            boolean exists = false;
            // 判断关系是否已存在相同关系
            for (String relationName : relations) {
                if (relationName.equals(newPersonInfo.getRelationship())) {
                    exists = true;
                    break;  // 如果已存在相同关系,则跳出循环
                }
            }

            if (!exists) {
                relations.add(newPersonInfo.getRelationship());  // 添加新的关系
            }

            person.setRelation(listToString(relations));
        }

        // 添加新性格
        if (person.getPersonality() == null) {
            person.setPersonality(newPersonInfo.getPersonality());  // 如果性格为空,添加性格
        } else {
            List<String> personalities = stringToList(person.getPersonality());
            boolean exists = false;
            // 判断性格是否已存在相同性格
            for (String personalityName : personalities) {
                if (personalityName.equals(newPersonInfo.getPersonality())) {
                    exists = true;
                    break;  // 如果已存在相同性格,则跳出循环
                }
            }

            if (!exists) {
                personalities.add(newPersonInfo.getPersonality());  // 添加新的性格
            }

            person.setPersonality(listToString(personalities));
        }

        // 更新出现次数
        Integer currentAppearanceCount = person.getAppearanceCount();
        if (currentAppearanceCount == null) {
            currentAppearanceCount = 0;
        }
        person.setAppearanceCount(currentAppearanceCount + 1);

        // 更新人物信息
        personMapper.update(person);
    }

    /**
     * 检查是否已存在关联，避免违反唯一约束
     *
     * @param diaryId 日记ID
     * @param personId 人物ID
     */
    private void checkConstraint(Integer diaryId, Integer personId, LocalDate appearanceDate, DiaryPersonPojo.MentionType mentionType) {
        if (diaryPersonMapper.selectByDiaryIdAndPersonId(diaryId, personId) == null) {
            diaryPersonMapper.insert(new DiaryPersonPojo(diaryId, personId, appearanceDate, mentionType));
        }
    }
}