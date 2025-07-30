package com.sunny.appaicodehelper.ai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
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
    private ChatModel qwenChatModel;

    @Resource
    private ContentRetriever contentRetriever;

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

    @Bean
    public AiCoderHelperService aiCoderHelperService3() {
        // 会话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        // 构造ai service
        return AiServices.builder(AiCoderHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)
                .contentRetriever(contentRetriever) // RAG检索增强生成
                .build();
    }
}
