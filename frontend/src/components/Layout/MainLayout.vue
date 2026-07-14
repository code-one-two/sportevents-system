<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">器材管理系统</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409eff"
      >
        <el-menu-item v-if="userStore.hasPermission('statistics:view')" index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/equipments">
          <el-icon><Box /></el-icon>
          <span>器材管理</span>
        </el-menu-item>
        <el-menu-item index="/borrows">
          <el-icon><Document /></el-icon>
          <span>借用管理</span>
        </el-menu-item>
        <el-menu-item index="/document-assistant">
          <el-icon><ChatDotRound /></el-icon>
          <span>文档助手</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.hasPermission('maintenance:manage')" index="/maintenances">
          <el-icon><Tools /></el-icon>
          <span>维修管理</span>
        </el-menu-item>
        <el-menu-item v-if="userStore.hasPermission('user:manage')" index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ currentTitle }}</span>
        <div class="header-right">
          <span>{{ userStore.realName || userStore.username }}</span>
          <el-button type="danger" link @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentTitle = computed(() => route.meta.title || '体育馆器材管理系统')

async function handleLogout() {
  await ElMessageBox.confirm('确定退出登录？', '提示', { type: 'warning' })
  await userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  height: 100%;
}
.aside {
  background: #001529;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.main {
  background: #f5f7fa;
}
</style>
