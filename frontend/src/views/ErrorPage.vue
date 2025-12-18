<template>
  <div class="error-page">
    <div class="error-card">
      <el-result
        icon="error"
        title="二维码已过期"
        sub-title="请重新扫描有效的二维码"
      >
        <template #extra>
          <el-button type="primary" @click="goBack">返回</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  // 返回上一页，如果没有上一页则跳转到登录页
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/login')
  }
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa, #e4e7ed);
  padding: 24px;
}

.error-card {
  width: 100%;
  max-width: 520px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  padding: 28px;
}
</style>

