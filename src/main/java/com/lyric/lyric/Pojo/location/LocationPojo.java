package com.lyric.lyric.Pojo.location;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 地点实体类
 * 对应数据库表: location
 *
 * @author Lyric
 * @since 2025-11-21
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 地点名称
     */
    private String name;

    /**
     * 地点别名
     */
    private String alias;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 城市
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

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param name 地点名称
     * @param alias 地点别名
     * @param longitude 经度
     * @param latitude 纬度
     * @param city 城市
     * @param geoHash 地理哈希
     * @param firstAppearance 首次出现时间
     * @param lastAppearance 最后一次出现时间
     * @param appearanceCount 出现次数
     * @param importance 重要性等级
     */
    public LocationPojo(String name, String alias, Double longitude, Double latitude, String city,
                        String geoHash, LocalDateTime firstAppearance, LocalDateTime lastAppearance,
                        Integer appearanceCount, ImportanceLevel importance) {
        this.name = name;
        this.alias = alias;
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.geoHash = geoHash;
        this.firstAppearance = firstAppearance;
        this.lastAppearance = lastAppearance;
        this.appearanceCount = appearanceCount;
        this.importance = importance;
    }
}