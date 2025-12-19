<template>
  <div class="stats-container">
    <div class="page-header">
      <h2>数据统计</h2>
      <el-button @click="handleExport">📥 导出Excel</el-button>
    </div>
    
    <!-- 概览统计 -->
    <el-row :gutter="20" class="overview-stats">
      <el-col :span="overviewColSpan" v-for="(item, index) in overviewStats" :key="index">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 统计维度切换 -->
    <el-card class="mt-20">
      <template #header>
        <div class="card-header">
          <span>统计维度</span>
          <el-radio-group v-model="statsType" @change="loadStatsData">
            <el-radio-button label="training">按培训项目</el-radio-button>
            <el-radio-button label="department">按部门</el-radio-button>
            <el-radio-button label="employee">按员工</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <!-- 按培训项目统计 -->
      <div v-if="statsType === 'training'">
        <el-table :data="trainingStats" border>
          <el-table-column prop="trainingName" label="培训名称" min-width="200" />
          <el-table-column prop="appliedCount" label="应到" width="100" />
          <el-table-column prop="signedCount" label="实到" width="100" />
          <el-table-column prop="lateCount" label="迟到" width="100" />
          <el-table-column prop="notSignedCount" label="未签到" width="100" />
          <el-table-column prop="participationRate" label="参与率" width="120" />
        </el-table>
      </div>
      
      <!-- 按部门统计 -->
      <div v-if="statsType === 'department'">
        <el-table :data="departmentStats" border>
          <el-table-column prop="department" label="部门" width="150" />
          <el-table-column prop="totalUsers" label="总人数" width="100" />
          <el-table-column prop="totalParticipations" label="参与人次" width="120" />
          <el-table-column prop="totalCheckins" label="签到人次" width="120" />
        </el-table>
        
        <div class="chart-container mt-20">
          <v-chart :option="departmentChartOption" style="height: 400px" />
        </div>
      </div>
      
      <!-- 按员工统计 -->
      <div v-if="statsType === 'employee'">
        <el-table :data="employeeStats" border>
          <el-table-column prop="userName" label="姓名" width="120" />
          <el-table-column prop="employeeNo" label="工号" width="120" />
          <el-table-column prop="department" label="部门" width="150" />
          <el-table-column prop="enrollments" label="报名次数" width="120" />
          <el-table-column prop="checkins" label="签到次数" width="120" />
          <el-table-column prop="lateCount" label="迟到次数" width="120" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  getTongji,
  getStatsByTraining,
  getStatsByDepartment,
  getStatsByEmployee,
  exportStatsByTraining,
  exportStatsByDepartment,
  exportStatsByEmployee
} from '@/api/stats'
import { ElMessage } from 'element-plus'


const statsType = ref('training')
const overviewStats = ref([])
const trainingStats = ref([])
const departmentStats = ref([])
const employeeStats = ref([])

const overviewColSpan = computed(() => {
  const len = overviewStats.value.length || 1
  return Math.floor(24 / len)
})

const departmentChartOption = computed(() => {
  return {
    title: {
      text: '各部门参与情况',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: departmentStats.value.map(item => item.department)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '参与人次',
        type: 'bar',
        data: departmentStats.value.map(item => Number(item.totalParticipations ?? 0)),
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  }
})

const loadOverviewStats = async () => {
  try {
    const res = await getTongji()
    const data = res.data || {}

    // monthlyParticipationRate 可能是 0~1 的比例，也可能是 0~100 的百分比数值，这里做兼容
    const rawRate = Number(data.monthlyParticipationRate ?? 0)
    const ratePercent = rawRate <= 1 ? rawRate * 100 : rawRate
    const rateText = `${ratePercent.toFixed(2)}%`

    overviewStats.value = [
      { label: '本月平均参与率', value: rateText },
      { label: '累计培训时长', value: `${Number(data.totalTrainingHours ?? 0).toFixed(2)}h` },
      { label: '人均培训场次', value: Number(data.averageTrainingSessions ?? 0).toFixed(2) }
    ]
  } catch (error) {
    console.error(error)
  }
}

const loadStatsData = async () => {
  try {
    if (statsType.value === 'training') {
      const res = await getStatsByTraining()
      trainingStats.value = res.data || []
    } else if (statsType.value === 'department') {
      const res = await getStatsByDepartment()
      departmentStats.value = res.data || []
    } else if (statsType.value === 'employee') {
      const res = await getStatsByEmployee()
      employeeStats.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

const handleExport = () => {
  const pad2 = (n) => String(n).padStart(2, '0')
  const d = new Date()
  const ts = `${d.getFullYear()}${pad2(d.getMonth() + 1)}${pad2(d.getDate())}_${pad2(d.getHours())}${pad2(d.getMinutes())}${pad2(d.getSeconds())}`

  const downloadBlob = (blob, filename) => {
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    a.remove()
    window.URL.revokeObjectURL(url)
  }

  const run = async () => {
    try {
      let blob
      let filename = `统计导出_${ts}.xlsx`

      if (statsType.value === 'training') {
        blob = await exportStatsByTraining()
        filename = `按培训项目统计_${ts}.xlsx`
      } else if (statsType.value === 'department') {
        blob = await exportStatsByDepartment()
        filename = `按部门统计_${ts}.xlsx`
      } else if (statsType.value === 'employee') {
        blob = await exportStatsByEmployee()
        filename = `按员工统计_${ts}.xlsx`
      }

      downloadBlob(blob, filename)
      ElMessage.success('导出成功')
    } catch (error) {
      ElMessage.error(error?.message || '导出失败')
    }
  }

  run()
}

onMounted(() => {
  loadOverviewStats()
  loadStatsData()
})
</script>

<style scoped>
.stats-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.overview-stats {
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

.mt-20 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  margin-top: 30px;
}
</style>

