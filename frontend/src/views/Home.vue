<template>
  <div class="home-container">
    <h2 class="page-title">{{ isAdmin ? '管理总览' : '我的培训看板' }}</h2>
    
    <!-- 管理员首页 -->
    <template v-if="isAdmin">
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="6" v-for="(item, index) in statsCards" :key="index">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="content-row">
        <el-col :span="16">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>最近发布的培训</span>
                <el-button type="primary" link @click="$router.push('/trainings')">查看全部</el-button>
              </div>
            </template>
            <el-table :data="recentTrainings" style="width: 100%">
              <el-table-column prop="title" label="培训名称" />
              <el-table-column prop="startTime" label="时间" width="180" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <el-button class="quick-action-btn" type="primary" @click="$router.push('/trainings/create')">
                发布新培训
              </el-button>
              <el-button class="quick-action-btn" @click="$router.push('/users')">
                添加新员工
              </el-button>
              <el-button class="quick-action-btn" @click="$router.push('/stats')">
                数据统计
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>
    
    <!-- 员工首页 -->
    <template v-else>
      <el-card class="mb-20">
        <template #header>
          <span>待参加培训</span>
        </template>
        <el-table :data="myTrainings" v-if="myTrainings.length > 0">
          <el-table-column prop="title" label="培训名称" />
          <el-table-column prop="startTime" label="时间" width="180" />
          <el-table-column prop="trainer" label="讲师" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无待参加的培训" />
      </el-card>
      
      <el-card>
        <template #header>
          <span>最新可报名</span>
        </template>
        <el-row :gutter="20">
          <el-col :span="12" v-for="training in availableTrainings" :key="training.id">
            <el-card class="training-card" @click="viewTraining(training.id)">
              <h3>{{ training.title }}</h3>
              <p class="training-info">⏰ {{ training.startTime }}</p>
              <p class="training-info">📍 {{ training.location || '地点待定' }}</p>
              <el-button type="primary" @click.stop="handleEnroll(training.id)" style="width: 100%; margin-top: 10px">
                立即报名
              </el-button>
            </el-card>
          </el-col>
        </el-row>
        <el-empty v-if="availableTrainings.length === 0" description="暂无新培训" />
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getOverviewStats } from '@/api/stats'
import { getTrainingList } from '@/api/training'
import { enroll } from '@/api/enrollment'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const authStore = useAuthStore()

const isAdmin = computed(() => authStore.user?.role === 'admin')
const statsCards = ref([])
const recentTrainings = ref([])
const myTrainings = ref([])
const availableTrainings = ref([])

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

const loadAdminData = async () => {
  try {
    const statsRes = await getOverviewStats()
    statsCards.value = [
      { label: '本月培训场次', value: statsRes.data.monthTrainings || 0 },
      { label: '当前报名人数', value: statsRes.data.currentEnrollments || 0 },
      { label: '本月签到总人数', value: statsRes.data.monthCheckins || 0 },
      { label: '培训完成率', value: statsRes.data.completionRate || '0%' }
    ]
    
    const trainingRes = await getTrainingList({ page: 1, size: 5 })
    recentTrainings.value = trainingRes.data.records.map(t => ({
      ...t,
      startTime: dayjs(t.startTime).format('YYYY-MM-DD HH:mm')
    }))
  } catch (error) {
    console.error(error)
  }
}

const loadEmployeeData = async () => {
  try {
    const trainingRes = await getTrainingList({ page: 1, size: 10, status: 'open' })
    availableTrainings.value = trainingRes.data.records.map(t => ({
      ...t,
      startTime: dayjs(t.startTime).format('YYYY-MM-DD HH:mm')
    }))
  } catch (error) {
    console.error(error)
  }
}

const handleEnroll = async (trainingId) => {
  try {
    await ElMessageBox.confirm('确定要报名该培训吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    await enroll(trainingId, authStore.user.id)
    ElMessage.success('报名成功！请准时参加')
    loadEmployeeData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '报名失败')
    }
  }
}

const viewTraining = (id) => {
  router.push(`/trainings/${id}`)
}

onMounted(() => {
  if (isAdmin.value) {
    loadAdminData()
  } else {
    loadEmployeeData()
  }
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  margin-bottom: 20px;
  font-size: 24px;
  color: #303133;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.content-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-action-btn {
  width: 100%;
  box-sizing: border-box;
}

/* Element Plus 默认会给相邻按钮加 margin-left，竖排时会造成左右不齐 */
.quick-actions .el-button + .el-button {
  margin-left: 0;
}

.mb-20 {
  margin-bottom: 20px;
}

.training-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.training-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.training-card h3 {
  margin-bottom: 10px;
  color: #303133;
}

.training-info {
  margin: 5px 0;
  color: #909399;
  font-size: 14px;
}
</style>

