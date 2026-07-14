import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userId: null,
    username: '',
    realName: '',
    roles: [],
    permissions: []
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    hasPermission: (state) => (code) => state.permissions.includes(code)
  },
  actions: {
    async login(loginForm) {
      const res = await loginApi(loginForm)
      const data = res.data
      this.token = data.token
      this.userId = data.userId
      this.username = data.username
      this.realName = data.realName
      this.roles = data.roles || []
      this.permissions = data.permissions || []
    },
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // ignore
      }
      this.$reset()
    }
  },
  persist: {
    key: 'gym-user',
    paths: ['token', 'userId', 'username', 'realName', 'roles', 'permissions']
  }
})
