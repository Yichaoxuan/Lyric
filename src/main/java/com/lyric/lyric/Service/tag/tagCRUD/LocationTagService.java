package com.lyric.lyric.Service.tag.tagCRUD;

import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.Mapper.relation.ActivityLocationMapper;
import com.lyric.lyric.Mapper.relation.DiaryActivityMapper;
import com.lyric.lyric.Mapper.tag.entity.LocationMapper;
import com.lyric.lyric.POJO.tag.entityTag.LocationPojo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 地点标签服务类
 * 提供地点标签的增删改查功能
 *
 * @author Yichaoxuan
 * @since 2026-03-19
 */
@Slf4j
@Service
public class LocationTagService {

    private final LocationMapper locationMapper;
    private final ActivityLocationMapper activityLocationMapper;
    private final DiaryActivityMapper diaryActivityMapper;

    public LocationTagService(LocationMapper locationMapper, ActivityLocationMapper activityLocationMapper,
            DiaryActivityMapper diaryActivityMapper) {
        this.locationMapper = locationMapper;
        this.activityLocationMapper = activityLocationMapper;
        this.diaryActivityMapper = diaryActivityMapper;
    }

    /**
     * 创建新的地点标签
     * 
     * @param locationPojo 地点实体对象
     */
    public void createLocation(LocationPojo locationPojo) {
        log.info("创建新地点标签：name={}, city={}, province={}", locationPojo.getName(),
                locationPojo.getCity(), locationPojo.getProvince());
        locationMapper.insert(locationPojo);
        log.info("地点标签创建成功，ID={}", locationPojo.getId());
    }

    /**
     * 根据 ID 查询地点标签
     * 
     * @param id 地点 ID
     * @return 地点实体对象，若不存在则返回 null
     */
    public LocationPojo getLocationById(Integer id) {
        log.debug("查询地点标签：id={}", id);
        LocationPojo location = locationMapper.selectById(id);
        if (location == null) {
            log.warn("地点标签不存在：id={}", id);
        }
        return location;
    }

    /**
     * 根据名称查询地点标签
     * 
     * @param name 地点名称
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByName(String name) {
        log.debug("按名称查询地点标签：name={}", name);
        return locationMapper.selectByName(name);
    }

    /**
     * 根据别名查询地点标签
     * 
     * @param alias 地点别名
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByAlias(String alias) {
        log.debug("按别名查询地点标签：alias={}", alias);
        return locationMapper.selectByAlias(alias);
    }

    /**
     * 根据城市查询地点标签
     * 
     * @param city 城市
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByCity(String city) {
        log.debug("按城市查询地点标签：city={}", city);
        return locationMapper.selectByCity(city);
    }

    /**
     * 根据省份查询地点标签
     * 
     * @param province 省份
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByProvince(String province) {
        log.debug("按省份查询地点标签：province={}", province);
        return locationMapper.selectByProvince(province);
    }

    /**
     * 根据国家查询地点标签
     * 
     * @param country 国家
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByCountry(String country) {
        log.debug("按国家查询地点标签：country={}", country);
        return locationMapper.selectByCountry(country);
    }

    /**
     * 根据日记ID 查询对应的地点标签列表
     * 
     * @param diaryId 日记ID
     * @return 地点实体列表，若日记没有关联地点则返回空列表
     */
    public List<LocationPojo> getLocationsByDiaryId(Integer diaryId) {
        log.debug("根据日记ID 查询地点标签：diaryId={}", diaryId);

        // 创建一个列表用于保存地点实体类
        List<LocationPojo> locations = new ArrayList<>();

        // 步骤 1: 查询该日记关联的所有活动ID
        List<Integer> activityIds = diaryActivityMapper.selectByDiaryId(diaryId);

        if (activityIds.isEmpty()) {
            log.warn("日记没有关联活动：diaryId={}", diaryId);
            return locations;
        }

        // 步骤 2: 查询每个活动关联的所有地点 ID
        for (Integer activityId : activityIds) {
            List<Integer> locationIds = activityLocationMapper.selectLocationIdsByActivityId(activityId);

            if (locationIds.isEmpty()) {
//                log.warn("活动没有关联地点：activityId={}", activityId);
                continue;
            }

            // 步骤 3: 批量查询地点详情
            for (Integer locationId : locationIds) {
                LocationPojo location = locationMapper.selectById(locationId);
                locations.add(location);
            }
        }

        return locations;
    }

