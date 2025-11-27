package com.lyric.lyric.Enums.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 成功消息枚举类
 * 用于定义系统中各种操作成功情况的提示消息
 */
@Getter
public enum SuccessMsgEnums {

    //日记模块相关
    SAVE_SUCCESS("保存成功"),
    DELETE_SUCCESS("删除成功"),
    MODIFY_SUCCESS("修改成功"),
    QUERY_SUCCESS("查询成功"),
    MOVE_TO_TRASH_SUCCESS("已移入回收站"),
    RESTORE_FROM_TRASH_SUCCESS("已从回收站恢复"),

    //用户设置
    SETTING_SUCCESS("设置成功"),
    MESSAGE_CONFIG_SUCCESS("响应消息更新成功");

    /**
     * 成功消息名称
     */
    private final String name;

    /**
     * 状态码
     */
    @Setter
    private String code;

    /**
     * 消息内容
     */
    @Setter
    private String message;

    /**
     * 成功消息枚举构造函数
     * @param name 成功名字
     */
    SuccessMsgEnums(String name) {
        this.name = name;
    }
}