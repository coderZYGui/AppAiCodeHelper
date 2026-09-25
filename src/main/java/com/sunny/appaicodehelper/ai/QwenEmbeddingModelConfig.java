package com.sunny.appaicodehelper.ai;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: RAG 向量模型配置。
 * <p>
 * 对话模型已切换到 DeepSeek, 但 DeepSeek 未提供 embeddings 接口(/v1/embeddings 返回 404),
 * 故向量化仍使用 Qwen text-embedding-v4。
 * <p>
 * 未配置 api-key 时不创建该 Bean: 应用照常启动, 仅 RAG 检索能力关闭, 不再因缺 key 导致启动失败。
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "langchain4j.qwen.embedding")
public class QwenEmbeddingModelConfig {

    private String apiKey;

    private String modelName = "text-embedding-v4";

    /** 向量维度, 留空使用模型默认(text-embedding-v4 默认 1024) */
    private Integer dimension;

    @Bean
    @ConditionalOnProperty(prefix = "langchain4j.qwen.embedding", name = "api-key")
    public EmbeddingModel qwenEmbeddingModel() {
        QwenEmbeddingModel.QwenEmbeddingModelBuilder builder = QwenEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName(modelName);
        if (dimension != null) {
            builder.dimension(dimension);
        }
        return builder.build();
    }
}