    /**
     * 根据活动ID获取地点标签列表
     * 
     * @param activityId 活动ID
     * @return 地点列表
     */
    public List<LocationPojo> getLocationsByActivityId(Integer activityId) {
        log.debug("根据活动ID查询地点标签：activityId={}", activityId);

        // 验证参数
        if (activityId == null) {
            log.warn("活动ID为空");
            return new ArrayList<>();
        }

        // 步骤 1: 查询该活动关联的所有地点关系
        List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> relations = activityLocationMapper
                .selectByActivityId(activityId);

        if (relations == null || relations.isEmpty()) {
            log.debug("活动未关联任何地点：activityId={}", activityId);
            return new ArrayList<>();
        }

        // 步骤 2: 提取地点ID并去重
        java.util.Set<Integer> locationIds = new java.util.HashSet<>();
        for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : relations) {
            locationIds.add(relation.getLocationId());
        }

        // 步骤 3: 批量查询地点详情
        List<LocationPojo> locations = new ArrayList<>();
        for (Integer locationId : locationIds) {
            LocationPojo location = locationMapper.selectById(locationId);
            if (location != null) {
                locations.add(location);
            } else {
                log.warn("地点不存在，跳过：locationId={}", locationId);
            }
        }

        log.info("根据活动ID查询到 {} 个地点：activityId={}", locations.size(), activityId);
        return locations;
    }

    /**
     * 查询所有地点标签
     * 
     * @return 地点列表
     */
    public List<LocationPojo> getAllLocations() {
        log.debug("查询所有地点标签");
        return locationMapper.selectAll();
    }

    /**
     * 更新地点标签信息
     * 
     * @param locationPojo 地点实体对象（必须包含 id）
     * @return 是否更新成功
     */
    public boolean updateLocation(LocationPojo locationPojo) {
        log.info("更新地点标签：id={}, name={}", locationPojo.getId(), locationPojo.getName());
        int rows = locationMapper.update(locationPojo);
        if (rows > 0) {
            log.info("地点标签更新成功：id={}", locationPojo.getId());
            return true;
        } else {
            log.error("地点标签更新失败：id={}", locationPojo.getId());
            return false;
        }
    }

    /**
     * 删除地点标签（级联删除关联表）
     * 先删除 activity_location 关联表中的相关记录，再删除地点本身
     * 
     * @param id 地点 ID
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLocation(Integer id) {
        log.info("删除地点标签：id={}", id);

        // 检查地点是否存在
        LocationPojo location = locationMapper.selectById(id);
        if (location == null) {
            log.error("地点标签不存在，无法删除：id={}", id);
            return false;
        }

        try {
            // 级联删除：删除 activity_location 关联表中的记录
            List<com.lyric.lyric.POJO.relation.ActivityLocationPojo> activityRelations = activityLocationMapper
                    .selectByLocationId(id);
            if (!activityRelations.isEmpty()) {
                log.info("地点参与了 {} 个活动，将级联删除这些关联", activityRelations.size());
                for (com.lyric.lyric.POJO.relation.ActivityLocationPojo relation : activityRelations) {
                    activityLocationMapper.deleteById(relation.getId());
                }
                log.info("已删除 {} 条活动 - 地点关联记录", activityRelations.size());
            }

            // 删除地点本身
            int rows = locationMapper.deleteById(id);
            if (rows > 0) {
                log.info("地点标签删除成功：id={}", id);
                return true;
            } else {
                log.error("地点标签删除失败：id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("删除地点标签时发生异常：id={}, error={}", id, e.getMessage(), e);
            throw new SystemException(SystemErrorMsgEnums.SYSTEM_ERROR, e); // 抛出异常以触发事务回滚
        }
    }

    /**
     * 增加地点出现次数
     * 
     * @param id 地点 ID
     * @return 是否更新成功
     */
    public boolean incrementAppearanceCount(Integer id) {
        LocationPojo location = locationMapper.selectById(id);
        if (location == null) {
            log.error("地点不存在，无法增加出现次数：id={}", id);
            return false;
        }

        Integer currentCount = location.getAppearanceCount();
        location.setAppearanceCount(currentCount + 1);

        log.debug("增加地点出现次数：id={}, oldCount={}, newCount={}",
                id, currentCount, location.getAppearanceCount());

        return updateLocation(location);
    }
}
