<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索用户名/姓名" clearable style="width: 220px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新增用户</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="margin-top: 16px">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="phone" label="手机" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="roles" label="角色">
          <template #default="{ row }">{{ (row.roles || []).join(', ') }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="密码" :prop="form.id ? '' : 'password'">
          <el-input v-model="form.password" type="password" :placeholder="form.id ? '留空则不修改' : '默认123123'" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPage, createUser, updateUser, deleteUser, getRoles } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roles = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  roleId: null,
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function loadRoles() {
  const res = await getRoles()
  roles.value = res.data
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  if (row) {
    Object.assign(form, {
      id: row.id,
      username: row.username,
      password: '',
      realName: row.realName,
      phone: row.phone,
      email: row.email,
      roleId: roles.value.find((r) => row.roles?.includes(r.roleName))?.id || null,
      status: row.status
    })
  } else {
    Object.assign(form, {
      id: null,
      username: '',
      password: '',
      realName: '',
      phone: '',
      email: '',
      roleId: roles.value[0]?.id || null,
      status: 1
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  if (form.id) {
    await updateUser(form.id, form)
    ElMessage.success('更新成功')
  } else {
    await createUser(form)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」？`, '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  await loadRoles()
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
}
</style>
