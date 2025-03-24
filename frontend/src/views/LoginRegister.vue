<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLogin = ref(true) // true为登录模式，false为注册模式
const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')

// 从localStorage获取用户数据
const getUsers = () => {
  const users = localStorage.getItem('users')
  return users ? JSON.parse(users) : []
}

// 保存用户数据到localStorage
const saveUsers = (users) => {
  localStorage.setItem('users', JSON.stringify(users))
}

// 登录处理
const handleLogin = () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请填写用户名和密码'
    return
  }

  const users = getUsers()
  const user = users.find(u => u.username === username.value && u.password === password.value)

  if (user) {
    // 保存登录状态
    localStorage.setItem('currentUser', JSON.stringify(user))
    // 跳转到待办事项页面
    router.push('/todolist')
  } else {
    errorMessage.value = '用户名或密码错误'
  }
}

// 注册处理
const handleRegister = () => {
  if (!username.value || !password.value || !confirmPassword.value) {
    errorMessage.value = '请填写所有字段'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  const users = getUsers()
  if (users.some(u => u.username === username.value)) {
    errorMessage.value = '用户名已存在'
    return
  }

  // 添加新用户
  users.push({
    id: Date.now(),
    username: username.value,
    password: password.value
  })
  saveUsers(users)

  // 切换到登录模式
  isLogin.value = true
  username.value = ''
  password.value = ''
  confirmPassword.value = ''
  errorMessage.value = '注册成功，请登录'
}

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  username.value = ''
  password.value = ''
  confirmPassword.value = ''
  errorMessage.value = ''
}
</script>

<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8 bg-white p-8 rounded-lg shadow-md">
      <div>
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          {{ isLogin ? '登录账户' : '注册账户' }}
        </h2>
      </div>
      
      <!-- 错误信息 -->
      <div v-if="errorMessage" class="bg-red-50 text-red-500 p-3 rounded text-center">
        {{ errorMessage }}
      </div>

      <form class="mt-8 space-y-6" @submit.prevent="isLogin ? handleLogin() : handleRegister()">
        <div class="rounded-md shadow-sm space-y-4">
          <div>
            <label for="username" class="sr-only">用户名</label>
            <input
              id="username"
              v-model="username"
              type="text"
              required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="用户名"
            >
          </div>
          <div>
            <label for="password" class="sr-only">密码</label>
            <input
              id="password"
              v-model="password"
              type="password"
              required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="密码"
            >
          </div>
          <div v-if="!isLogin">
            <label for="confirm-password" class="sr-only">确认密码</label>
            <input
              id="confirm-password"
              v-model="confirmPassword"
              type="password"
              required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="确认密码"
            >
          </div>
        </div>

        <div>
          <button
            type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500"
          >
            {{ isLogin ? '登录' : '注册' }}
          </button>
        </div>
      </form>

      <div class="text-center">
        <button
          @click="toggleMode"
          class="text-sm text-green-600 hover:text-green-500"
        >
          {{ isLogin ? '没有账户？立即注册' : '已有账户？立即登录' }}
        </button>
      </div>
    </div>
  </div>
</template> 