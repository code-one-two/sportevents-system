<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索编码/名称" clearable style="width: 220px" @keyup.enter="loadData" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px" @change="loadData">
          <el-option v-for="(label, key) in EQUIPMENT_STATUS" :key="key" :label="label" :value="key" />
        </el-select>
        <el-select v-model="query.categoryId" placeholder="分类" clearable style="width: 140px" @change="loadData">
          <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-if="canManage" type="success" @click="openDialog()">新增器材</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="margin-top: 16px">
        <el-table-column prop="equipmentCode" label="编码" width="120" />
        <el-table-column prop="equipmentName" label="名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">{{ EQUIPMENT_STATUS[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column prop="totalQuantity" label="总量" width="80" />
        <el-table-column prop="availableQuantity" label="可用" width="80" />
        <el-table-column prop="location" label="位置" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canManage" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="canManage" link type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑器材' : '新增器材'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="编码" prop="equipmentCode">
          <el-input v-model="form.equipmentCode" />
        </el-form-item>
        <el-form-item label="名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option v-for="(label, key) in EQUIPMENT_STATUS" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="总数量" prop="totalQuantity">
          <el-input-number v-model="form.totalQuantity" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
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
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { EQUIPMENT_STATUS } from '@/utils/constants'
import {
  getEquipmentPage,
  createEquipment,
  updateEquipment,
  deleteEquipment,
  getCategories
} from '@/api/equipment'

const userStore = useUserStore()
const canManage = computed(() => userStore.hasPermission('equipment:manage'))

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', status: '', categoryId: null })
const form = reactive({
  id: null,
  equipmentCode: '',
  equipmentName: '',
  categoryId: null,
  status: 'NORMAL',
  totalQuantity: 0,
  location: '',
  description: ''
})

const rules = {
  equipmentCode: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  totalQuantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

async function loadCategories() {
  const res = await getCategories()
  categories.value = res.data
}

async function loadData() {
  loading.value = true
  try {
    const res = await getEquipmentPage(query)
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
      equipmentCode: row.equipmentCode,
      equipmentName: row.equipmentName,
      categoryId: row.categoryId,
      status: row.status,
      totalQuantity: row.totalQuantity,
      location: row.location,
      description: row.description
    })
  } else {
    Object.assign(form, {
      id: null,
      equipmentCode: '',
      equipmentName: '',
      categoryId: categories.value[0]?.id || null,
      status: 'NORMAL',
      totalQuantity: 0,
      location: '',
      description: ''
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  if (form.id) {
    await updateEquipment(form.id, form)
    ElMessage.success('更新成功')
  } else {
    await createEquipment(form)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除器材「${row.equipmentName}」？`, '提示', { type: 'warning' })
  await deleteEquipment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  await loadCategories()
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
