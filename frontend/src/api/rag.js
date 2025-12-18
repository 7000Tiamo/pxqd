import request from './request'

/**
 * 发送聊天消息（流式响应）
 */
export function sendChatMessage(data) {
  return request({
    url: '/rag/chat/stream',
    method: 'post',
    data,
    responseType: 'text', // 需要处理流式响应
    timeout: 300000 // 5分钟超时
  })
}


