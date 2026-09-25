package com.sunny.appaicodehelper.ai;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.Result;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: 以下均为集成测试, 会真实调用 DeepSeek 接口。
 * RAG / MCP 属于可选能力, 未配置对应 key 时对应用例自动跳过, 不视为失败。
 */

@SpringBootTest
class AiCoderHelperServiceTest {

    /**
     * 通过 AiCoderHelperServiceFactory 工厂来创建的, 并注入进来
     */
    @Resource
    private AiCoderHelperService aiCoderHelperService;

    @Resource
    private ApplicationContext applicationContext;

    private boolean configured(Class<?> type) {
        return applicationContext.getBeanNamesForType(type).length > 0;
    }

    @Test
    void chat() {
        String prompt = "你好，我想学习Java编程，你能给我一些建议吗？";
        String chat = aiCoderHelperService.chat(prompt);
        System.out.println(chat);
        assertThat(chat).isNotBlank();
    }

    @Test
    void chatWithMemory() {
        String result = aiCoderHelperService.chat("你好, 我是雷军");
        System.out.println(result);
        assertThat(result).isNotBlank();
        // 同一会话(默认 memoryId)应能记住上文
        result = aiCoderHelperService.chat("我是谁, 你知道吗!");
        System.out.println(result);
        assertThat(result).contains("雷军");
    }

    @Test
    void chatForReport() {
        AiCoderHelperService.Report report = aiCoderHelperService.chatForReport("帮我指定一个学习计划");
        System.out.println(report);
        assertThat(report).isNotNull();
        assertThat(report.suggestionList()).isNotEmpty();
    }

    @Test // 测试RAG检索增强生成
    void chatForRAG() {
        assumeTrue(configured(ContentRetriever.class),
                "未配置 langchain4j.qwen.embedding.api-key, 跳过 RAG 测试");
        Result<String> result = aiCoderHelperService.chatWithRag("在学习Java项目开发中, 有什么学习建议?");
        System.out.println(result.content());
        System.out.println(result.sources());
        assertThat(result.content()).isNotBlank();
    }

    @Test
    void chatWithTools() {
        String result = aiCoderHelperService.chat("有哪些常见的计算机网络面试题？");
        System.out.println(result);
        assertThat(result).isNotBlank();
    }

    @Test
    void chatWithMcp() {
        assumeTrue(configured(McpToolProvider.class),
                "未配置 bigmodel.api-key, 跳过 MCP 测试");
        String result = aiCoderHelperService.chat("CSDN 白骆驼");
        System.out.println(result);
        assertThat(result).isNotBlank();
    }

    /**
     * 护航验证: 命中敏感词时应在调用 LLM 前被拦截并抛错;
     * 正常输入应放行。原用例传的是无敏感词的输入, 实际并未覆盖拦截分支。
     */
    @Test
    void chatWithGuardrail() {
        assertThatThrownBy(() -> aiCoderHelperService.chat("please kill the process"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("kill");
        System.out.println("敏感词已被拦截");

        String result = aiCoderHelperService.chat("我是谁, 我在哪");
        System.out.println(result);
        assertThat(result).isNotBlank();
    }
}
