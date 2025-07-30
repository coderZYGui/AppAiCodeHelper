package com.sunny.appaicodehelper.ai;

import com.sunny.appaicodehelper.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: 用于生成AiCoderHelperService实例
 */

@Configuration // 在@AiCoderHelperService接口上AiService注解, 这里注解要去掉
public class AiCoderHelperServiceFactory {


    @Resource
//    private ChatModel qwenChatModel;
    private ChatModel myQwenChatModel; // 使用自定义的Qwen大模型

    @Resource
    private ContentRetriever contentRetriever;

    @Resource
    private McpToolProvider mcpToolProvider;

    @Resource
    private StreamingChatModel streamingChatModel;

//    @Bean
//    public AiCoderHelperService createAiCoderHelperService() {
//        return AiServices.create(AiCoderHelperService.class, qwenChatModel);
//    }

    /**
     * 使用LongChain4j的会话记忆
     * @return
     */
//    @Bean
//    public AiCoderHelperService aiCoderHelperService2() {
//        // 会话记忆
//        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
//
//        return AiServices.builder(AiCoderHelperService.class)
//                .chatModel(qwenChatModel)
//                .chatMemory(chatMemory)
//                .build();
//    }


    // -----四、RAG, 关联LLM、RAG,使查询增强-----
//    @Bean
//    public AiCoderHelperService aiCoderHelperService3() {
//        // 会话记忆
//        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
//        // 构造ai service
//        return AiServices.builder(AiCoderHelperService.class)
//                .chatModel(qwenChatModel) // 使用Qwen大语言模型
//                .chatMemory(chatMemory)
//                .contentRetriever(contentRetriever) // RAG检索增强生成
//                .build();
//    }

    @Bean
    public AiCoderHelperService aiCoderHelperService() {
        // 会话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造ai service
        return AiServices.builder(AiCoderHelperService.class)
                .chatModel(myQwenChatModel) // 使用Qwen大语言模型
                .chatMemory(chatMemory)
                .streamingChatModel(streamingChatModel) // 流式LLM 支持 SSE流式输出, AI返回打字效果
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10)) //每个会话独立存储
                .contentRetriever(contentRetriever) // RAG检索增强生成
                .tools(new InterviewQuestionTool()) // 添加工具
                .toolProvider(mcpToolProvider) // 添加MCP工具
                .build();
    }
}
