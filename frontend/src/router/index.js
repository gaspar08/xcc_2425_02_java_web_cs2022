import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginRegister.vue')
    },
    {
      path: '/todolist',
      name: 'todolist',
      component: () => import('../views/Todolist.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 尝试从 localStorage 恢复用户状态
  const isLoggedIn = userStore.token || userStore.initializeFromStorage()
  
  // 需要登录的路由
  if (to.path === '/todolist' && !isLoggedIn) {
    next('/login')
    return
  }
  
  // 已登录用户访问登录页
  if (to.path === '/login' && isLoggedIn) {
    next('/todolist')
    return
  }
  
  next()
})

export default router
