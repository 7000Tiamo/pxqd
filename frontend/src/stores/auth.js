import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getCurrentUser } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUser = (userData) => {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }
  
  const loginAction = async (username, password) => {
    try {
      const res = await login(username, password)
      setToken(res.data.token)
      
      // 获取用户信息
      const userRes = await getCurrentUser()
      setUser(userRes.data)
      
      ElMessage.success('登录成功')
      router.push('/home')
      return true
    } catch (error) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }
  
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    router.push('/login')
  }
  
  return {
    token,
    user,
    setToken,
    setUser,
    loginAction,
    logout
  }
})


