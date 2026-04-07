package com.lyric.lyric.POJO.tag.entityTag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 地点实体类
 * 对应数据库表: location
 *
 * @author Yichaoxuan
 * @since 2026-02-16
 */
@Data
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
     * 地点明确性标识
     * "0" 表示具有明确指代性（如天安门、大兴机场、廊坊师范学院等）
     * "1" 表示不具有明确指代性（如廊坊师范学院食堂、校医务室、学校西门烧烤摊等）
     */
    @JsonIgnore
    private Boolean specificity = null;

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
        this.district = locationInfo.getDistrict();
        this.city = locationInfo.getCity();
        this.province = locationInfo.getProvince();
        this.country = locationInfo.getCountry();
        this.specificity = Integer.parseInt(locationInfo.getSpecificity()) == 0;
        this.geoHash = null;
        this.firstAppearance = DateTimeUtils.now();
        this.lastAppearance = firstAppearance;
        this.appearanceCount = 1;
        this.importance = ImportanceLevel.MEDIUM;
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