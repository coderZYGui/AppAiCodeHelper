# AI编程小助手

一个基于Vue3的AI编程助手前端应用，帮助用户解答编程学习和求职面试相关的问题。

## 功能特性

- 🤖 智能AI对话：基于SSE的实时流式对话
- 💬 聊天室界面：现代化的聊天界面设计
- 📱 响应式设计：支持桌面端和移动端
- 🎨 美观UI：渐变背景和现代化设计
- ⚡ 实时响应：支持流式输出，实时显示AI回复
- 🔄 会话管理：自动生成会话ID，区分不同对话

## 技术栈

- **前端框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **HTTP客户端**: Axios
- **样式**: CSS3 (原生样式，无UI框架依赖)
- **实时通信**: Server-Sent Events (SSE)

## 项目结构

```
ai-code-helper/
├── src/
│   ├── App.vue          # 主应用组件
│   ├── main.js          # 应用入口
│   └── style.css        # 全局样式
├── index.html           # HTML模板
├── package.json         # 项目配置
├── vite.config.js       # Vite配置
└── README.md           # 项目说明
```

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 后端接口

项目需要配合SpringBoot后端使用，后端接口信息：

- **接口地址**: `http://localhost:8081/api/ai/chat`
- **请求方式**: GET
- **参数**:
  - `memoryId`: 内存ID (int) - 用于后端会话管理
  - `message`: 用户消息 (String)
- **返回格式**: Server-Sent Events (SSE)

### 后端接口示例

```java
@RestController
@RequestMapping("/ai")
public class AiController {

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(int memoryId, String message) {
        return aiCodeHelperService.chatStream(memoryId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
```

## 功能说明

### 聊天界面

- **用户消息**: 显示在右侧，蓝色渐变背景
- **AI回复**: 显示在左侧，白色背景
- **实时输入**: 支持流式输出，实时显示AI回复内容
- **输入提示**: 显示"正在输入"动画效果

### 会话管理

- 每次进入页面自动生成唯一的会话ID和内存ID
- 会话ID和内存ID显示在页面头部
- 内存ID用于后端会话管理，会话ID用于前端显示
- 支持区分不同的对话会话

### 交互功能

- **发送消息**: 按Enter键或点击发送按钮
- **换行输入**: 按Ctrl+Enter键
- **自动滚动**: 新消息自动滚动到底部
- **响应式设计**: 适配不同屏幕尺寸

## 自定义配置

### 修改后端接口地址

在 `src/App.vue` 文件中修改SSE连接地址：

```javascript
const eventSource = new EventSource(
  `http://localhost:8081/api/ai/chat?memoryId=${sessionId.value}&message=${encodeURIComponent(message)}`
)
```

### 修改样式

- 全局样式在 `src/style.css`
- 组件样式在 `src/App.vue` 的 `<style>` 部分

## 浏览器兼容性

- Chrome 60+
- Firefox 55+
- Safari 11+
- Edge 79+

## 开发说明

### 开发环境要求

- Node.js 16+
- npm 8+

### 开发命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 部署

### 静态部署

1. 构建项目：`npm run build`
2. 将 `dist` 目录部署到Web服务器
3. 确保后端服务正常运行在 `http://localhost:8081`

### Docker部署

可以配合Docker进行容器化部署，需要配置nginx等Web服务器。

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。 