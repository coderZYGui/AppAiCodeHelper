package com.sunny.appaicodehelper.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */

@Configuration // 在@AiCoderHelperService接口上AiService注解, 这里注解要去掉
public class AiCoderHelperServiceFactory {


    @Resource
    private ChatModel qwenChatModel;

    @Bean
    public AiCoderHelperService createAiCoderHelperService() {
        return AiServices.create(AiCoderHelperService.class, qwenChatModel);
    }

}
