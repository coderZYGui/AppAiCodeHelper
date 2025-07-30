package com.sunny.appaicodehelper;

import com.sunny.appaicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AppAiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void testChat() {
        String prompt = "请帮我写一个Java程序，计算1到100的和";
        aiCodeHelper.chat(prompt);
    }

    @Test
    void testChatWithMessage() {
        aiCodeHelper.chatWithMessage(UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("https://profile-avatar.csdnimg.cn/4357218f50c14a2dbdb0396181d925b9_m0_37989980.jpg!1")
        ));
    }

    @Test
    void testChatWithSystemPrompt() {
        aiCodeHelper.chatWithSystemPrompt("你好, 我是雷军");
    }

    @Test
    void contextLoads() {
    }

}
