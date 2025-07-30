package com.sunny.appaicodehelper.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description:
 */
@Configuration
public class RagConfig {

    @Resource
    private EmbeddingModel qwenEmbeddingModel;;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Bean
    public ContentRetriever contentRetriever() {
        // -----RAG-----
        //1. 加载我们添加的知识库
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
        //2. 文档切割: 将每个文档每段进行分割, 最大1000字符, 每次重叠最多200个字符
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(1000, 200);
        //3. 自定义文档加载器
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder().documentSplitter(splitter)
                // 为了提高搜索质量，为每个 TextSegment 添加文档名称
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))
                // 指定向量模型
                .embeddingModel(qwenEmbeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        // 加载文档
        ingestor.ingest(documents);

        //4. 创建内容检索器
        // 4. 自定义内容查询器
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(qwenEmbeddingModel)
                .maxResults(5) // 最多 5 个检索结果
                .minScore(0.75) // 过滤掉分数小于 0.75 的结果
                .build();

        return contentRetriever;
    }
}
