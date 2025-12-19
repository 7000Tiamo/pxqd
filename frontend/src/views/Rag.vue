<template>
  <div class="rag-container">
    <div class="page-head">
      <div>
        <h2 class="page-title">智能问答助手</h2>
        <div class="page-subtitle">面向考勤与培训制度的智能问答，支持连续对话</div>
      </div>
    </div>

    <el-row :gutter="20" class="rag-grid">
        <el-col :span="18" class="chat-col">
          <el-card class="chat-card">
            <div class="chat-header">
              <div class="chat-header-left">
                <div class="bot-badge">AI</div>
                <div class="chat-header-text">
                  <div class="chat-header-title">考勤助手</div>
                  <div class="chat-header-desc">输入问题后点击发送，获取基于制度的答案</div>
                </div>
              </div>
              <div class="chat-header-right">
                <div class="chat-status" :class="{ loading }">
                  <span class="dot" />
                  <span>{{ loading ? '思考中' : '在线' }}</span>
                </div>
              <el-button size="small" @click="clearChat">清空对话</el-button>
              </div>
            </div>

            <div class="chat-messages" ref="messagesContainer">
              <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.type]">
                <div class="message-row">
                  <div class="avatar" :class="msg.type">{{ msg.type === 'user' ? '我' : 'AI' }}</div>
                  <div class="bubble">
                    <div class="message-content">{{ msg.content }}</div>
                    <div class="message-time">{{ msg.time }}</div>
                  </div>
                </div>
              </div>

              <div v-if="loading" class="message bot">
                <div class="message-row">
                  <div class="avatar bot">AI</div>
                  <div class="bubble">
                    <div class="message-content thinking">
                      <span class="typing-dot" />
                      <span class="typing-dot" />
                      <span class="typing-dot" />
                      <span class="thinking-text">正在思考中...</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="chat-input-area">
              <el-input
                v-model="inputMessage"
                class="chat-input"
                type="textarea"
                :rows="3"
                resize="none"
                placeholder="输入问题，例如：公司的考勤方式是什么？"
                @keydown.enter.prevent="handleSend"
                :disabled="loading"
              />
              <div class="input-actions">
                <div class="hint">Enter 发送（可按住 Shift 换行）</div>
                <div class="actions-right">
                  <el-button type="primary" @click="handleSend" :loading="loading" :disabled="!inputMessage.trim()">
                    发送
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="6" class="side-col">
          <el-card class="side-card">
            <template #header>
              <div class="side-header">
                <span class="side-title">参考文档</span>
                <span class="side-tip">用于回答的制度依据</span>
              </div>
            </template>
            <ul class="doc-list">
              <li class="doc-item">
                <div class="doc-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="doc-meta">
                  <div class="doc-name">员工考勤管理制度</div>
                  <div class="doc-sub">PDF · 版本 1.0</div>
                </div>
              </li>
              <li class="doc-item">
                <div class="doc-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="doc-meta">
                  <div class="doc-name">培训积分管理办法</div>
                  <div class="doc-sub">DOCX · 版本 1.0</div>
                </div>
              </li>
            </ul>
          </el-card>
        </el-col>
      </el-row>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import dayjs from 'dayjs'

const authStore = useAuthStore()

