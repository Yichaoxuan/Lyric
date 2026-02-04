package com.lyric.lyric.POJO.tag.entityTag;

import com.lyric.lyric.POJO.AI.AITagJson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 地点实体类
 * 对应数据库表: location
 *
 * @author Yichaoxuan
 * @since 2026-02-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 地点名称
     */
    private String name;

    /**
     * 地点别名
     */
    private String alias;

    /**
     * 地点描述
     */
    private String description;

    /**
     * 颜色代码
     */
    private String color;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 所在区县
     */
    private String district;
    /**
     * 所在城市
     */
    private String city;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 地理哈希 (用于附近地点查询优化)
     */
    private String geoHash;

    /**
     * 首次出现时间
     */
    private LocalDateTime firstAppearance;

    /**
     * 最后一次出现时间
     */
    private LocalDateTime lastAppearance;

    /**
     * 出现次数
     */
    private Integer appearanceCount;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 由AITagJson.LocationInfo转换为LocationPojo对象
     * @param name 地点名称
     * @param locationInfo AITagJson.LocationInfo对象
     */
    public LocationPojo(String name, AITagJson.LocationInfo locationInfo) {
        this.name = name;
        this.alias = null;
        this.description = locationInfo.getDescription();
        this.color = locationInfo.getColor();
        this.longitude = null;
        this.latitude = null;
        this.city = null;
        this.geoHash = null;
        this.firstAppearance = null;
        this.lastAppearance = null;
        this.appearanceCount = null;
        this.importance = ImportanceLevel.MEDIUM;
        this.createdAt = null;
    }

    /**
     * 重要性等级枚举
     */
    @Getter
    public enum ImportanceLevel {
        /**
         * 高重要性
         */
        HIGH,

        /**
         * 中等重要性
         */
        MEDIUM,

        /**
         * 低重要性
         */
        LOW
    }
}