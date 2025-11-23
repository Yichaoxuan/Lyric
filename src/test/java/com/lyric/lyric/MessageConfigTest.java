package com.lyric.lyric;

import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 消息配置测试类
 * 用于测试消息配置是否正确加载和注入
 */
@SpringBootTest
public class MessageConfigTest {

    @Test
    public void testSuccessMessageInjection() {
        // 测试成功消息是否正确注入
        SuccessMsgEnums saveSuccess = SuccessMsgEnums.SAVE_SUCCESS;
        assertNotNull(saveSuccess.getCode());
        assertNotNull(saveSuccess.getMessage());
        assertEquals("200", saveSuccess.getCode());
        assertEquals("保存成功", saveSuccess.getMessage());
    }
}