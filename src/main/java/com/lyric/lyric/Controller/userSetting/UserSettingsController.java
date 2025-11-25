package com.lyric.lyric.Controller.userSetting;

import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.Pojo.usersettings.UserSettingsPojo;
import com.lyric.lyric.Service.userSettings.UserSettingsService;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsController.class);
    
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
        logger.info("收到获取用户设置的请求");
        UserSettingsPojo settings = userSettingsService.getUserSettings();
        logger.info("成功获取用户设置");
        return Result.success(SuccessMsgEnums.SETTING_SUCCESS.getCode(), settings);
    }

    /**
     * 更新用户设置
     *
     * @param userSettings 新的用户设置
     * @return 更新结果
     */
    @PutMapping("/updateUserSettings")
    public Result<Void> updateUserSettings(@RequestBody UserSettingsPojo userSettings) {
        logger.info("收到更新用户设置的请求");
        userSettingsService.updateUserSettings(userSettings);
        logger.info("用户设置更新成功");
        return Result.success(SuccessMsgEnums.SETTING_SUCCESS.getCode(), "设置更新成功");
    }

    /**
     * 检查特定功能是否启用
     *
     * @param featureName 功能名称
     * @return 功能启用状态
     */
    @GetMapping("/feature/{featureName}")
    public Result<Boolean> isFeatureEnabled(@PathVariable String featureName) {
        logger.debug("检查功能启用状态: {}", featureName);
        boolean enabled = userSettingsService.isFeatureEnabled(UserFunctionEnum.valueOf(featureName));
        logger.debug("功能 {} 启用状态: {}", featureName, enabled);
        return Result.success(SuccessMsgEnums.QUERY_SUCCESS.getCode(), enabled);
    }
    
    /**
     * 验证并打印用户设置配置
     *
     * @return 验证结果
     */
    @PostMapping("/validate")
    public Result<Void> validateSettings() {
        logger.info("收到验证用户设置配置的请求");
        userSettingsService.validateAndPrintSettings();
        logger.info("用户设置配置验证完成");
        return Result.success(SuccessMsgEnums.SETTING_SUCCESS.getCode(), "配置验证完成，请查看日志");
    }
}