import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
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

// 路由守卫
router.beforeEach((to, from, next) => {
  const currentUser = localStorage.getItem('currentUser')
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 需要登录的路由
    if (!currentUser) {
      // 未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    // 不需要登录的路由
    if (currentUser && to.path === '/login') {
      // 已登录用户访问登录页，重定向到待办事项页面
      next('/todolist')
    } else {
      next()
    }
  }
})

export default router
