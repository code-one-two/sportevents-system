<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6">
        <el-card shadow="hover"><div class="stat-item"><span>器材总数</span><strong>{{ stats.totalEquipment }}</strong></div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><div class="stat-item"><span>借用总次数</span><strong>{{ stats.totalBorrow }}</strong></div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><div class="stat-item"><span>超时未还</span><strong class="danger">{{ stats.overdueCount }}</strong></div></el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover"><div class="stat-item"><span>维修中</span><strong>{{ stats.maintenanceCount }}</strong></div></el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card header="器材使用率">
          <el-table :data="stats.equipmentUsage" size="small" stripe>
            <el-table-column prop="name" label="器材名称" />
            <el-table-column prop="usageRate" label="使用率(%)" />
            <el-table-column prop="borrowCount" label="借出数量" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="借用趋势">
          <el-table :data="stats.borrowTrend" size="small" stripe>
            <el-table-column prop="month" label="月份" />
            <el-table-column prop="count" label="借用次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { getDashboard } from '@/api/statistics'

const stats = reactive({
  totalEquipment: 0,
  totalBorrow: 0,
  overdueCount: 0,
  maintenanceCount: 0,
  equipmentUsage: [],
  borrowTrend: []
})

async function loadData() {
  const res = await getDashboard()
  Object.assign(stats, res.data)
}

onMounted(loadData)
</script>

<style scoped>
.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.stat-item span {
  color: #909399;
  font-size: 14px;
}
.stat-item strong {
  font-size: 28px;
  color: #303133;
}
.stat-item .danger {
  color: #f56c6c;
}
</style>
