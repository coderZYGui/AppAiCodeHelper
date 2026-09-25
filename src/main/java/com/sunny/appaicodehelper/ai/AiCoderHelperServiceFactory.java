package com.sunny.appaicodehelper.ai;

import com.sunny.appaicodehelper.tools.InterviewQuestionTool;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: 用于生成 AiCoderHelperService 实例
 */

@Configuration // 在 AiCoderHelperService 接口上使用 @AiService 注解时, 这里注解要去掉
@Slf4j
public class AiCoderHelperServiceFactory {

    /** 每个会话保留的最大消息数 */
    private static final int MAX_MESSAGES = 10;

    @Bean
    public AiCoderHelperService aiCoderHelperService(ChatModel deepSeekChatModel,
                                                     StreamingChatModel deepSeekStreamingChatModel,
                                                     ObjectProvider<ContentRetriever> contentRetrieverProvider,
                                                     ObjectProvider<McpToolProvider> mcpToolProviderProvider) {
        AiServices<AiCoderHelperService> builder = AiServices.builder(AiCoderHelperService.class)
                .chatModel(deepSeekChatModel) // 对话模型: DeepSeek
                .streamingChatModel(deepSeekStreamingChatModel) // 流式LLM 支持 SSE流式输出, AI返回打字效果
                // 每个会话独立存储。设置 chatMemoryProvider 后 chatMemory 会被忽略, 故不再重复声明
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(MAX_MESSAGES))
                .tools(new InterviewQuestionTool()); // 添加工具

        // RAG 与 MCP 均为可选能力: 缺少对应的 key 时不装配, 不影响对话主链路
        ContentRetriever contentRetriever = contentRetrieverProvider.getIfAvailable();
        if (contentRetriever != null) {
            builder.contentRetriever(contentRetriever); // RAG检索增强生成
            log.info("RAG 已启用: 已装配知识库检索增强");
        } else {
            log.warn("RAG 未启用: 缺少向量模型配置 langchain4j.qwen.embedding.api-key");
        }

        McpToolProvider mcpToolProvider = mcpToolProviderProvider.getIfAvailable();
        if (mcpToolProvider != null) {
            builder.toolProvider(mcpToolProvider); // 添加MCP工具
            log.info("MCP 已启用: 已装配外部 MCP 工具");
        } else {
            log.warn("MCP 未启用: 缺少 bigmodel.api-key");
        }

        return builder.build();
    }
}
