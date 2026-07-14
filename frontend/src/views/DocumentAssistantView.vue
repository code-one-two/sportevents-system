<template>
  <div class="assistant-page">
    <el-card class="document-panel" shadow="never">
      <template #header>
        <div class="panel-title">
          <div>
            <strong>参考文档</strong>
            <p>粘贴或导入文本，助手会优先依据文档回答</p>
          </div>
          <el-upload
            accept=".txt,.md,.csv,.json"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
          >
            <el-button :icon="Upload">导入文本</el-button>
          </el-upload>
        </div>
      </template>
      <el-input
        v-model="documentContent"
        type="textarea"
        :rows="24"
        maxlength="50000"
        show-word-limit
        placeholder="在这里粘贴规章制度、器材说明、活动方案等文档内容……"
      />
      <div class="document-actions">
        <el-button link type="danger" :disabled="!documentContent" @click="documentContent = ''">
          清空文档
        </el-button>
      </div>
    </el-card>

    <el-card class="chat-panel" shadow="never">
      <template #header>
        <div class="panel-title">
          <div>
            <strong>DeepSeek 文档助手</strong>
            <p>可进行总结、问答、润色和清单提取</p>
          </div>
          <el-button link :disabled="messages.length === 0" @click="clearConversation">清空对话</el-button>
        </div>
      </template>

      <div ref="messageContainer" class="messages">
        <div v-if="messages.length === 0" class="empty-state">
          <el-icon><ChatDotRound /></el-icon>
          <h3>想从这份文档里知道什么？</h3>
          <p>例如：总结核心要点、提取借用规则、生成通知。</p>
          <div class="suggestions">
            <el-button v-for="item in suggestions" :key="item" round @click="question = item">
              {{ item }}
            </el-button>
          </div>
        </div>

        <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
          <div class="message-label">{{ message.role === 'user' ? '你' : 'DeepSeek' }}</div>
          <div class="message-content">{{ message.content }}</div>
        </div>
        <div v-if="loading" class="message assistant">
          <div class="message-label">DeepSeek</div>
          <div class="message-content typing">正在阅读并整理文档<span>...</span></div>
        </div>
      </div>

      <div class="composer">
        <el-input
          v-model="question"
          type="textarea"
          :rows="3"
          maxlength="4000"
          show-word-limit
          resize="none"
          placeholder="输入关于文档的问题，Ctrl / Command + Enter 发送"
          @keydown="handleKeydown"
        />
        <el-button
          type="primary"
          :icon="Promotion"
          :loading="loading"
          :disabled="!question.trim()"
          @click="sendQuestion"
        >
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { ChatDotRound, Promotion, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { askDocumentAssistant } from '@/api/ai'

const documentContent = ref('')
const question = ref('')
const messages = ref([])
const loading = ref(false)
const messageContainer = ref()

const suggestions = ['总结这份文档的核心要点', '提取所有需要注意的规则', '把内容整理成执行清单']

async function handleFileChange(file) {
  const raw = file.raw
  if (!raw) return
  if (raw.size > 1024 * 1024) {
    ElMessage.warning('文件不能超过 1MB')
    return
  }
  try {
    documentContent.value = await raw.text()
    ElMessage.success(`已导入 ${raw.name}`)
  } catch {
    ElMessage.error('文件读取失败，请使用 UTF-8 文本文件')
  }
}

function handleKeydown(event) {
  if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
    event.preventDefault()
    sendQuestion()
  }
}

async function sendQuestion() {
  const content = question.value.trim()
  if (!content || loading.value) return

  const history = messages.value.slice(-20).map(({ role, content: messageContent }) => ({
    role,
    content: messageContent
  }))
  messages.value.push({ role: 'user', content })
  question.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await askDocumentAssistant({
      documentContent: documentContent.value,
      question: content,
      history
    })
    messages.value.push({ role: 'assistant', content: res.data.answer })
  } catch {
    messages.value.pop()
    question.value = content
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

async function clearConversation() {
  await ElMessageBox.confirm('确定清空当前对话？参考文档会保留。', '提示', { type: 'warning' })
  messages.value = []
}

async function scrollToBottom() {
  await nextTick()
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}
</script>

<style scoped>
.assistant-page {
  display: grid;
  grid-template-columns: minmax(320px, 0.85fr) minmax(420px, 1.15fr);
  gap: 16px;
  height: calc(100vh - 100px);
  min-height: 560px;
}
.document-panel,
.chat-panel {
  height: 100%;
}
.document-panel :deep(.el-card__body),
.chat-panel :deep(.el-card__body) {
  height: calc(100% - 69px);
}
.document-panel :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
}
.document-panel :deep(.el-textarea),
.document-panel :deep(.el-textarea__inner) {
  height: 100%;
}
.panel-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}
.panel-title p {
  margin-top: 5px;
  color: #909399;
  font-size: 12px;
}
.document-actions {
  display: flex;
  justify-content: flex-end;
  height: 30px;
}
.chat-panel :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  padding: 0;
}
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 22px;
}
.empty-state {
  display: flex;
  height: 100%;
  min-height: 300px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #606266;
  text-align: center;
}
.empty-state .el-icon {
  margin-bottom: 14px;
  color: #409eff;
  font-size: 48px;
}
.empty-state p {
  margin: 8px 0 20px;
  color: #909399;
}
.suggestions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}
.suggestions .el-button {
  margin: 0;
}
.message {
  margin-bottom: 20px;
}
.message-label {
  margin-bottom: 6px;
  color: #909399;
  font-size: 12px;
}
.message-content {
  display: inline-block;
  max-width: 88%;
  padding: 11px 14px;
  border-radius: 10px;
  background: #f4f4f5;
  color: #303133;
  line-height: 1.7;
  text-align: left;
  white-space: pre-wrap;
  word-break: break-word;
}
.message.user {
  text-align: right;
}
.message.user .message-content {
  background: #409eff;
  color: white;
}
.typing {
  color: #909399;
}
.composer {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding: 16px;
  border-top: 1px solid #ebeef5;
}
.composer .el-button {
  height: 40px;
}
@media (max-width: 960px) {
  .assistant-page {
    display: flex;
    height: auto;
    flex-direction: column;
  }
  .document-panel {
    height: 420px;
  }
  .chat-panel {
    height: 620px;
  }
}
</style>
