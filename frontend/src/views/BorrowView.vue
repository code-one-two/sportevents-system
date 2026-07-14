<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px" @change="loadData">
          <el-option v-for="(label, key) in BORROW_STATUS" :key="key" :label="label" :value="key" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button v-if="canApply" type="success" @click="openApplyDialog">申请借用</el-button>
        <el-button v-if="canManage" @click="handleCheckOverdue">检查超时</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe style="margin-top: 16px">
        <el-table-column prop="borrowNo" label="单号" width="180" />
        <el-table-column prop="realName" label="借用人" width="100" />
        <el-table-column prop="equipmentName" label="器材" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">{{ BORROW_STATUS[row.status] || row.status }}</template>
        </el-table-column>
        <el-table-column prop="expectedReturn" label="预计归还" width="170">
          <template #default="{ row }">{{ formatDateTime(row.expectedReturn) }}</template>
        </el-table-column>
        <el-table-column prop="overdueRemind" label="超时提醒" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.overdueRemind === 1" type="danger">已提醒</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="canManage && row.status === 'PENDING'">
              <el-button link type="success" @click="handleApprove(row, true)">批准</el-button>
              <el-button link type="danger" @click="handleApprove(row, false)">拒绝</el-button>
            </template>
            <el-button
              v-if="canManage && (row.status === 'BORROWED' || row.status === 'OVERDUE')"
              link
              type="primary"
              @click="handleReturn(row)"
            >
              归还
            </el-button>
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

    <el-dialog v-model="applyVisible" title="申请借用" width="480px">
      <el-form ref="applyRef" :model="applyForm" :rules="applyRules" label-width="100px">
        <el-form-item label="器材" prop="equipmentId">
          <el-select v-model="applyForm.equipmentId" filterable style="width: 100%">
            <el-option
              v-for="e in equipments"
              :key="e.id"
              :label="`${e.equipmentName} (可用:${e.availableQuantity})`"
              :value="e.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="applyForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计归还" prop="expectedReturn">
          <el-date-picker
            v-model="applyForm.expectedReturn"
            type="datetime"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.applyReason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { BORROW_STATUS, formatDateTime } from '@/utils/constants'
import { getBorrowPage, applyBorrow, approveBorrow, returnBorrow, checkOverdue } from '@/api/borrow'
import { getEquipmentPage } from '@/api/equipment'

const userStore = useUserStore()
const canApply = computed(() => userStore.hasPermission('borrow:apply'))
const canManage = computed(() => userStore.hasPermission('borrow:manage'))

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const equipments = ref([])
const applyVisible = ref(false)
const applyRef = ref()

const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const applyForm = reactive({
  equipmentId: null,
  quantity: 1,
  expectedReturn: '',
  applyReason: ''
})

const applyRules = {
  equipmentId: [{ required: true, message: '请选择器材', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  expectedReturn: [{ required: true, message: '请选择归还时间', trigger: 'change' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getBorrowPage(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadEquipments() {
  const res = await getEquipmentPage({ pageNum: 1, pageSize: 100, status: 'NORMAL' })
  equipments.value = res.data.records.filter((e) => e.availableQuantity > 0)
}

function openApplyDialog() {
  applyForm.equipmentId = equipments.value[0]?.id || null
  applyForm.quantity = 1
  applyForm.expectedReturn = ''
  applyForm.applyReason = ''
  applyVisible.value = true
}

async function submitApply() {
  await applyRef.value.validate()
  await applyBorrow(applyForm)
  ElMessage.success('申请已提交')
  applyVisible.value = false
  loadData()
}

async function handleApprove(row, approved) {
  const action = approved ? '批准' : '拒绝'
  await ElMessageBox.confirm(`确定${action}该借用申请？`, '提示', { type: 'warning' })
  await approveBorrow({ id: row.id, approved, approveRemark: action })
  ElMessage.success(`${action}成功`)
  loadData()
}

async function handleReturn(row) {
  await ElMessageBox.confirm('确认归还该器材？', '提示', { type: 'warning' })
  await returnBorrow(row.id)
  ElMessage.success('归还成功')
  loadData()
}

async function handleCheckOverdue() {
  await checkOverdue()
  ElMessage.success('超时检查完成')
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
  flex-wrap: wrap;
}
</style>
