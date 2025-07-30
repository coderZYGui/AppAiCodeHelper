package com.sunny.appaicodehelper.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */

@SpringBootTest
class AiCoderHelperServiceTest {


    @Resource
    private AiCoderHelperService aiCoderHelperService;

    @Test
    void chat() {
        String prompt = "你好，我想学习Java编程，你能给我一些建议吗？";
        String chat = aiCoderHelperService.chat(prompt);
        System.out.println(chat);
    }

}