<template>
  <div class="users-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <div>
        <el-button @click="handleImport">批量导入</el-button>
        <el-button @click="handleExport">批量导出</el-button>
        <el-button type="primary" @click="handleCreate">+ 新建用户</el-button>
      </div>
    </div>
    
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="姓名/工号/部门"
            clearable
            @keyup.enter="loadData"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="dept" label="部门" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
              {{ row.role === 'admin' ? '管理员' : '员工' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
    
    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="工号" prop="employeeNo">
          <el-input v-model="form.employeeNo" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="部门" prop="dept">
          <el-input v-model="form.dept" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="form.id">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="批量导入用户" width="500px">
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :on-change="handleFileChange"
        accept=".xlsx,.xls"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传xlsx/xls文件
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleImportSubmit" :loading="importLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserList, createUser, updateUser, deleteUser, importUsers, exportUsers } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建用户')
const importDialogVisible = ref(false)
const importLoading = ref(false)
const uploadFile = ref(null)

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  employeeNo: '',
  dept: '',
  phone: '',
  status: 1
})

const formRef = ref()

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  employeeNo: [{ required: true, message: '请输入工号', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  pagination.page = 1
  loadData()
}

const handleCreate = () => {
  dialogTitle.value = '新建用户'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    employeeNo: row.employeeNo,
    dept: row.dept || '',
    phone: row.phone || '',
    status: row.status
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateUser(form.id, {
            name: form.name,
            employeeNo: form.employeeNo,
            dept: form.dept,
            phone: form.phone,
            status: form.status
          })
          ElMessage.success('更新成功')
        } else {
          await createUser({
            name: form.name,
            employeeNo: form.employeeNo,
            dept: form.dept,
            phone: form.phone
          })
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    employeeNo: '',
    dept: '',
    phone: '',
    status: 1
  })
  formRef.value?.resetFields()
}

const handleImport = () => {
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  uploadFile.value = file.raw
}

const handleImportSubmit = async () => {
  if (!uploadFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  
  importLoading.value = true
  try {
    const res = await importUsers(uploadFile.value)
    ElMessage.success(`成功导入 ${res.data} 条数据`)
    importDialogVisible.value = false
    uploadFile.value = null
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '导入失败')
  } finally {
    importLoading.value = false
  }
}

const handleExport = async () => {
  try {
    const res = await exportUsers()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `用户列表_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.users-container {
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

.search-form {
  margin-bottom: 20px;
}
</style>


