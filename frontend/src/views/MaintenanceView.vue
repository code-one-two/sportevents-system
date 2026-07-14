<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px" @change="loadData">
          <el-option v-for="(label, key) in MAINTENANCE_STATUS" :key="key" :label="label" :value="key" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新建维修</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="margin-top: 16px">
        <el-table-column prop="maintenanceNo" label="单号" width="180" />
        <el-table-column prop="equipmentName" label="器材" />
        <el-table-column prop="reporterName" label="报修人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">{{ MAINTENANCE_STATUS[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column prop="faultDesc" label="故障描述" show-overflow-tooltip />
        <el-table-column prop="cost" label="费用" width="80" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" link type="primary" @click="handleStart(row)">开始</el-button>
            <el-button v-if="row.status === 'IN_PROGRESS'" link type="success" @click="openComplete(row)">完成</el-button>
            <el-button v-if="row.status === 'COMPLETED'" link @click="handleArchive(row)">归档</el-button>
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

    <el-dialog v-model="dialogVisible" title="新建维修记录" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="器材" prop="equipmentId">
          <el-select v-model="form.equipmentId" filterable style="width: 100%">
            <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="form.faultDesc" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="completeVisible" title="完成维修" width="480px">
      <el-form ref="completeRef" :model="completeForm" label-width="90px">
        <el-form-item label="维修说明">
          <el-input v-model="completeForm.repairDesc" type="textarea" />
        </el-form-item>
        <el-form-item label="费用">
          <el-input-number v-model="completeForm.cost" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComplete">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MAINTENANCE_STATUS } from '@/utils/constants'
import {
  getMaintenancePage,
  createMaintenance,
  startMaintenance,
  completeMaintenance,
  archiveMaintenance
} from '@/api/maintenance'
import { getEquipmentPage } from '@/api/equipment'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const equipments = ref([])
const dialogVisible = ref(false)
const completeVisible = ref(false)
const formRef = ref()
const completeRef = ref()
const currentRow = ref(null)

const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const form = reactive({ equipmentId: null, faultDesc: '' })
const completeForm = reactive({ repairDesc: '', cost: 0 })

const rules = {
  equipmentId: [{ required: true, message: '请选择器材', trigger: 'change' }],
  faultDesc: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMaintenancePage(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadEquipments() {
  const res = await getEquipmentPage({ pageNum: 1, pageSize: 100 })
  equipments.value = res.data.records
}

function openDialog() {
  form.equipmentId = equipments.value[0]?.id || null
  form.faultDesc = ''
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  await createMaintenance(form)
  ElMessage.success('创建成功')
  dialogVisible.value = false
  loadData()
}

async function handleStart(row) {
  await startMaintenance(row.id)
  ElMessage.success('已开始维修')
  loadData()
}

function openComplete(row) {
  currentRow.value = row
  completeForm.repairDesc = ''
  completeForm.cost = 0
  completeVisible.value = true
}

async function submitComplete() {
  await completeMaintenance(currentRow.value.id, completeForm)
  ElMessage.success('维修已完成')
  completeVisible.value = false
  loadData()
}

async function handleArchive(row) {
  await ElMessageBox.confirm('确定归档该维修记录？', '提示', { type: 'warning' })
  await archiveMaintenance(row.id)
  ElMessage.success('归档成功')
  loadData()
}

onMounted(async () => {
  await loadEquipments()
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
}
</style>
