package com.sunny.appaicodehelper;

import com.sunny.appaicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppAiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void testChat() {
        String prompt = "请帮我写一个Java程序，计算1到100的和";
        String result = aiCodeHelper.chat(prompt);
        System.out.println(result);
        assertThat(result).isNotBlank();
    }

    /** DeepSeek 的 deepseek-flash 支持图片输入(input_modalities 含 image) */
    @Test
    void testChatWithMessage() {
        String result = aiCodeHelper.chatWithMessage(UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("https://profile-avatar.csdnimg.cn/4357218f50c14a2dbdb0396181d925b9_m0_37989980.jpg!1")
        ));
        System.out.println(result);
        assertThat(result).isNotBlank();
    }

    @Test
    void testChatWithSystemPrompt() {
        String result = aiCodeHelper.chatWithSystemPrompt("你好, 我是雷军");
        System.out.println(result);
        assertThat(result).isNotBlank();
    }

    @Test
    void contextLoads() {
    }

}
