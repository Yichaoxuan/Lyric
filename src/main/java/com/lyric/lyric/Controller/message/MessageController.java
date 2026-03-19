package com.lyric.lyric.Controller.message;

import com.lyric.lyric.Service.message.MessageService;
import com.lyric.lyric.Utils.resultUtils.Result;
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
     * 更新消息配置并保存到配置文件
     * @param responseStyleInstructions 响应消息的角色设定
     * @return 更新结果
     */
    @PutMapping("/updateMessageConfig")
    public Result<Void> updateMessageConfigAndSave(@RequestParam String responseStyleInstructions) {
        logger.info("收到更新消息配置并保存到文件的请求");
        return messageService.updateMessageConfigAndSaveToFile(responseStyleInstructions);
    }
}