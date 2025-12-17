<template>
  <div class="my-trainings-container">
    <div class="page-header">
      <h2 class="page-title">
        <el-icon class="title-icon"><Document /></el-icon>
        我的培训
      </h2>
      <div class="stats-summary">
        <div class="stat-item stat-available">
          <div class="stat-value">{{ statsCounts.available }}</div>
          <div class="stat-label">可报名</div>
        </div>
        <div class="stat-item stat-upcoming">
          <div class="stat-value">{{ statsCounts.upcoming }}</div>
          <div class="stat-label">待参加</div>
        </div>
        <div class="stat-item stat-ongoing">
          <div class="stat-value">{{ statsCounts.ongoing }}</div>
          <div class="stat-label">进行中</div>
        </div>
        <div class="stat-item stat-completed">
          <div class="stat-value">{{ statsCounts.completed }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </div>
    </div>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="custom-tabs">
      <el-tab-pane name="available">
        <template #label>
          <span class="tab-label">
            <el-icon><Plus /></el-icon>
            最新可报名
          </span>
        </template>
        <div v-loading="loading" class="trainings-grid">
          <el-card 
            v-for="training in availableTrainings" 
            :key="training.id" 
            class="training-card card-available"
            shadow="hover"
          >
            <div class="card-header">
              <h3 class="training-title">{{ training.title }}</h3>
              <el-tag type="success" size="small">报名中</el-tag>
            </div>
            <div class="card-content">
              <div class="info-row">
                <el-icon class="info-icon"><User /></el-icon>
                <span class="info-text">{{ training.trainer || '待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">{{ training.location || '地点待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span class="info-text">{{ formatTime(training.startTime) }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Calendar /></el-icon>
                <span class="info-text">{{ formatDate(training.startTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <div class="enrollment-info">
                <span class="enrollment-text">
                  已报名 <strong>{{ training.appliedCount || 0 }}</strong> / 
                  {{ training.maxParticipants && training.maxParticipants > 0 ? training.maxParticipants : '∞' }}
                </span>
              </div>
              <el-button 
                type="primary" 
                :icon="Plus" 
                @click="handleEnroll(training.id)"
                class="enroll-btn"
              >
                立即报名
              </el-button>
            </div>
          </el-card>
          <el-empty 
            v-if="!loading && availableTrainings.length === 0" 
            description="暂无可报名的培训"
            :image-size="120"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane name="upcoming">
        <template #label>
          <span class="tab-label">
            <el-icon><Clock /></el-icon>
            待参加培训
          </span>
        </template>
        <div v-loading="loading" class="trainings-grid">
          <el-card 
            v-for="training in upcomingTrainings" 
            :key="training.id" 
            class="training-card card-upcoming"
            shadow="hover"
          >
            <div class="card-header">
              <h3 class="training-title">{{ training.title }}</h3>
              <el-tag type="success" size="small">报名中</el-tag>
            </div>
            <div class="card-content">
              <div class="info-row">
                <el-icon class="info-icon"><User /></el-icon>
                <span class="info-text">{{ training.trainer || '待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">{{ training.location || '地点待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span class="info-text">{{ formatTime(training.startTime) }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Calendar /></el-icon>
                <span class="info-text">{{ formatDate(training.startTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button-group>
                <el-button 
                  type="primary" 
                  :icon="View" 
                  @click="viewDetail(training.id)"
                >
                  查看详情
                </el-button>
                <el-button 
                  type="danger" 
                  :icon="Delete" 
                  @click="handleCancel(training.id)"
                >
                  取消报名
                </el-button>
              </el-button-group>
            </div>
          </el-card>
          <el-empty 
            v-if="!loading && upcomingTrainings.length === 0" 
            description="暂无待参加的培训"
            :image-size="120"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane name="ongoing">
        <template #label>
          <span class="tab-label">
            <el-icon><VideoPlay /></el-icon>
            进行中培训
          </span>
        </template>
        <div v-loading="loading" class="trainings-grid">
          <el-card 
            v-for="training in ongoingTrainings" 
            :key="training.id" 
            class="training-card card-ongoing"
            shadow="hover"
          >
            <div class="card-header">
              <h3 class="training-title">{{ training.title }}</h3>
              <el-tag type="warning" size="small">进行中</el-tag>
            </div>
            <div class="card-content">
              <div class="info-row">
                <el-icon class="info-icon"><User /></el-icon>
                <span class="info-text">{{ training.trainer || '待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">{{ training.location || '地点待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span class="info-text">{{ formatTime(training.startTime) }} - {{ formatTime(training.endTime) }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Calendar /></el-icon>
                <span class="info-text">{{ formatDate(training.startTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button 
                type="primary" 
                :icon="View" 
                @click="viewDetail(training.id)"
                class="view-btn"
              >
                查看详情
              </el-button>
            </div>
          </el-card>
          <el-empty 
            v-if="!loading && ongoingTrainings.length === 0" 
            description="暂无进行中的培训"
            :image-size="120"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane name="completed">
        <template #label>
          <span class="tab-label">
            <el-icon><CircleCheck /></el-icon>
            已完成培训
          </span>
        </template>
        <div v-loading="loading" class="trainings-grid">
          <el-card 
            v-for="training in completedTrainings" 
            :key="training.id" 
            class="training-card card-completed"
            shadow="hover"
          >
            <div class="card-header">
              <h3 class="training-title">{{ training.title }}</h3>
              <el-tag type="info" size="small">已结束</el-tag>
            </div>
            <div class="card-content">
              <div class="info-row">
                <el-icon class="info-icon"><User /></el-icon>
                <span class="info-text">{{ training.trainer || '待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">{{ training.location || '地点待定' }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span class="info-text">{{ formatTime(training.startTime) }} - {{ formatTime(training.endTime) }}</span>
              </div>
              <div class="info-row">
                <el-icon class="info-icon"><Calendar /></el-icon>
                <span class="info-text">{{ formatDate(training.startTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <el-button 
                type="primary" 
                :icon="View" 
                @click="viewDetail(training.id)"
                class="view-btn"
              >
                查看详情
              </el-button>
            </div>
          </el-card>
          <el-empty 
            v-if="!loading && completedTrainings.length === 0" 
            description="暂无已完成的培训"
            :image-size="120"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getTrainingList } from '@/api/training'
import { getUserEnrolledTrainings, enroll, cancelEnrollment } from '@/api/enrollment'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, Plus, Clock, CircleCheck, User, Location, Calendar, View, Delete, VideoPlay
} from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('available')
const loading = ref(false)
const availableTrainings = ref([])
const upcomingTrainings = ref([])
const ongoingTrainings = ref([])
const completedTrainings = ref([])

// 统计数量（用于统计卡片显示）
const statsCounts = ref({
  available: 0,
  upcoming: 0,
  ongoing: 0,
  completed: 0
})

const formatTime = (time) => {
  if (!time) return '-'
  return dayjs(time).format('HH:mm')
}

const formatDate = (time) => {
  if (!time) return '-'
  return dayjs(time).format('YYYY年MM月DD日')
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

// 加载最新可报名培训（排除已报名的）
const loadAvailableTrainings = async (setLoading = true) => {
  if (setLoading) loading.value = true
  try {
    const userId = authStore.user?.id
    if (!userId) {
      if (setLoading) ElMessage.error('用户信息不存在')
      return
    }
    
    // 获取所有可报名的培训
    const res = await getTrainingList({ page: 1, size: 100, status: 'open' })
    const allAvailable = res.data.records || []
    
    // 获取用户已报名的培训ID列表
    const enrolledRes = await getUserEnrolledTrainings(userId, null)
    const enrolledIds = new Set((enrolledRes.data || []).map(t => t.id))
    
    // 过滤掉已报名的培训
    availableTrainings.value = allAvailable.filter(t => !enrolledIds.has(t.id))
    // 更新统计数量
    statsCounts.value.available = availableTrainings.value.length
  } catch (error) {
    if (setLoading) ElMessage.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    if (setLoading) loading.value = false
  }
}

// 加载待参加培训（已报名且状态为open）
const loadUpcomingTrainings = async (setLoading = true) => {
  if (setLoading) loading.value = true
  try {
    const userId = authStore.user?.id
    if (!userId) {
      if (setLoading) ElMessage.error('用户信息不存在')
      return
    }
    
    // 获取用户已报名且状态为open的培训
    const enrolledRes = await getUserEnrolledTrainings(userId, 'open')
    upcomingTrainings.value = enrolledRes.data || []
    // 更新统计数量
    statsCounts.value.upcoming = upcomingTrainings.value.length
  } catch (error) {
    if (setLoading) ElMessage.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    if (setLoading) loading.value = false
  }
}

// 加载进行中培训（已报名且状态为ongoing）
const loadOngoingTrainings = async (setLoading = true) => {
  if (setLoading) loading.value = true
  try {
    const userId = authStore.user?.id
    if (!userId) {
      if (setLoading) ElMessage.error('用户信息不存在')
      return
    }
    
    // 获取用户已报名且状态为ongoing的培训
    const enrolledRes = await getUserEnrolledTrainings(userId, 'ongoing')
    ongoingTrainings.value = enrolledRes.data || []
    // 更新统计数量
    statsCounts.value.ongoing = ongoingTrainings.value.length
  } catch (error) {
    if (setLoading) ElMessage.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    if (setLoading) loading.value = false
  }
}

// 加载已完成培训（已报名且状态为ended）
const loadCompletedTrainings = async (setLoading = true) => {
  if (setLoading) loading.value = true
  try {
    const userId = authStore.user?.id
    if (!userId) {
      if (setLoading) ElMessage.error('用户信息不存在')
      return
    }
    
    const res = await getUserEnrolledTrainings(userId, 'ended')
    completedTrainings.value = res.data || []
    // 更新统计数量
    statsCounts.value.completed = completedTrainings.value.length
  } catch (error) {
    if (setLoading) ElMessage.error('加载失败：' + (error.message || '未知错误'))
  } finally {
    if (setLoading) loading.value = false
  }
}

// 只查询各状态的数量（用于初始化统计卡片）
// 如果 loadAvailableData 为 true，同时加载可报名培训的完整数据
const loadStatsCounts = async (loadAvailableData = false) => {
  try {
    const userId = authStore.user?.id
    if (!userId) {
      return
    }
    
    // 并行查询所有状态的数量
    const [availableRes, enrolledRes] = await Promise.all([
      // 获取所有可报名的培训
      getTrainingList({ page: 1, size: 100, status: 'open' }),
      // 获取用户所有已报名的培训
      getUserEnrolledTrainings(userId, null)
    ])
    
    const allAvailable = availableRes.data.records || []
    const allEnrolled = enrolledRes.data || []
    const enrolledIds = new Set(allEnrolled.map(t => t.id))
    
    // 计算可报名数量（排除已报名的）
    const availableList = allAvailable.filter(t => !enrolledIds.has(t.id))
    const availableCount = availableList.length
    
    // 如果需要在加载统计数据时同时加载可报名数据，则设置数据
    if (loadAvailableData) {
      availableTrainings.value = availableList
    }
    
    // 按状态分类统计已报名的培训数量
    const upcomingCount = allEnrolled.filter(t => t.status === 'open').length
    const ongoingCount = allEnrolled.filter(t => t.status === 'ongoing').length
    const completedCount = allEnrolled.filter(t => t.status === 'ended').length
    
    // 更新统计数量
    statsCounts.value = {
      available: availableCount,
      upcoming: upcomingCount,
      ongoing: ongoingCount,
      completed: completedCount
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const handleTabChange = (tabName) => {
  if (tabName === 'available') {
    loadAvailableTrainings()
  } else if (tabName === 'upcoming') {
    loadUpcomingTrainings()
  } else if (tabName === 'ongoing') {
    loadOngoingTrainings()
  } else if (tabName === 'completed') {
    loadCompletedTrainings()
  }
}

const handleEnroll = async (trainingId) => {
  try {
    await ElMessageBox.confirm('确定要报名该培训吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    const userId = authStore.user?.id
    if (!userId) {
      ElMessage.error('用户信息不存在')
      return
    }
    
    await enroll(trainingId, userId)
    ElMessage.success('报名成功！')
    // 刷新列表：从可报名中移除，添加到待参加中
    await Promise.all([
      loadAvailableTrainings(),
      loadUpcomingTrainings(),
      loadOngoingTrainings()
    ])
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '报名失败')
    }
  }
}

const handleCancel = async (trainingId) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const userId = authStore.user?.id
    if (!userId) {
      ElMessage.error('用户信息不存在')
      return
    }
    
    await cancelEnrollment(trainingId, userId)
    ElMessage.success('取消报名成功')
    // 刷新列表：从待参加/进行中中移除，如果状态是open则重新出现在可报名中
    await Promise.all([
      loadAvailableTrainings(),
      loadUpcomingTrainings(),
      loadOngoingTrainings()
    ])
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消报名失败')
    }
  }
}

const viewDetail = (id) => {
  router.push(`/trainings/${id}`)
}

onMounted(() => {
  // 页面加载时查询各状态的数量，并同时加载可报名培训的数据（避免重复调用接口）
  loadStatsCounts(true)
})
</script>

<style scoped>
.my-trainings-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #fff;
}

.title-icon {
  font-size: 32px;
}

.stats-summary {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px 25px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 10px;
  min-width: 100px;
  transition: all 0.3s;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.custom-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.trainings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.training-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: hidden;
}

.training-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-available {
  border-top: 4px solid #67c23a;
}

.card-upcoming {
  border-top: 4px solid #e6a23c;
}

.card-ongoing {
  border-top: 4px solid #f56c6c;
}

.card-completed {
  border-top: 4px solid #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.training-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  line-height: 1.4;
}

.card-content {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  color: #606266;
}

.info-icon {
  font-size: 18px;
  color: #909399;
  flex-shrink: 0;
}

.info-text {
  font-size: 14px;
  flex: 1;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.enrollment-info {
  flex: 1;
}

.enrollment-text {
  font-size: 13px;
  color: #909399;
}

.enrollment-text strong {
  color: #409eff;
  font-weight: 600;
}

.enroll-btn {
  font-weight: 500;
  padding: 10px 20px;
}

.view-btn {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }

  .stats-summary {
    width: 100%;
    justify-content: space-around;
  }

  .stat-item {
    flex: 1;
    min-width: auto;
  }

  .trainings-grid {
    grid-template-columns: 1fr;
  }
}

/* 空状态样式优化 */
:deep(.el-empty) {
  grid-column: 1 / -1;
  padding: 60px 20px;
}

:deep(.el-empty__description) {
  color: #909399;
  font-size: 14px;
}

/* Tab样式优化 */
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}

:deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
  padding: 0 24px;
  height: 50px;
  line-height: 50px;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

:deep(.el-tabs__active-bar) {
  height: 3px;
}

/* 加载状态 */
:deep(.el-loading-mask) {
  border-radius: 12px;
}
</style>

