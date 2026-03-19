package com.lyric.lyric.Controller.userSetting;

import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.POJO.usersettings.UserSettingsPojo;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.resultUtils.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置控制器
 * 提供获取和更新用户设置的REST API接口
 *
 * @author Yichaoxun
 * @since 2025-11-23
 */
@RestController
@RequestMapping("/settings")
public class UserSettingsController {
    
    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    /**
     * 获取用户设置
     *
     * @return 用户设置信息
     */
    @GetMapping("/getUserSettings")
    public Result<UserSettingsPojo> getUserSettings() {
        return userSettingsService.getLatestConfig();
    }

    /**
     * 更新用户设置
     *
     * @param userSettings 新的用户设置
     * @return 更新结果
     */
    @PutMapping("/updateUserSettings")
    public Result<Void> updateUserSettings(@RequestBody UserSettingsPojo userSettings) {
        return userSettingsService.updateUserSettings(userSettings);
    }

}