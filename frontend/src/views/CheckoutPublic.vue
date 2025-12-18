<template>
  <div class="checkout-page">
    <div class="checkout-card">
      <!-- 二维码过期提示（扫码进入且 token 无效时显示） -->
      <el-result
        v-if="tokenExpired"
        icon="error"
        title="二维码已过期"
        sub-title="请重新扫描有效的二维码进行签退"
        class="expired-result"
      />

      <!-- 正常页面内容 -->
      <template v-else>
      <div class="header">
        <div>
          <h2>培训签退</h2>
        </div>
      </div>

      <!-- 培训信息卡片 -->
      <el-card v-if="trainingInfo" class="training-info-card mb-16" shadow="never">
        <div class="training-info">
          <h3 class="training-title">{{ trainingInfo.title }}</h3>
          <div class="training-meta">
            <div class="meta-item" v-if="trainingInfo.trainer">
              <el-icon><User /></el-icon>
              <span>讲师：{{ trainingInfo.trainer }}</span>
            </div>
            <div class="meta-item" v-if="trainingInfo.location">
              <el-icon><Location /></el-icon>
              <span>地点：{{ trainingInfo.location }}</span>
            </div>
            <div class="meta-item" v-if="trainingInfo.startTime">
              <el-icon><Clock /></el-icon>
              <span>开始时间：{{ formatTime(trainingInfo.startTime) }}</span>
            </div>
            <div class="meta-item" v-if="trainingInfo.endTime">
              <el-icon><Clock /></el-icon>
              <span>结束时间：{{ formatTime(trainingInfo.endTime) }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入您的用户名"
            clearable
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item label="工号" prop="employeeNo">
          <el-input
            v-model="form.employeeNo"
            placeholder="请输入您的工号"
            clearable
            autocomplete="off"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            size="large"
            class="submit-btn"
            @click="handleSubmit"
            :disabled="!trainingId"
          >
            立即签退
          </el-button>
        </el-form-item>
      </el-form>

      <el-result
        v-if="checkoutResult"
        icon="success"
        title="签退成功"
        sub-title="信息已记录"
        class="result"
      >
        <template #sub-title>
          <div class="result-info">
            <p>签退时间：{{ formatTime(checkoutResult.checkoutTime) || '已记录' }}</p>
            <p>状态：{{ stateText }}</p>
            <p v-if="checkoutResult.isEarlyLeave">已标记：早退</p>
          </div>
        </template>
      </el-result>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Location, Clock } from '@element-plus/icons-vue'
import { publicCheckout, isCheckoutActive } from '@/api/checkin'
import { getPublicTrainingInfo } from '@/api/training'
import dayjs from 'dayjs'

const route = useRoute()
const formRef = ref()
const loading = ref(false)
const checkoutResult = ref(null)
const trainingInfo = ref(null)
const loadingTraining = ref(false)
const tokenExpired = ref(false)
const checkingToken = ref(false)

const form = reactive({
  username: '',
  employeeNo: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }]
}

const trainingId = computed(() => {
  return route.query.training_id || route.query.trainingId || route.query.id
})

const token = computed(() => {
  return route.query.token
})

const stateText = computed(() => {
  if (!checkoutResult.value) return ''
  if (checkoutResult.value.state === 'signed') return '已签到'
  if (checkoutResult.value.state === 'checked_out') return '已签退'
  return checkoutResult.value.state || '已记录'
})

const formatTime = (time) => {
  if (!time) return ''
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 加载培训信息
const loadTrainingInfo = async () => {
  if (!trainingId.value) return
  loadingTraining.value = true
  try {
    const res = await getPublicTrainingInfo(trainingId.value)
    trainingInfo.value = res.data
  } catch (error) {
    console.error('加载培训信息失败:', error)
  } finally {
    loadingTraining.value = false
  }
}

// 监听 trainingId 变化
watch(trainingId, (newId) => {
  if (newId) {
    loadTrainingInfo()
  }
})

// 扫码进入时：先校验 token 是否有效（无 token 则不校验）
const checkTokenValid = async () => {
  if (!trainingId.value) return
  if (!token.value) {
    loadTrainingInfo()
    return
  }

  checkingToken.value = true
  try {
    const res = await isCheckoutActive(trainingId.value, token.value)
    if (res.data === false) {
      tokenExpired.value = true
      return
    }
    loadTrainingInfo()
  } catch (e) {
    tokenExpired.value = true
  } finally {
    checkingToken.value = false
  }
}

onMounted(() => {
  checkTokenValid()
})

const handleSubmit = async () => {
  if (!trainingId.value) {
    ElMessage.error('缺少培训ID，请重新扫码进入')
    return
  }

  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await publicCheckout(trainingId.value, form.username.trim(), form.employeeNo.trim(), token.value)
      checkoutResult.value = res.data
      ElMessage.success('签退成功')
    } catch (error) {
      ElMessage.error(error.message || '签退失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa, #e4e7ed);
  padding: 24px;
}

.checkout-card {
  width: 100%;
  max-width: 520px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.08);
  padding: 28px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.subtitle {
  margin: 6px 0 0;
  color: #909399;
  font-size: 14px;
}

.form {
  margin-top: 12px;
}

.submit-btn {
  width: 100%;
}

.result {
  margin-top: 8px;
}

.result-info {
  color: #606266;
  line-height: 1.8;
}

.mb-16 {
  margin-bottom: 16px;
}

.training-info-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.training-info {
  padding: 8px 0;
}

.training-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.training-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
}

.meta-item .el-icon {
  color: #909399;
}

.expired-result {
  padding: 20px 0;
}

</style>

