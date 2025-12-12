package com.lyric.lyric.DTO.tag.entityTag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 地点请求DTO类
 * 只包含前端可信字段
 *
 * @author Yichaoxuan
 */
@Getter
@Setter
@NoArgsConstructor
public class Location {

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
     * 所在城市
     */
    private String city;

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
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 有参构造方法
     * @param id 主键ID
     * @param name 地点名称
     * @param alias 地点别称
     * @param longitude 经度
     * @param latitude 纬度
     * @param city 城市
     * @param geoHash GeoHash
     * @param firstAppearance 首次出现时间
     * @param lastAppearance 最后一次出现时间
     * @param importance 重要性等级
     */
    public Location(Integer id, String name, String alias, String description, String color, Double longitude, Double latitude, String city,
                   String geoHash, LocalDateTime firstAppearance, LocalDateTime lastAppearance,
                   ImportanceLevel importance) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.color = color;
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.geoHash = geoHash;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.importance = importance;
    }

    /**
     * 重要性等级枚举
     */
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