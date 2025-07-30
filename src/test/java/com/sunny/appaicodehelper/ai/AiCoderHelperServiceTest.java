package com.sunny.appaicodehelper.ai;

import dev.langchain4j.service.Result;
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

    @Test
    void chatWithMemory() {
        String result = aiCoderHelperService.chat("你好, 我是雷军");
        System.out.println(result);
        result = aiCoderHelperService.chat("我是谁, 你知道吗");
        System.out.println(result);
    }

    @Test
    void chatForReport() {
        AiCoderHelperService.Report report = aiCoderHelperService.chatForReport("帮我指定一个学习计划");
        System.out.println(report);
    }

    @Test // 测试RAG检索增强生成
    void chatForRAG() {
        Result<String> result = aiCoderHelperService.chatWithRag("怎么学习 Java？有哪些常见面试题？");
        System.out.println(result.content());
        System.out.println(result.sources());
    }
}