package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Mapper.relation.SubEventLocationMapper;
import com.lyric.lyric.Mapper.relation.SubEventPersonMapper;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.POJO.tag.entityTag.event.SubEventPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.TogEventPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 事件标签服务类
 * 提供父事件 (TogEvent) 和子事件 (SubEvent) 的增删改查功能
 * 删除时支持级联删除关联表数据
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class EventTagService {

    private final EventMapper eventMapper;
    private final SubEventLocationMapper subEventLocationMapper;
    private final SubEventPersonMapper subEventPersonMapper;

    public EventTagService(EventMapper eventMapper, 
                          SubEventLocationMapper subEventLocationMapper,
                          SubEventPersonMapper subEventPersonMapper) {
        this.eventMapper = eventMapper;
        this.subEventLocationMapper = subEventLocationMapper;
        this.subEventPersonMapper = subEventPersonMapper;
    }

    // ==================== 父事件 (TogEvent) 相关操作 ====================

    /**
     * 创建父事件
     * @param togEvent 父事件实体对象
     * @return 创建后的父事件 ID（数据库自增主键）
     */
    public Integer createTogEvent(TogEventPojo togEvent) {
        log.info("创建新父事件：name={}, startDate={}, endDate={}", 
                togEvent.getName(), togEvent.getStartDate(), togEvent.getEndDate());
        eventMapper.insertTogEvent(togEvent);
        log.info("父事件创建成功，ID={}", togEvent.getId());
        return togEvent.getId();
    }

    /**
     * 根据 ID 查询父事件
     * @param id 父事件 ID
     * @return 父事件实体对象，若不存在则返回 null
     */
    public TogEventPojo getTogEventById(Integer id) {
        log.debug("查询父事件：id={}", id);
        TogEventPojo togEvent = eventMapper.selectTogEventById(id);
        if (togEvent == null) {
            log.warn("父事件不存在：id={}", id);
        }
        return togEvent;
    }

    /**
     * 根据日记 ID 查询父事件
     * @param diaryId 日记 ID
     * @return 父事件实体对象，若不存在则返回 null
     */
    public TogEventPojo getTogEventByDiaryId(Integer diaryId) {
        log.debug("根据日记 ID 查询父事件：diaryId={}", diaryId);
        TogEventPojo togEvent = eventMapper.selectTogEventByDiaryId(diaryId);
        if (togEvent == null) {
            log.warn("日记未关联父事件：diaryId={}", diaryId);
        }
        return togEvent;
    }

    /**
     * 查询所有父事件
     * @return 父事件列表
     */
    public List<TogEventPojo> getAllTogEvents() {
        log.debug("查询所有父事件");
        return eventMapper.selectAllTogEvents();
    }

    /**
     * 更新父事件信息
     * @param togEvent 父事件实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateTogEvent(TogEventPojo togEvent) {
        log.info("更新父事件：id={}, name={}", togEvent.getId(), togEvent.getName());
        int rows = eventMapper.updateTogEvent(togEvent);
        if (rows > 0) {
            log.info("父事件更新成功：id={}", togEvent.getId());
            return true;
        } else {
            log.error("父事件更新失败：id={}", togEvent.getId());
            return false;
        }
    }

    /**
     * 删除父事件（级联删除所有子事件及关联表）
     * 删除顺序：sub_event_location -> sub_event_person -> sub_event -> tog_event
     * @param id 父事件 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTogEvent(Integer id) {
        log.info("删除父事件：id={}", id);
        
        // 检查父事件是否存在
        TogEventPojo togEvent = eventMapper.selectTogEventById(id);
        if (togEvent == null) {
            log.error("父事件不存在，无法删除：id={}", id);
            return false;
        }
        
        try {
            // 查询该父事件下的所有子事件
            List<SubEventPojo> subEvents = eventMapper.selectSubEventsByTogEventId(id);
            if (!subEvents.isEmpty()) {
                log.info("父事件下包含 {} 个子事件，将级联删除", subEvents.size());
                
                // 对每个子事件进行级联删除
                for (SubEventPojo subEvent : subEvents) {
                    deleteSubEventWithRelations(subEvent.getId());
                }
                log.info("已删除 {} 个子事件及其关联数据", subEvents.size());
            }
            
            // 删除父事件本身
            int rows = eventMapper.deleteTogEventById(id);
            if (rows > 0) {
                log.info("父事件删除成功：id={}", id);
                return true;
            } else {
                log.error("父事件删除失败：id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除父事件时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    // ==================== 子事件 (SubEvent) 相关操作 ====================

    /**
     * 创建子事件
     * @param subEvent 子事件实体对象
     * @return 创建后的子事件 ID（数据库自增主键）
     */
    public Integer createSubEvent(SubEventPojo subEvent) {
        log.info("创建新子事件：name={}, togEventId={}, eventDate={}", 
                subEvent.getName(), subEvent.getTogEventId(), subEvent.getEventDate());
        eventMapper.insertSubEvent(subEvent);
        log.info("子事件创建成功，ID={}", subEvent.getId());
        return subEvent.getId();
    }

    /**
     * 根据 ID 查询子事件
     * @param id 子事件 ID
     * @return 子事件实体对象，若不存在则返回 null
     */
    public SubEventPojo getSubEventById(Integer id) {
        log.debug("查询子事件：id={}", id);
        SubEventPojo subEvent = eventMapper.selectSubEventById(id);
        if (subEvent == null) {
            log.warn("子事件不存在：id={}", id);
        }
        return subEvent;
    }

    /**
     * 根据父事件 ID 查询所有子事件
     * @param togEventId 父事件 ID
     * @return 子事件列表
     */
    public List<SubEventPojo> getSubEventsByTogEventId(Integer togEventId) {
        log.debug("根据父事件 ID 查询子事件：togEventId={}", togEventId);
        return eventMapper.selectSubEventsByTogEventId(togEventId);
    }

    /**
     * 查询所有子事件
     * @return 子事件列表
     */
    public List<SubEventPojo> getAllSubEvents() {
        log.debug("查询所有子事件");
        return eventMapper.selectAllSubEvents();
    }

    /**
     * 更新子事件信息
     * @param subEvent 子事件实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateSubEvent(SubEventPojo subEvent) {
        log.info("更新子事件：id={}, name={}", subEvent.getId(), subEvent.getName());
        int rows = eventMapper.updateSubEvent(subEvent);
        if (rows > 0) {
            log.info("子事件更新成功：id={}", subEvent.getId());
            return true;
        } else {
            log.error("子事件更新失败：id={}", subEvent.getId());
            return false;
        }
    }

    /**
     * 删除子事件（不删除父事件，仅级联删除该子事件的关联表）
     * 删除顺序：sub_event_location -> sub_event_person -> sub_event
     * @param id 子事件 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSubEvent(Integer id) {
        log.info("删除子事件：id={}", id);
        
        // 检查子事件是否存在
        SubEventPojo subEvent = eventMapper.selectSubEventById(id);
        if (subEvent == null) {
            log.error("子事件不存在，无法删除：id={}", id);
            return false;
        }
        
        try {
            // 级联删除关联表数据
            deleteSubEventWithRelations(id);
            log.info("子事件及其关联数据删除成功：id={}", id);
            return true;
        } catch (Exception e) {
            log.error("删除子事件时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw e; // 抛出异常以触发事务回滚
        }
    }

    /**
     * 内部方法：级联删除子事件的关联表数据
     * 删除顺序：sub_event_location -> sub_event_person -> sub_event
     * @param subEventId 子事件 ID
     */
    private void deleteSubEventWithRelations(Integer subEventId) {
        log.debug("开始级联删除子事件关联数据：subEventId={}", subEventId);
        
        // 1. 删除 sub_event_location 关联
        List<com.lyric.lyric.POJO.relation.SubEventLocationPojo> locationRelations = 
            subEventLocationMapper.selectByEventId(subEventId);
        if (!locationRelations.isEmpty()) {
            log.info("子事件关联了 {} 个地点，正在删除", locationRelations.size());
            for (com.lyric.lyric.POJO.relation.SubEventLocationPojo relation : locationRelations) {
                subEventLocationMapper.deleteById(relation.getId());
            }
            log.info("已删除 {} 条地点关联记录", locationRelations.size());
        }
        
        // 2. 删除 sub_event_person 关联
        List<com.lyric.lyric.POJO.relation.SubEventPersonPojo> personRelations = 
            subEventPersonMapper.selectByEventId(subEventId);
        if (!personRelations.isEmpty()) {
            log.info("子事件关联了 {} 个人物，正在删除", personRelations.size());
            for (com.lyric.lyric.POJO.relation.SubEventPersonPojo relation : personRelations) {
                subEventPersonMapper.deleteById(relation.getId());
            }
            log.info("已删除 {} 条人物关联记录", personRelations.size());
        }
        
        // 3. 删除子事件本身
        int rows = eventMapper.deleteSubEventById(subEventId);
        if (rows > 0) {
            log.debug("子事件删除成功：id={}", subEventId);
        } else {
            log.error("子事件删除失败：id={}", subEventId);
        }
    }
}
