import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getCurrentUser } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 优先从localStorage读取，如果没有则从sessionStorage读取
  const token = ref(localStorage.getItem('token') || sessionStorage.getItem('token') || '')
  const userStr = localStorage.getItem('user') || sessionStorage.getItem('user') || 'null'
  const user = ref(JSON.parse(userStr))
  
  const setToken = (newToken, rememberMe = true) => {
    token.value = newToken
    if (rememberMe) {
      localStorage.setItem('token', newToken)
      sessionStorage.removeItem('token')
    } else {
      sessionStorage.setItem('token', newToken)
      localStorage.removeItem('token')
    }
  }
  
  const setUser = (userData, rememberMe = true) => {
    user.value = userData
    if (rememberMe) {
      localStorage.setItem('user', JSON.stringify(userData))
      sessionStorage.removeItem('user')
    } else {
      sessionStorage.setItem('user', JSON.stringify(userData))
      localStorage.removeItem('user')
    }
  }
  
  const loginAction = async (username, password, rememberMe = true) => {
    try {
      const res = await login(username, password)
      setToken(res.data.token, rememberMe)
      
      // 获取用户信息
      const userRes = await getCurrentUser()
      setUser(userRes.data, rememberMe)
      
      ElMessage.success('登录成功')
      // 根据角色跳转：管理员到首页，员工到我的培训
      if (userRes.data.role === 'admin') {
        router.push('/home')
      } else {
        router.push('/my-trainings')
      }
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
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('user')
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


