<template>
  <el-container class="layout-container">
    <el-header class="header">
      <div class="header-left">
        <!-- 品牌图标：使用 public 下的 favicon.ico（路径为 /favicon.ico） -->
        <img class="brand-img" src="/favicon.ico" alt="logo" />
        <h2>企业培训签到系统</h2>
      </div>
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        router
        class="header-menu"
      >
        <el-menu-item v-if="isAdmin" index="/home">首页</el-menu-item>
        <el-menu-item v-if="!isAdmin" index="/my-trainings">我的培训</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/users">用户管理</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/trainings">培训管理</el-menu-item>
        <el-menu-item v-if="isAdmin" index="/stats">数据统计</el-menu-item>
        <el-menu-item index="/rag">智能助手</el-menu-item>
      </el-menu>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar 
              v-if="user?.avatar" 
              :src="user.avatar" 
              :size="32"
              style="margin-right: 8px;"
            />
            <el-icon v-else style="margin-right: 8px;"><User /></el-icon>
            {{ user?.name || '用户' }}
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const authStore = useAuthStore()

const user = computed(() => authStore.user)
const isAdmin = computed(() => user.value?.role === 'admin')
const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      authStore.logout()
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-img {
  width: 26px;
  height: 26px;
  object-fit: contain;
  display: block;
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  color: #409eff;
}

.header-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
}
</style>


