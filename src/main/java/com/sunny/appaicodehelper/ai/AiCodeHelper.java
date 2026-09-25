package com.sunny.appaicodehelper.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Author: guizy
 * Date: 2025/7/30
 * Description: 直接使用 ChatModel 的基础调用示例, 作为 AiServices 之外的原生用法对照。
 */

@Service
@Slf4j
public class AiCodeHelper {

    @Resource
    private ChatModel deepSeekChatModel;

    private static final String SYSTEM_PROMPT = """
            你是编程领域的小助手，帮助用户解答编程学习和求职面试相关的问题，并给出建议。重点关注 4 个方向：
            1. 规划清晰的编程学习路线
            2. 提供项目学习建议
            3. 给出程序员求职全流程指南（比如简历优化、投递技巧）
            4. 分享高频面试题和面试技巧
            请用简洁易懂的语言回答，助力用户高效学习与求职。
            """;

    public String chat(String prompt) {
        // 将输入的prompt转换为UserMessage对象
        return chatWithMessage(UserMessage.from(prompt));
    }

    public String chatWithMessage(UserMessage userMessage) {
        // 使用 deepSeekChatModel 对象进行聊天
        ChatResponse chatResponse = deepSeekChatModel.chat(userMessage);
        // 获取聊天结果中的AI消息
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("chatWithMessage AI 输出: {}", aiMessage.text());
        return aiMessage.text();
    }

    public String chatWithSystemPrompt(String prompt) {
        ChatResponse chatResponse = deepSeekChatModel.chat(
                SystemMessage.from(SYSTEM_PROMPT), UserMessage.from(prompt));
        AiMessage aiMessage = chatResponse.aiMessage();
        log.info("chatWithSystemPrompt AI 输出: {}", aiMessage.text());
        return aiMessage.text();
    }
}
