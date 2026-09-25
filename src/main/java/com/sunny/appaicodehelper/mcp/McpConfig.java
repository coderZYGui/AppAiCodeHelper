package com.sunny.appaicodehelper.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Author: guizy
 * Date: 2025/7/31
 * Description: 接入外部 MCP 服务(智谱 web_search), 为应用增强联网搜索能力。
 * <p>
 * 该能力依赖第三方(智谱)的 api-key, 与对话模型 DeepSeek 无关, 因此改为按需启用:
 * 仅当配置了 bigmodel.api-key 时才创建 Bean。未配置时应用照常启动, 只是没有 MCP 工具。
 * <p>
 * 注意: LangChain4j 1.20.0 已移除旧的 HttpMcpTransport(HTTP+SSE), 仅保留
 * StreamableHttpMcpTransport。若智谱侧仍是旧版 SSE 端点, 需要把 bigmodel.mcp.url
 * 指向其 Streamable HTTP 端点后再验证(本机无智谱 key, 该链路未做端到端验证)。
 */
@Configuration
public class McpConfig {

    @Value("${bigmodel.api-key:}")
    private String apiKey;

    @Value("${bigmodel.mcp.url:https://open.bigmodel.cn/api/mcp/web_search/sse}")
    private String mcpUrl;

    @Value("${bigmodel.mcp.timeout:30s}")
    private Duration timeout;

    /**
     * HTTP 层请求/响应日志。默认关闭: 智谱要求把 Authorization 放在 URL query 上,
     * 开启后密钥会被完整打印到日志中。
     */
    @Value("${bigmodel.mcp.logging:false}")
    private boolean logging;

    @Bean
    @ConditionalOnProperty(name = "bigmodel.api-key")
    public McpToolProvider mcpToolProvider() {
        // 和 MCP 服务通讯
        McpTransport transport = new StreamableHttpMcpTransport.Builder()
                .url(mcpUrl + "?Authorization=" + apiKey)
                .timeout(timeout)
                .logRequests(logging)
                .logResponses(logging)
                .build();
        // 创建 MCP 客户端
        McpClient mcpClient = new DefaultMcpClient.Builder()
                .key("yupiMcpClient")
                .transport(transport)
                .build();
        // 从 MCP 客户端获取工具
        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }
}
