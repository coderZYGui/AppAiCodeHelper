package com.sunny.appaicodehelper.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: DeepSeek 对话模型配置, 绑定 listener。
 * <p>
 * DeepSeek 提供 OpenAI 兼容接口, 因此复用 LangChain4j 的 OpenAiChatModel / OpenAiStreamingChatModel,
 * 仅替换 baseUrl 与 modelName, 无需专用 SDK。
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.deepseek")
public class DeepSeekChatModelConfig {

    /** DeepSeek OpenAI 兼容端点 */
    private String baseUrl = "https://api.deepseek.com/v1";

    private String apiKey;

    /** deepseek-flash 支持 function calling; 推理模型(如 deepseek-reasoner 线)不支持工具调用 */
    private String chatModelName = "deepseek-flash";

    /** 流式模型名, 留空则复用 chatModelName */
    private String streamingModelName;

    private Duration timeout = Duration.ofSeconds(60);

    /**
     * 推理强度: low / high / max, 留空使用服务端默认(high)。
     * 该模型为推理模型, reasoning token 会占用输出预算并带来首字延迟;
     * 若追求打字机式的即时响应, 可下调为 low。
     */
    private String reasoningEffort;

    @Resource
    private ChatModelListener chatModelListener;

    @Bean
    public ChatModel deepSeekChatModel() {
        // 对话模型是核心能力, 缺 key 时快速失败并给出可操作的提示, 而不是静默降级
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("""
                    缺少 DeepSeek api-key, 无法创建对话模型。
                    请在 src/main/resources/application-local.yml 中配置:
                        langchain4j.deepseek.api-key: sk-xxx
                    或设置环境变量 DEEPSEEK_API_KEY。参考 application-local.yml.example。""");
        }
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(chatModelName)
                .timeout(timeout)
                // DeepSeek 仅支持 response_format=json_object, 不支持 json_schema
                // (请求 json_schema 会返回 "This response_format type is unavailable now")。
                // 因此显式声明"不支持任何可选能力"(空集合), 使结构化输出回退到 json_object 方案。
                .supportedCapabilities(Set.of())
                // HTTP 层日志关闭: 避免 Authorization 头与对话内容落入日志, 观测交给 ChatModelListener
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(chatModelListener));
        if (StringUtils.hasText(reasoningEffort)) {
            builder.reasoningEffort(reasoningEffort);
        }
        return builder.build();
    }

    @Bean
    public StreamingChatModel deepSeekStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(StringUtils.hasText(streamingModelName) ? streamingModelName : chatModelName)
                .timeout(timeout)
                .logRequests(false)
                .logResponses(false)
                .listeners(List.of(chatModelListener))
                .build();
    }
}
