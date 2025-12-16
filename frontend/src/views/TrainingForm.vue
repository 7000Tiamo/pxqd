<template>
  <div class="form-container">
    <div class="page-header">
      <el-button @click="$router.back()">← 返回</el-button>
      <h2>{{ isEdit ? '编辑培训' : '发布培训' }}</h2>
    </div>

    <el-card>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="培训名称" prop="title">
              <el-input v-model="form.title" placeholder="例如：2025年第一季度安全培训" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="讲师" prop="trainer">
              <el-input v-model="form.trainer" placeholder="讲师姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="地点" prop="location">
              <el-input v-model="form.location" placeholder="例如：301会议室" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人数限制" prop="maxParticipants">
              <el-input-number v-model="form.maxParticipants" :min="0" placeholder="0表示不限制" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%"
                value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%"
                value-format="YYYY-MM-DDTHH:mm:ss" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="培训介绍" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="6" placeholder="请输入培训详细介绍..." />
        </el-form-item>

        <el-divider>签到规则</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="需要报名">
              <el-switch v-model="form.needSignup" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="需要签退">
              <el-switch v-model="form.needCheckout" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="迟到阈值(分钟)">
              <el-input-number v-model="form.lateMinutes" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="早退阈值(分钟)">
          <el-input-number v-model="form.earlyLeaveMinutes" :min="0" style="width: 100%" />
        </el-form-item>

        <el-form-item>
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存为草稿</el-button>
          <el-button type="success" @click="handlePublish" :loading="saving">发布培训</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTrainingDetail, createTraining, updateTraining, publishTraining } from '@/api/training'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const saving = ref(false)

const isEdit = ref(false)
const trainingId = ref(null)

const form = reactive({
  title: '',
  description: '',
  trainer: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50,
  needSignup: true,
  needCheckout: false,
  lateMinutes: 0,
  earlyLeaveMinutes: 0
})

const rules = {
  title: [{ required: true, message: '请输入培训名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const loadData = async () => {
  if (route.params.id) {
    isEdit.value = true
    trainingId.value = route.params.id
    try {
      const res = await getTrainingDetail(trainingId.value)
      const data = res.data
      Object.assign(form, {
        title: data.title || '',
        description: data.description || '',
        trainer: data.trainer || '',
        location: data.location || '',
        startTime: data.startTime ? dayjs(data.startTime).format('YYYY-MM-DDTHH:mm:ss') : '',
        endTime: data.endTime ? dayjs(data.endTime).format('YYYY-MM-DDTHH:mm:ss') : '',
        maxParticipants: data.maxParticipants || 50,
        needSignup: data.needSignup !== false,
        needCheckout: data.needCheckout === true,
        lateMinutes: data.lateMinutes || 15,
        earlyLeaveMinutes: data.earlyLeaveMinutes || 15
      })
    } catch (error) {
      ElMessage.error('加载失败')
    }
  }
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const data = {
          ...form,
          startTime: form.startTime || null,
          endTime: form.endTime || null
        }

        if (isEdit.value) {
          await updateTraining(trainingId.value, { ...data, status: 'draft' })
          ElMessage.success('保存成功')
        } else {
          await createTraining(data)
          ElMessage.success('创建成功')
        }
        router.push('/trainings')
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handlePublish = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const data = {
          ...form,
          startTime: form.startTime || null,
          endTime: form.endTime || null
        }

        if (isEdit.value) {
          await updateTraining(trainingId.value, data)
          await publishTraining(trainingId.value)
        } else {
          const res = await createTraining(data)
          await publishTraining(res.data.id)
        }
        ElMessage.success('发布成功')
        router.push('/trainings')
      } catch (error) {
        ElMessage.error(error.message || '发布失败')
      } finally {
        saving.value = false
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.form-container {
  max-width: 1200px;
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
</style>
