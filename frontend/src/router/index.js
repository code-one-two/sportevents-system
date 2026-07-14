import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('@/components/Layout/MainLayout.vue'),
    redirect: () => {
      const userStore = useUserStore()
      return userStore.hasPermission('statistics:view') ? '/dashboard' : '/equipments'
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '控制台', permission: 'statistics:view' }
      },
      {
        path: 'equipments',
        name: 'Equipments',
        component: () => import('@/views/EquipmentView.vue'),
        meta: { title: '器材管理' }
      },
      {
        path: 'borrows',
        name: 'Borrows',
        component: () => import('@/views/BorrowView.vue'),
        meta: { title: '借用管理' }
      },
      {
        path: 'document-assistant',
        name: 'DocumentAssistant',
        component: () => import('@/views/DocumentAssistantView.vue'),
        meta: { title: '文档助手' }
      },
      {
        path: 'maintenances',
        name: 'Maintenances',
        component: () => import('@/views/MaintenanceView.vue'),
        meta: { title: '维修管理', permission: 'maintenance:manage' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/UserView.vue'),
        meta: { title: '用户管理', permission: 'user:manage' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.isLoggedIn) {
    next('/login')
    return
  }
  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  if (to.meta.permission && !userStore.hasPermission(to.meta.permission)) {
    next('/dashboard')
    return
  }
  next()
})

export default router
