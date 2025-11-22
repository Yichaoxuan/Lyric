package com.lyric.lyric.Dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户设置请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class UserSetting {

    /**
     * 首次使用日期
     */
    private String firstUseDate;

    /**
     * API配置
     */
    private String apiConfig;

    /**
     * 用户配置
     */
    private String userConfig;
    /**
     * 有参构造方法
     * @param firstUseDate 首次使用日期
     * @param apiConfig API配置
     * @param userConfig 用户配置
     */
    public UserSetting(String firstUseDate, String apiConfig, String userConfig) {
        this.firstUseDate = firstUseDate;
        this.apiConfig = apiConfig;
        this.userConfig = userConfig;
    }
}