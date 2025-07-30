<template>
  <div class="app-container">
    <!-- 头部 -->
    <header class="header">
      <div class="header-content">
        <h1 class="title">
          <span class="ai-icon">🤖</span>
          AI编程小助手
        </h1>
                 <div class="session-info">
           <span class="session-id">会话ID: {{ sessionId }}</span>
           <span class="memory-id">MemoryID: {{ memoryId }}</span>
         </div>
      </div>
    </header>

    <!-- 聊天区域 -->
    <main class="chat-container">
      <div class="chat-messages" ref="messagesContainer">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-content">
            <h2>👋 欢迎使用AI编程小助手</h2>
            <p>我可以帮助您解答编程学习和求职面试相关的问题</p>
            <div class="suggestions">
              <h3>💡 您可以问我：</h3>
              <ul>
                <li>编程语言学习建议</li>
                <li>算法和数据结构问题</li>
                <li>面试题解答</li>
                <li>代码优化建议</li>
                <li>技术选型指导</li>
              </ul>
            </div>
          </div>
        </div>
        
        <div 
          v-for="(message, index) in messages" 
          :key="index"
          :class="['message', message.type]"
        >
          <div class="message-content">
            <div class="message-avatar">
              <span v-if="message.type === 'ai'">🤖</span>
              <span v-else>👤</span>
            </div>
            <div class="message-bubble">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 正在输入指示器 -->
        <div v-if="isTyping" class="message ai">
          <div class="message-content">
            <div class="message-avatar">
              <span>🤖</span>
            </div>
            <div class="message-bubble">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 输入区域 -->
    <footer class="input-container">
      <div class="input-wrapper">
        <textarea
          v-model="inputMessage"
          @keydown.enter.prevent="sendMessage"
          @keydown.ctrl.enter="sendMessage"
          placeholder="输入您的问题，按Enter发送，Ctrl+Enter换行..."
          class="message-input"
          :disabled="isTyping"
          ref="messageInput"
        ></textarea>
        <button 
          @click="sendMessage" 
          class="send-button"
          :disabled="!inputMessage.trim() || isTyping"
        >
          <span v-if="!isTyping">发送</span>
          <span v-else>发送中...</span>
        </button>
      </div>
    </footer>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'

