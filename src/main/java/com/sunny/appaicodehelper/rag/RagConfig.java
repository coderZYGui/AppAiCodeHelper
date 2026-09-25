package com.sunny.appaicodehelper.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 * RAG 全流程: 加载知识库 -> 文档切割 -> 向量化入库 -> 提供内容检索器。
 * <p>
 * 仅在配置了向量模型(langchain4j.qwen.embedding.api-key)时生效; 否则整条 RAG 链路关闭, 应用仍可正常启动。
 */
@Configuration
@Data
@Slf4j
@ConfigurationProperties(prefix = "langchain4j.rag")
public class RagConfig {

    /** 知识库在 classpath 下的位置 */
    private String docsLocation = "docs";

    /** 单个分段最大字符数 */
    private int maxSegmentSize = 1000;

    /** 分段之间的重叠字符数 */
    private int maxOverlapSize = 200;

    /** 每次检索返回的最大结果数 */
    private int maxResults = 5;

    /**
     * 相似度下限, 低于该分数的结果会被过滤(分数越高越相关)。
     * 该阈值与向量模型强相关: 更换 embedding 模型后需重新标定。
     */
    private double minScore = 0.8;

    /**
     * 向量库。原实现依赖 starter 自动装配, 现已改为显式声明, 避免隐式依赖。
     * 内存实现适用于当前演示规模; 生产环境应替换为持久化向量库(如 Milvus/PgVector)。
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    @ConditionalOnProperty(prefix = "langchain4j.qwen.embedding", name = "api-key")
    public ContentRetriever contentRetriever(EmbeddingModel embeddingModel,
                                             EmbeddingStore<TextSegment> embeddingStore) {
        // -----一、RAG, 文档收集和切割-----
        // 从 classpath 加载: 打成 jar 后依然可用。
        // 原实现使用 FileSystemDocumentLoader + "src/main/resources/docs" 相对路径, 仅在 IDE 工作目录下成立。
        List<Document> documents = ClassPathDocumentLoader.loadDocuments(docsLocation);
        if (documents.isEmpty()) {
            log.warn("RAG: classpath:{} 下未加载到任何文档, 检索结果将为空", docsLocation);
        } else {
            log.info("RAG: 从 classpath:{} 加载到 {} 个文档", docsLocation, documents.size());
        }

        // -----二、RAG, 文档加载器将文档转为向量, 向量存储-----
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                // 文档切割: 按段落分割, 最大 maxSegmentSize 字符, 每次重叠 maxOverlapSize 个字符
                .documentSplitter(new DocumentByParagraphSplitter(maxSegmentSize, maxOverlapSize))
                // 为提高搜索质量, 为每个 TextSegment 追加文档名称
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(documents);

        // -----三、RAG, 文档过滤和检索(检索条件)-----
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(maxResults)
                .minScore(minScore)
                .build();
    }
}