const messages = ref([
  {
    type: 'bot',
    content: '你好！我是你的考勤助手，有问题请随时问我。例如："公司的考勤方式是什么？ 上下班时间是多久？"',
    time: dayjs().format('HH:mm:ss')
  }
])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const conversationId = ref(null)

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const handleSend = async () => {
  const question = inputMessage.value.trim()
  if (!question || loading.value) return

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: question,
    time: dayjs().format('HH:mm:ss')
  })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    // 调用外部智能体接口（阻塞式）
    const response = await fetch('https://agent.thunisoft.com/v1/chat-messages', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer app-R6Waa5Yagch1QVGuFFODqbBR'
      },
      body: JSON.stringify({
        query: question,
        user: authStore?.user?.username || authStore?.user?.id || 'anonymous',
        inputs: {},
        response_mode: 'blocking'
      })
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP error! status: ${response.status}, ${errorText}`)
    }

    const data = await response.json()

    // 兼容不同返回字段（常见：answer / message / data.answer）
    const answer =
      data?.answer ??
      data?.message ??
      data?.data?.answer ??
      data?.data?.message ??
      ''

    if (data?.conversation_id) {
      conversationId.value = data.conversation_id
    }

    messages.value.push({
      type: 'bot',
      content: answer || '（未收到有效回复）',
      time: dayjs().format('HH:mm:ss')
    })
    loading.value = false
    scrollToBottom()
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error(error.message || '发送消息失败，请稍后重试')
    loading.value = false
    
    messages.value.push({
      type: 'bot',
      content: '抱歉，服务暂时不可用，请稍后重试。',
      time: dayjs().format('HH:mm:ss')
    })
  }
}

const clearChat = () => {
  conversationId.value = null
  messages.value = [{
    type: 'bot',
    content: '你好！我是你的考勤助手，有问题请随时问我。例如："迟到怎么算？"',
    time: dayjs().format('HH:mm:ss')
  }]
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.rag-page {
  min-height: calc(100vh - 120px);
  padding: 20px 0 28px;
  background:
    radial-gradient(1200px 500px at 15% 0%, rgba(64, 158, 255, 0.18), transparent 60%),
    radial-gradient(900px 420px at 85% 5%, rgba(147, 197, 253, 0.22), transparent 55%),
    linear-gradient(180deg, #f6f8fc 0%, #ffffff 60%, #ffffff 100%);
}

.rag-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 10px;
}

.page-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  padding: 18px 18px 16px;
  margin-bottom: 16px;
  border-radius: 16px;
  color: #0f172a;
  background:
    linear-gradient(135deg, rgba(64, 158, 255, 0.22) 0%, rgba(99, 102, 241, 0.12) 40%, rgba(255, 255, 255, 0.65) 100%),
    rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(148, 163, 184, 0.28);
  backdrop-filter: blur(8px);
}

.hero-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.2px;
}

.hero-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: rgba(15, 23, 42, 0.7);
}

.hero-btn {
  border-radius: 10px;
}

.chat-card {
  height: calc(100vh - 230px);
  display: flex;
  flex-direction: column;
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid rgba(148, 163, 184, 0.22);
}

.chat-header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.chat-header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 0 0 auto;
}

.bot-badge {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-weight: 700;
  color: #0b4aa2;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.25), rgba(99, 102, 241, 0.18));
  border: 1px solid rgba(64, 158, 255, 0.35);
}

.chat-header-text {
  min-width: 0;
}

.chat-header-title {
  font-size: 14px;
  font-weight: 700;
  color: #0f172a;
}

.chat-header-desc {
  margin-top: 2px;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.62);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 760px;
}

.chat-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  color: rgba(15, 23, 42, 0.7);
  background: rgba(15, 23, 42, 0.04);
  border: 1px solid rgba(148, 163, 184, 0.22);
}

.chat-status .dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #22c55e;
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.12);
}

.chat-status.loading .dot {
  background: #f59e0b;
  box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.16);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 18px 16px;
  background:
    radial-gradient(900px 320px at 30% 0%, rgba(64, 158, 255, 0.08), transparent 55%),
    linear-gradient(180deg, rgba(248, 250, 252, 0.85), rgba(255, 255, 255, 0.92));
}

.message {
  margin-bottom: 14px;
  display: flex;
  flex-direction: column;
}

.message.user {
  align-items: flex-end;
}

.message.bot {
  align-items: flex-start;
}

.message-row {
  display: flex;
  gap: 10px;
  max-width: 100%;
}

.message.user .message-row {
  flex-direction: row-reverse;
}

.avatar {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 12px;
  flex: 0 0 auto;
  user-select: none;
}

.avatar.user {
  color: #ffffff;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  box-shadow: 0 10px 18px rgba(37, 99, 235, 0.18);
}

.avatar.bot {
  color: #0b4aa2;
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.22), rgba(99, 102, 241, 0.16));
  border: 1px solid rgba(64, 158, 255, 0.3);
}

.bubble {
  max-width: min(760px, calc(100% - 44px));
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-content {
  padding: 12px 14px;
  border-radius: 14px;
  word-wrap: break-word;
  line-height: 1.7;
  font-size: 14px;
  white-space: pre-wrap;
}

.message.user .message-content {
  background: linear-gradient(135deg, #409eff, #2563eb);
  color: #ffffff;
  border-bottom-right-radius: 6px;
  box-shadow: 0 14px 26px rgba(37, 99, 235, 0.18);
}

.message.bot .message-content {
  background: rgba(255, 255, 255, 0.92);
  color: #0f172a;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-bottom-left-radius: 6px;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.06);
}

.message-time {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.45);
  padding: 0 6px;
}

.chat-input-area {
  border-top: 1px solid rgba(148, 163, 184, 0.22);
  padding: 14px 16px 16px;
  background: rgba(255, 255, 255, 0.92);
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 14px;
  padding: 12px 12px;
  box-shadow: none;
}

.chat-input :deep(.el-textarea__inner:focus) {
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.18);
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
}

.hint {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.55);
}

.actions-right {
  display: flex;
  gap: 10px;
  align-items: center;
}

.thinking {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.typing-dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.45);
  animation: typing 1.2s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.15s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.3s;
}

.thinking-text {
  margin-left: 6px;
  color: rgba(15, 23, 42, 0.6);
}

@keyframes typing {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.55;
  }
  50% {
    transform: translateY(-3px);
    opacity: 1;
  }
}

.doc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.doc-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.2s;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(255, 255, 255, 0.75);
}

.doc-item:hover {
  background-color: rgba(64, 158, 255, 0.08);
  transform: translateY(-1px);
}

.doc-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(64, 158, 255, 0.12);
  border: 1px solid rgba(64, 158, 255, 0.22);
  color: #409eff;
}

.doc-meta {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.doc-name {
  font-size: 14px;
  color: rgba(15, 23, 42, 0.86);
  font-weight: 600;
}

.doc-sub {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.48);
}

.side-card {
  border-radius: 16px;
  border: 1px solid rgba(148, 163, 184, 0.24);
  overflow: hidden;
}

.side-header {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.side-title {
  font-weight: 700;
  color: rgba(15, 23, 42, 0.88);
}

.side-tip {
  font-size: 12px;
  color: rgba(15, 23, 42, 0.52);
}

@media (max-width: 992px) {
  .rag-grid :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }

  .chat-card {
    height: calc(100vh - 260px);
  }

  .chat-header-desc {
    max-width: 420px;
  }
}
</style>

