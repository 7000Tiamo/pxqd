<template>
  <div class="detail-container" v-loading="loading">
    <div class="page-header">
      <el-button @click="$router.back()">← 返回</el-button>
      <h2>培训详情</h2>
    </div>
    
    <el-row :gutter="20" v-if="training">
      <el-col :span="16">
        <el-card>
          <div class="training-header">
            <div>
              <h3>{{ training.title }}</h3>
              <div class="training-meta">
                <span>👤 {{ training.trainer }}</span>
                <span>📍 {{ training.location }}</span>
                <span>⏰ {{ formatTime(training.startTime) }} - {{ formatTime(training.endTime) }}</span>
              </div>
            </div>
            <el-tag :type="getStatusType(training.status)" size="large">
              {{ getStatusText(training.status) }}
            </el-tag>
          </div>
          
          <el-divider />
          
          <div class="training-description">
            <h4>培训介绍</h4>
            <p>{{ training.description || '暂无介绍' }}</p>
          </div>
          
          <div class="training-actions" v-if="isAdmin">
            <el-button type="primary" @click="generateQRCode">生成签到二维码</el-button>
            <el-button @click="editTraining">编辑培训</el-button>
            <el-button @click="publishNotice">发布公告</el-button>
          </div>
        </el-card>
        
        <el-card class="mt-20">
          <template #header>
            <span>学员名单</span>
          </template>
          <el-table :data="enrollmentList" border>
            <el-table-column prop="userName" label="姓名" width="120" />
            <el-table-column prop="userDept" label="部门" width="150" />
            <el-table-column prop="enrolledAt" label="报名时间" width="180">
              <template #default="{ row }">
                {{ formatTime(row.enrolledAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="checkinTime" label="签到时间" width="180">
              <template #default="{ row }">
                {{ row.checkinTime ? formatTime(row.checkinTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.checkinTime" :type="row.isLate ? 'warning' : 'success'">
                  {{ row.isLate ? '迟到' : '正常' }}
                </el-tag>
                <el-tag v-else type="info">未签到</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-row :gutter="10" class="stats-cards">
          <el-col :span="12" v-for="(stat, index) in statsCards" :key="index">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-dialog
      v-model="qrDialogVisible"
      title="签到二维码"
      width="420px"
      @close="handleQrClose"
    >
      <div class="qr-wrap" v-if="qrCodeUrl">
        <img :src="qrCodeUrl" alt="签到二维码" class="qr-image" />
        <p class="qr-desc">手机扫码打开签到页面，输入用户名即可签到</p>
        <el-input
          v-model="qrTargetUrl"
          readonly
          class="mt-10"
        >
          <template #append>
            <el-button @click="copyLink">复制链接</el-button>
          </template>
        </el-input>
      </div>
      <div v-else class="qr-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>二维码生成中...</span>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getTrainingDetail } from '@/api/training'
import { getTrainingEnrollments } from '@/api/enrollment'
import { getCheckinStats } from '@/api/checkin'
import { ElMessage } from 'element-plus'
import { Loading, CopyDocument } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const training = ref(null)
const enrollmentList = ref([])
const statsCards = ref([])
const qrDialogVisible = ref(false)
const qrCodeUrl = ref('')
const qrTargetUrl = ref('')
let qrBlobUrl = ''

const isAdmin = computed(() => authStore.user?.role === 'admin')

const formatTime = (time) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const getStatusType = (status) => {
  const map = {
    'draft': 'info',
    'open': 'success',
    'ongoing': 'warning',
    'ended': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'draft': '草稿',
    'open': '报名中',
    'ongoing': '进行中',
    'ended': '已结束'
  }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const [trainingRes, enrollmentRes, statsRes] = await Promise.all([
      getTrainingDetail(id),
      getTrainingEnrollments(id),
      getCheckinStats(id)
    ])
    
    training.value = trainingRes.data
    
    // 更新统计卡片
    statsCards.value = [
      { label: '已报名', value: training.value.appliedCount || 0 },
      { label: '已签到', value: training.value.signedCount || 0 },
      { label: '迟到', value: training.value.lateCount || 0 },
      { label: '签到率', value: (training.value.signRate || 0).toFixed(1) + '%' }
    ]
    
    enrollmentList.value = enrollmentRes.data || []
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const generateQRCode = () => {
  if (!route.params.id) return
  qrDialogVisible.value = true
  qrCodeUrl.value = ''
  const origin = import.meta.env.VITE_FRONTEND_DOMAIN || window.location.origin
  qrTargetUrl.value = `${origin}/checkin?training_id=${route.params.id}`
  

  // 获取后端生成的二维码图片（PNG）
  fetch(`/api/qrcode?trainingId=${route.params.id}`)
    .then(async (res) => {
      if (!res.ok) {
        throw new Error('二维码生成失败')
      }
      const blob = await res.blob()
      if (qrBlobUrl) URL.revokeObjectURL(qrBlobUrl)
      qrBlobUrl = URL.createObjectURL(blob)
      qrCodeUrl.value = qrBlobUrl
    })
    .catch(() => {
      ElMessage.error('二维码生成失败，请稍后重试')
      qrDialogVisible.value = false
    })
}

const editTraining = () => {
  router.push(`/trainings/${route.params.id}/edit`)
}

const publishNotice = () => {
  ElMessage.info('公告发布功能开发中')
}

const copyLink = async () => {
  if (!qrTargetUrl.value) return
  try {
    await navigator.clipboard.writeText(qrTargetUrl.value)
    ElMessage.success('链接已复制')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

const handleQrClose = () => {
  if (qrBlobUrl) {
    URL.revokeObjectURL(qrBlobUrl)
    qrBlobUrl = ''
  }
  qrCodeUrl.value = ''
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.detail-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.training-header h3 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.training-meta {
  display: flex;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}

.training-description {
  margin: 20px 0;
}

.training-description h4 {
  margin-bottom: 10px;
  color: #303133;
}

.training-description p {
  color: #606266;
  line-height: 1.8;
}

.training-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.mt-20 {
  margin-top: 20px;
}

.stats-cards {
  margin-bottom: 10px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.qr-wrap {
  text-align: center;
}

.qr-image {
  width: 240px;
  height: 240px;
  object-fit: contain;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 8px;
  background: #fff;
}

.qr-desc {
  margin: 10px 0 6px;
  color: #606266;
}

.qr-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  justify-content: center;
  min-height: 180px;
}

.mt-10 {
  margin-top: 10px;
}
</style>


