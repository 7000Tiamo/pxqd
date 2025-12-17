import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 如果是blob类型响应（文件下载），直接返回blob数据
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      // 检查Content-Type，如果是JSON格式的blob，说明是错误响应
      const contentType = response.headers['content-type'] || response.headers['Content-Type'] || ''
      if (contentType.includes('application/json')) {
        // 错误响应，需要解析JSON
        return response.data.text().then(text => {
          try {
            const errorData = JSON.parse(text)
            return Promise.reject(new Error(errorData.message || '请求失败'))
          } catch (e) {
            return Promise.reject(new Error('请求失败'))
          }
        })
      }
      // 正常文件响应，直接返回blob
      return response.data
    }

    const res = response.data

    if (res.code === 0) {
      return res
    }

    // 业务错误交由调用方自行提示，避免重复弹窗
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  error => {
    // 处理blob类型的错误响应
    if (error.response && error.response.data instanceof Blob) {
      const contentType = error.response.headers['content-type'] || error.response.headers['Content-Type'] || ''
      if (contentType.includes('application/json')) {
        error.response.data.text().then(text => {
          try {
            const errorData = JSON.parse(text)
            ElMessage.error(errorData.message || '请求失败')
          } catch (e) {
            ElMessage.error('请求失败')
          }
        })
        return Promise.reject(error)
      }
    }

    if (error.response) {
      const { status } = error.response
      
      if (status === 401) {
        ElMessage.error('未授权，请重新登录')
        const authStore = useAuthStore()
        authStore.logout()
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status >= 500) {
        ElMessage.error('服务器错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误')
    }
    
    return Promise.reject(error)
  }
)

export default service


