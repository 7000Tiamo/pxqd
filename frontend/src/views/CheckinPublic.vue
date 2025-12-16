<template>
  <div class="checkin-page">
    <div class="checkin-card">
      <div class="header">
        <div>
          <h2>培训签到</h2>
          <p class="subtitle">扫码后自动带入培训ID，输入用户名和工号即可完成签到</p>
        </div>
        <el-tag :type="trainingId ? 'success' : 'danger'">
          {{ trainingId ? `培训ID：${trainingId}` : '缺少培训ID' }}
        </el-tag>
      </div>

      <el-alert
        v-if="!trainingId"
        type="error"
        title="未获取到培训ID，请通过有效二维码进入或补充 training_id 参数"
        show-icon
        class="mb-16"
      />

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
            立即签到
          </el-button>
        </el-form-item>
      </el-form>

      <el-result
        v-if="checkinResult"
        icon="success"
        title="签到成功"
        sub-title="信息已记录"
        class="result"
      >
        <template #sub-title>
          <div class="result-info">
            <p>签到时间：{{ formatTime(checkinResult.checkinTime) || '已记录' }}</p>
            <p>状态：{{ stateText }}</p>
            <p v-if="checkinResult.isLate">已标记：迟到</p>
          </div>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publicCheckin } from '@/api/checkin'

const route = useRoute()
const formRef = ref()
const loading = ref(false)
const checkinResult = ref(null)

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

const stateText = computed(() => {
  if (!checkinResult.value) return ''
  if (checkinResult.value.state === 'signed') return '已签到'
  if (checkinResult.value.state === 'checked_out') return '已签退'
  return checkinResult.value.state || '已记录'
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  if (Number.isNaN(date.getTime())) return time
  return date.toLocaleString()
}

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
      const res = await publicCheckin(trainingId.value, form.username.trim(), form.employeeNo.trim())
      checkinResult.value = res.data
      ElMessage.success('签到成功')
    } catch (error) {
      ElMessage.error(error.message || '签到失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.checkin-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa, #e4e7ed);
  padding: 24px;
}

.checkin-card {
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
</style>