export default {
  name: 'App',
  setup() {
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const sessionId = ref('')
    const memoryId = ref(0)
    const messagesContainer = ref(null)
    const messageInput = ref(null)

    // 生成会话ID
    const generateSessionId = () => {
      const timestamp = Date.now()
      const random = Math.random().toString(36).substring(2, 8)
      return `${timestamp}-${random}`
    }

    // 生成数字格式的memoryId（用于后端接口）
    const generateMemoryId = () => {
      return Math.floor(Math.random() * 1000000) + 1
    }

    // 格式化消息内容（支持换行）
    const formatMessage = (content) => {
      return content.replace(/\n/g, '<br>')
    }

    // 格式化时间
    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      return date.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // 滚动到底部
    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 发送消息
    const sendMessage = async () => {
      const message = inputMessage.value.trim()
      if (!message || isTyping.value) return

      // 添加用户消息
      messages.value.push({
        type: 'user',
        content: message,
        timestamp: Date.now()
      })

      inputMessage.value = ''
      await scrollToBottom()

      // 开始AI回复
      isTyping.value = true
      await scrollToBottom()

      try {
        // 使用SSE调用后端接口
        const eventSource = new EventSource(
          `http://localhost:8081/api/ai/chat?memoryId=${memoryId.value}&message=${encodeURIComponent(message)}`
        )

        let aiResponse = ''
        let aiMessageIndex = messages.value.length

        // 添加AI消息占位符
        messages.value.push({
          type: 'ai',
          content: '',
          timestamp: Date.now()
        })

        eventSource.onmessage = (event) => {
          aiResponse += event.data
          messages.value[aiMessageIndex].content = aiResponse
          scrollToBottom()
        }

        eventSource.onerror = (error) => {
          console.error('SSE Error:', error)
          eventSource.close()
          isTyping.value = false
          
          // 如果AI没有回复，显示错误消息
          if (!aiResponse) {
            messages.value[aiMessageIndex].content = '抱歉，我遇到了一些问题，请稍后再试。'
          }
        }

        eventSource.addEventListener('end', () => {
          eventSource.close()
          isTyping.value = false
        })

      } catch (error) {
        console.error('发送消息失败:', error)
        isTyping.value = false
        
        // 添加错误消息
        messages.value.push({
          type: 'ai',
          content: '抱歉，网络连接出现问题，请检查网络后重试。',
          timestamp: Date.now()
        })
      }
    }

    // 监听消息变化，自动滚动
    watch(messages, () => {
      scrollToBottom()
    }, { deep: true })

    onMounted(() => {
      // 生成会话ID和memoryId
      sessionId.value = generateSessionId()
      memoryId.value = generateMemoryId()
      
      // 聚焦输入框
      if (messageInput.value) {
        messageInput.value.focus()
      }
    })

    return {
      messages,
      inputMessage,
      isTyping,
      sessionId,
      memoryId,
      messagesContainer,
      messageInput,
      sendMessage,
      formatMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 1800px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 50px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-icon {
  font-size: 28px;
}

.session-info {
  font-size: 14px;
  opacity: 0.9;
}

 .session-id {
   background: rgba(255, 255, 255, 0.2);
   padding: 6px 12px;
   border-radius: 20px;
   font-family: monospace;
   margin-right: 10px;
 }

 .memory-id {
   background: rgba(255, 255, 255, 0.2);
   padding: 6px 12px;
   border-radius: 20px;
   font-family: monospace;
 }

.chat-container {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.chat-messages {
  height: 100%;
  overflow-y: auto;
  padding: 40px;
  scroll-behavior: smooth;
}

.welcome-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  text-align: center;
}

.welcome-content {
  max-width: 700px;
  padding: 40px;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 20px;
  border: 2px dashed rgba(102, 126, 234, 0.3);
}

.welcome-content h2 {
  color: #667eea;
  margin-bottom: 15px;
  font-size: 28px;
}

.welcome-content p {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.suggestions h3 {
  color: #667eea;
  margin-bottom: 15px;
  font-size: 18px;
}

.suggestions ul {
  list-style: none;
  text-align: left;
}

.suggestions li {
  color: #666;
  margin: 8px 0;
  padding: 8px 15px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  border-left: 4px solid #667eea;
}

.message {
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease-in;
}

.message.user {
  display: flex;
  justify-content: flex-end;
}

.message.ai {
  display: flex;
  justify-content: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 85%;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message.ai .message-avatar {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.message-bubble {
  background: white;
  padding: 20px 25px;
  border-radius: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.message.ai .message-bubble {
  background: white;
  color: #333;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.message-time {
  font-size: 12px;
  opacity: 0.7;
  margin-top: 8px;
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 10px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: -0.16s;
}

.input-container {
  padding: 30px 50px;
  background: white;
  border-top: 1px solid #eee;
}

.input-wrapper {
  display: flex;
  gap: 15px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  min-height: 50px;
  max-height: 120px;
  padding: 15px 20px;
  border: 2px solid #e1e5e9;
  border-radius: 25px;
  font-size: 16px;
  font-family: inherit;
  resize: none;
  outline: none;
  transition: border-color 0.3s ease;
}

.message-input:focus {
  border-color: #667eea;
}

.message-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.send-button {
  padding: 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 100px;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-container {
    border-radius: 0;
    height: 100vh;
  }
  
  .header {
    padding: 25px 30px;
  }
  
  .title {
    font-size: 20px;
  }
  
  .session-info {
    display: none;
  }
  
  .chat-messages {
    padding: 25px;
  }
  
  .message-content {
    max-width: 95%;
  }
  
  .input-container {
    padding: 25px 30px;
  }
  
  .input-wrapper {
    gap: 10px;
  }
  
  .send-button {
    padding: 15px 20px;
    min-width: 80px;
  }
}
</style> 