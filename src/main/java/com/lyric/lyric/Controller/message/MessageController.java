package com.lyric.lyric.Controller.message;

import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import com.lyric.lyric.Service.message.MessageService;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 * 提供消息配置的动态更新REST API接口
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 更新消息配置
     *
     * @param messageConfigPojo 新的消息配置
     * @return 更新结果
     */
    @PutMapping("/updateMessageConfig")
    public Result<Void> updateMessageConfig(@RequestBody MessageConfigPojo messageConfigPojo) {
        logger.info("收到更新消息配置的请求");
        messageService.updateMessageConfig(messageConfigPojo);
        logger.info("消息配置更新成功");
        return Result.success(SuccessMsgEnums.SETTING_SUCCESS.getCode(), "消息配置更新成功");
    }
}