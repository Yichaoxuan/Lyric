package com.lyric.lyric.Service.tag.parsing;

import com.lyric.lyric.Mapper.relation.*;
import com.lyric.lyric.Mapper.tag.entity.EventMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.Mapper.tag.entity.PersonMapper;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.POJO.relation.SubEventLocationPojo;
import com.lyric.lyric.POJO.relation.SubEventPersonPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.SubEventPojo;
import com.lyric.lyric.POJO.tag.entityTag.event.TogEventPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 事件标签服务类
 *
 * @author Yichaoxuan
 * @serial 2026/02/08
 */
@Slf4j
@Service
public class EventParsingService {

    public final SubEventLocationMapper subEventLocationMapper;
    private final EventMapper eventMapper;
    private final SubEventPersonMapper subEventPersonMapper;
    private final PersonMapper personMapper;
    private final LocationMapper locationMapper;

    public EventParsingService(EventMapper eventMapper, SubEventPersonMapper subEventPersonMapper, PersonMapper personMapper,
                               LocationMapper locationMapper, SubEventLocationMapper subEventLocationMapper) {
        this.eventMapper = eventMapper;
        this.subEventPersonMapper = subEventPersonMapper;
        this.personMapper = personMapper;
        this.locationMapper = locationMapper;
        this.subEventLocationMapper = subEventLocationMapper;
    }

    public void eventDeduplication(Integer diaryId, Map<String, AITagJson.TogEventInfo> newEventInfoMap,
            Map<Integer, Integer> integerIntegerMap) {

        if (newEventInfoMap == null) {
            return;
        }
        if (integerIntegerMap == null) {
            return;
        }

        // 遍历父事件
        for (Map.Entry<String, AITagJson.TogEventInfo> togEvent : newEventInfoMap.entrySet()) {

            String togEventName = togEvent.getKey();

            AITagJson.TogEventInfo newTogEventInfo = togEvent.getValue();

            if (newTogEventInfo == null) {
                continue;
            }

            Integer togEventId = togEventDeduplication(diaryId, togEventName, newTogEventInfo);

            // 遍历该个父事件下的所有子事件
            for (Map.Entry<String, AITagJson.SubEventInfo> subEvent : newTogEventInfo.getSubEvents().entrySet()) {
                String subEventName = subEvent.getKey();
                AITagJson.SubEventInfo newSubEventInfo = subEvent.getValue();
                subEventDeduplication(togEventId, subEventName, newSubEventInfo, integerIntegerMap);
            }
        }

    }

    public Integer togEventDeduplication(Integer diaryId, String togEventName, AITagJson.TogEventInfo newEventInfo) {

        try {
            TogEventPojo togEventPojo = new TogEventPojo(diaryId, togEventName, newEventInfo);

            addNewEvent(togEventPojo);

            return togEventPojo.getId();

        } catch (Exception e) {
            log.error("父事件去重失败");
            log.error("{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 子事件标签去重
     *
     * @param togEventId        父事件ID
     * @param newSubEventName   子事件名称
     * @param newSubEventInfo   子事件对象
     * @param integerIntegerMap 参与人物ID和人物索引
     */
    public void subEventDeduplication(Integer togEventId, String newSubEventName,
            AITagJson.SubEventInfo newSubEventInfo, Map<Integer, Integer> integerIntegerMap) {

        try {
            SubEventPojo subEventPojo = new SubEventPojo(togEventId, newSubEventName, newSubEventInfo);

            addNewEvent(subEventPojo);

            addNewEventPersonRelation(subEventPojo.getId(), newSubEventInfo, integerIntegerMap);

            addNewEventLocationRelation(subEventPojo.getId(), newSubEventInfo, integerIntegerMap);

        } catch (Exception e) {
            log.error("子事件去重失败");
            log.error("{}", e.getMessage(), e);
        }

    }

    /**
     * 添加父事件
     *
     * @param togEventPojo 父事件对象
     */
    private void addNewEvent(TogEventPojo togEventPojo) {
        // 将父事件插入数据库
        eventMapper.insertTogEvent(togEventPojo);
        log.info("父事件添加日记成功：{}", togEventPojo.getName());
    }

    /**
     * 添加子事件
     *
     * @param subEventPojo 子事件对象
     */
    private void addNewEvent(SubEventPojo subEventPojo) {
        // 将子事件插入数据库
        eventMapper.insertSubEvent(subEventPojo);
        log.info("子事件添加成功：{}", subEventPojo.getName());
    }

    /**
     * 添加子事件和人物关联
     *
     * @param subEventId        子事件ID
     * @param newSubEventInfo   子事件对象（包含了该事件中出现的人物的索引）
     * @param integerIntegerMap 包含地点ID和地点索引
     */
    private void addNewEventPersonRelation(Integer subEventId, AITagJson.SubEventInfo newSubEventInfo,
            Map<Integer, Integer> integerIntegerMap) {
        if (newSubEventInfo.getPersons() != null) {
            for (Map.Entry<String, String> personMap : newSubEventInfo.getPersons().entrySet()) {
                String personIndex = personMap.getKey();
                String role = personMap.getValue();
                for (Map.Entry<Integer, Integer> entry : integerIntegerMap.entrySet()) {
                    if (entry.getValue().equals(Integer.parseInt(personIndex))) {
                        com.lyric.lyric.POJO.tag.entityTag.PersonPojo person = personMapper.selectById(entry.getKey());
                        if (person != null && person.getId() != null) {
                            subEventPersonMapper.insert(new SubEventPersonPojo(subEventId, person.getId(), role));
                            log.info("添加子事件和人物关联成功：人物索引={}, 角色={}", personIndex, role);
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加子事件和地点关联
     * 
     * @param subEventId        子事件ID
     * @param newSubEventInfo   子事件对象（包含了该事件中出现的地点的索引）
     * @param integerIntegerMap 包含地点ID和地点索引
     */
    private void addNewEventLocationRelation(Integer subEventId, AITagJson.SubEventInfo newSubEventInfo,
            Map<Integer, Integer> integerIntegerMap) {
        if (newSubEventInfo.getLocation() != null) {
            for (Map.Entry<String, String> location : newSubEventInfo.getLocation().entrySet()) {
                String locationIndex = location.getKey();
                String locationName = location.getValue();
                for (Map.Entry<Integer, Integer> entry : integerIntegerMap.entrySet()) {
                    if (entry.getValue().equals(Integer.parseInt(locationIndex))) {
                        com.lyric.lyric.POJO.tag.entityTag.LocationPojo locationPojo = locationMapper
                                .selectById(entry.getKey());
                        if (locationPojo != null && locationPojo.getId() != null) {
                            subEventLocationMapper.insert(new SubEventLocationPojo(subEventId, locationPojo.getId()));
                            log.info("添加子事件和地点关联成功：地点索引={}, 地点名称={}", locationIndex, locationName);
                        }
                    }
                }
            }
        }
    }
}
