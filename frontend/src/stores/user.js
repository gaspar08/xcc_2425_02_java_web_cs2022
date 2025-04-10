import { defineStore } from 'pinia'
import request from '@/utils/request'  // 改用封装的 request

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    username: null,
    token: null
  }),
  actions: {
    setUser(userData) {
      this.userId = userData.userId
      this.username = userData.username
      this.token = userData.token
      
      // 保存到 localStorage
      localStorage.setItem('token', userData.token)
      localStorage.setItem('user', JSON.stringify({
        userId: userData.userId,
        username: userData.username
      }))
      
      // 设置 axios 请求头
      // 移除这行，因为已经在 request 拦截器中处理
      // axios.defaults.headers.common['Authorization'] = `Bearer ${userData.token}`
    },
    
    clearUser() {
      this.userId = null
      this.username = null
      this.token = null
      
      // 清除 localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      
      // 清除 axios 请求头
      // 移除这行，因为已经在 request 拦截器中处理
      // delete axios.defaults.headers.common['Authorization']
    },
    
    // 确保方法名完全一致
    initializeFromStorage() {
      const token = localStorage.getItem('token')
      const user = JSON.parse(localStorage.getItem('user'))
      
      if (token && user) {
        this.setUser({
          userId: user.userId,
          username: user.username,
          token: token
        })
        return true
      }
      return false
    }
  }
})