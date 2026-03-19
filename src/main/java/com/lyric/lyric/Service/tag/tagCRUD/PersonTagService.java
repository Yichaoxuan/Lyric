package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Mapper.relation.DiaryPersonMapper;
import com.lyric.lyric.Mapper.relation.SubEventPersonMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.relation.DiaryPersonPojo;
import com.lyric.lyric.POJO.relation.SubEventPersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.PersonPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 人物标签服务类
 * 提供人物标签的增删改查功能，支持级联删除关联表
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class PersonTagService {

    private final PersonMapper personMapper;
    private final DiaryPersonMapper diaryPersonMapper;
    private final SubEventPersonMapper subEventPersonMapper;

    public PersonTagService(PersonMapper personMapper, DiaryPersonMapper diaryPersonMapper, 
                           SubEventPersonMapper subEventPersonMapper) {
        this.personMapper = personMapper;
        this.diaryPersonMapper = diaryPersonMapper;
        this.subEventPersonMapper = subEventPersonMapper;
    }

    /**
     * 创建新的人物标签
     * @param personPojo 人物实体对象
     * @return 创建后的人物 ID（数据库自增主键）
     */
    public Integer createPerson(PersonPojo personPojo) {
        log.info("创建新人物标签：name={}, relation={}", personPojo.getName(), personPojo.getRelation());
        personMapper.insert(personPojo);
        log.info("人物标签创建成功，ID={}", personPojo.getId());
        return personPojo.getId();
    }

    /**
     * 根据 ID 查询人物标签
     * @param id 人物 ID
     * @return 人物实体对象，若不存在则返回 null
     */
    public PersonPojo getPersonById(Integer id) {
        log.debug("查询人物标签：id={}", id);
        PersonPojo person = personMapper.selectById(id);
        if (person == null) {
            log.warn("人物标签不存在：id={}", id);
        }
        return person;
    }

    /**
     * 根据名称查询人物标签
     * @param name 人物名称
     * @return 人物实体对象，若不存在则返回 null
     */
    public PersonPojo getPersonByName(String name) {
        log.debug("按名称查询人物标签：name={}", name);
        PersonPojo person = personMapper.selectByName(name);
        if (person == null) {
            log.warn("人物标签不存在：name={}", name);
        }
        return person;
    }

    /**
     * 查询所有人物标签
     * @return 人物列表
     */
    public List<PersonPojo> getAllPersons() {
        log.debug("查询所有人物标签");
        return personMapper.selectAll();
    }

    /**
     * 根据性别查询人物标签
     * @param gender 性别（0:未知，1:男，2:女）
     * @return 人物列表
     */
    public List<PersonPojo> getPersonsByGender(Integer gender) {
        log.debug("按性别查询人物标签：gender={}", gender);
        return personMapper.selectByGender(gender);
    }

    /**
     * 根据关系查询人物标签
     * @param relation 关系关键词
     * @return 人物列表
     */
    public List<PersonPojo> getPersonsByRelation(String relation) {
        log.debug("按关系查询人物标签：relation={}", relation);
        return personMapper.selectByRelation(relation);
    }

    /**
     * 更新人物标签信息
     * @param personPojo 人物实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updatePerson(PersonPojo personPojo) {
        log.info("更新人物标签：id={}, name={}", personPojo.getId(), personPojo.getName());
        int rows = personMapper.update(personPojo);
        if (rows > 0) {
            log.info("人物标签更新成功：id={}", personPojo.getId());
            return true;
        } else {
            log.error("人物标签更新失败：id={}", personPojo.getId());
            return false;
        }
    }

    /**
     * 删除人物标签（级联删除关联表）
     * 先删除 sub_event_person 和 diary_person 关联表中的相关记录，再删除人物本身
     * @param id 人物 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePerson(Integer id) {
        log.info("删除人物标签：id={}", id);
        
        // 检查人物是否存在
        PersonPojo person = personMapper.selectById(id);
        if (person == null) {
            log.error("人物标签不存在，无法删除：id={}", id);
            return false;
        }
        
        // 级联删除：先删除 sub_event_person 关联表中的记录
        try {
            List<SubEventPersonPojo> subEventRelations = subEventPersonMapper.selectByPersonId(id);
            if (!subEventRelations.isEmpty()) {
                log.info("人物参与了 {} 个子事件，将级联删除这些关联", subEventRelations.size());
                for (SubEventPersonPojo relation : subEventRelations) {
                    subEventPersonMapper.deleteById(relation.getId());
                }
                log.info("已删除 {} 条子事件 - 人物关联记录", subEventRelations.size());
            }
            
            // 级联删除：再删除 diary_person 关联表中的记录
            List<DiaryPersonPojo> diaryRelations = diaryPersonMapper.selectByPersonId(id);
            if (!diaryRelations.isEmpty()) {
                log.info("人物被 {} 篇日记使用，将级联删除这些关联", diaryRelations.size());
                for (DiaryPersonPojo relation : diaryRelations) {
                    diaryPersonMapper.deleteById(relation.getId());
                }
                log.info("已删除 {} 条日记 - 人物关联记录", diaryRelations.size());
            }
            
            // 删除人物本身
            int rows = personMapper.deleteById(id);
            if (rows > 0) {
                log.info("人物标签删除成功：id={}", id);
                return true;
            } else {
                log.error("人物标签删除失败：id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除人物标签时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    /**
     * 增加人物出现次数
     * @param id 人物 ID
     * @return 是否更新成功
     */
    public boolean incrementAppearanceCount(Integer id) {
        PersonPojo person = personMapper.selectById(id);
        if (person == null) {
            log.error("人物不存在，无法增加出现次数：id={}", id);
            return false;
        }
        
        Integer currentCount = person.getAppearanceCount();
        person.setAppearanceCount(currentCount + 1);
        
        log.debug("增加人物出现次数：id={}, oldCount={}, newCount={}", 
                  id, currentCount, person.getAppearanceCount());
        
        return updatePerson(person);
    }
}
