package com.lyric.lyric.Service.tag;

import com.lyric.lyric.Mapper.relation.DiaryPersonMapper;
import com.lyric.lyric.Mapper.relation.EventPersonMapper;
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

/**
 * 提供人物标签处理的相关方法
 *
 * @author Yichaoxuan
 * @since 2025-12-11
 */
@Slf4j
@Service
public class PersonsService {
    
    private final PersonMapper personMapper;

    private final AIAnalysisService aiAnalysisService;

    private final DiaryPersonMapper diaryPersonMapper;

    private final EventPersonMapper eventPersonMapper;
    
    public PersonsService(PersonMapper personMapper, AIAnalysisService aiAnalysisService, DiaryPersonMapper diaryPersonMapper, EventPersonMapper eventPersonMapper) {
        this.personMapper = personMapper;
        this.aiAnalysisService = aiAnalysisService;
        this.diaryPersonMapper = diaryPersonMapper;
        this.eventPersonMapper = eventPersonMapper;
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

        //一级匹配 通过性别匹配
        List<PersonPojo> candidatePersons = findByGender(newPersonInfo.getGender());
        log.info("性别匹配：{}", newPersonInfo.getGender());
        if (candidatePersons != null && !candidatePersons.isEmpty()) {
            //二级匹配 通过名字和别称匹配
            candidatePersons = findExactMatch(newPersonName, candidatePersons);
            log.info("名称匹配与别称匹配：{}", newPersonName);
            if (!candidatePersons.isEmpty()) {
                //三级匹配 通过关系匹配
                candidatePersons = findByNameAndRelation(newPersonInfo.getRelationship(), candidatePersons);
                log.info("关系匹配：{}", newPersonInfo.getRelationship());
                if (!candidatePersons.isEmpty()) {
                    //四级匹配 通过AI匹配
                    Integer matchTheCharacterIndex = aiAnalysisService.personTagDeduplicationAnalysis(newPersonName, newPersonInfo, candidatePersons);
                    if(matchTheCharacterIndex == -1) {
                        log.info("未开启AI分析功能");
                    }
                    if (matchTheCharacterIndex == 0 ) {
                        //判断为新人物
                        log.info("AI判定为新人物：{}", newPersonName);
                        Integer personId = personMapper.insert(new PersonPojo(newPersonName, newPersonInfo));

                        //检查是否已存在关联，避免违反唯一约束
                        checkConstraint(diaryId, personId, DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
                    }
                    
                    //为同一人物,更新人物信息
                    personUpdater(diaryId, candidatePersons.get(matchTheCharacterIndex), newPersonName, newPersonInfo);
                }
            }
        }
        //判断为新人物
        log.info("判定为新人物：{}", newPersonName);
        Integer personId = personMapper.insert(new PersonPojo(newPersonName, newPersonInfo));

        //检查是否已存在关联，避免违反唯一约束
        checkConstraint(diaryId, personId, DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()), newPersonInfo.getMentionType());
    }

    /**
     * 根据性别查找人物
     *
     * @param gender 性别
     * @return List<PersonPojo> 符合性别条件的候选人物列表，如果没有匹配项则返回null
     */
    private List<PersonPojo> findByGender(String gender) {
        List<PersonPojo> candidatePersons = personMapper.selectByGender(gender);
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
     * @return List<PersonPojo> 匹配到的人物列表
     */
    private List<PersonPojo> findExactMatch(String name, List<PersonPojo> candidatePersons) {

        //定义一个空列表，记录候选人物
        List<PersonPojo> newCandidatePersons = new ArrayList<>();

        for (PersonPojo candidatePerson : candidatePersons) {
            //查询该具有相同名字的人物,判断是否存在相同名字
            if (candidatePerson.getName().equals(name)) {
                newCandidatePersons.add(candidatePerson);
            }

            //查询是否与别称相同
            String alias = candidatePerson.getAlias();

            //别称可能为空
            if (alias == null) {
                continue;
            }

            //判断名称是否存在于别称中
            if (alias.contains(name)) {
                newCandidatePersons.add(candidatePerson);
            }
        }

        //返回候选人物列表
        return newCandidatePersons;
    }

    /**
     * 人物更新处理器
     * 更新人物信息，并将更新后的人物信息保存到数据库中
     *
     * @param diaryId 日记ID
     * @param person 待更新人物
     * @param newPersonName 新人物名称
     * @param newPersonInfo 新人物信息
     */
    private void personUpdater(Integer diaryId, PersonPojo person, String newPersonName, AITagJson.PersonInfo newPersonInfo) {

        // 添加新的人物别名
        person.setAlias(person.getAlias() + "," + newPersonName);

        // 添加新关系
        person.setRelation(person.getRelation() + "," + newPersonInfo.getRelationship());

        // 添加新性格
        person.setPersonality(person.getPersonality() + "," + newPersonInfo.getPersonality());

        //更新出现次数
        person.setAppearanceCount(person.getAppearanceCount() + 1);

        //更新人物信息
        personMapper.update(person);

        // 查询日记-人物关联表,更新人物在某篇日记中被提及/出现的时间
        DiaryPersonPojo diaryPerson = diaryPersonMapper.selectByDiaryIdAndPersonId(diaryId, person.getId());
        diaryPerson.setAppearanceDate(DateTimeUtils.parseDate(newPersonInfo.getAppearanceDate()));
        diaryPerson.setMentionType(newPersonInfo.getMentionType());
        diaryPersonMapper.update(diaryPerson);
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
        //定义一个空列表，记录候选人物
        List<PersonPojo> newCandidatePersons = new ArrayList<>();

        //查询该具有相同关系的人物
        for (PersonPojo candidatePerson : candidatePersons) {
            //判断关系是否相同
            if (candidatePerson.getRelation().equals(relation)) {
                newCandidatePersons.add(candidatePerson);
            }
        }
        //返回候选人物列表
        return newCandidatePersons;
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