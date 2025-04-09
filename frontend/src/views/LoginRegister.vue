<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isLogin = ref(true) // true为登录模式，false为注册模式
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMessage = ref('')
const username = ref('')

// 登录处理
const handleLogin = async () => {
  if (!email.value || !password.value) {
    errorMessage.value = '请填写邮箱和密码'
    return
  }

  try {
    const { data } = await axios.post('/api/v1/auth/login', {
      email: email.value,
      password: password.value
    })
    
    // 登录处理中的保存逻辑修改为：
    if (data.code != 200) {
      errorMessage.value = data.message || '邮箱或密码错误'
      return
    }

    // 保存到 pinia store
    userStore.setUser({
      userId: data.data.userId,
      username: data.data.username,
      token: data.data.token
    })

    // 设置 axios 默认请求头
    axios.defaults.headers.common['Authorization'] = `Bearer ${data.data.token}`

    errorMessage.value = '登录成功'
    setTimeout(() => {
      router.push('/todolist')
    }, 1000)

  } catch (error) {
    console.error('登录请求失败:', error)
    errorMessage.value = '登录失败，请稍后重试'
  }
}

// 注册处理
const handleRegister = async () => {
  if (!email.value || !password.value || !confirmPassword.value || !username.value) {
    errorMessage.value = '请填写所有字段'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  try {
    const { data } = await axios.post('/api/v1/auth/register', {
      username: username.value,
      password: password.value,
      email: email.value
    })

    if (data.code !== 200) {
      if (data.message?.includes('邮箱已存在')) {
        errorMessage.value = '该邮箱已被注册，请使用其他邮箱'
      } else {
        errorMessage.value = data.message || '注册失败，请重试'
      }
      return
    }
    // 注册成功，切换到登录模式
    errorMessage.value = '注册成功，请登录'
    isLogin.value = true
    email.value = ''
    password.value = ''
    confirmPassword.value = ''
  } catch (error) {
    console.error('注册请求失败:', error)
    errorMessage.value = '注册失败，请稍后重试'
  }
}

// 切换登录/注册模式
const toggleMode = () => {
  isLogin.value = !isLogin.value
  email.value = ''
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
            <label for="email" class="sr-only">邮箱</label>
            <input id="email" v-model="email" type="email" required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="邮箱">
          </div>
          <div v-if="!isLogin">
            <label for="username" class="sr-only">用户名</label>
            <input id="username" v-model="username" type="text" required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="用户名">
          </div>
          <div>
            <label for="password" class="sr-only">密码</label>
            <input id="password" v-model="password" type="password" required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="密码">
          </div>
          <div v-if="!isLogin">
            <label for="confirm-password" class="sr-only">确认密码</label>
            <input id="confirm-password" v-model="confirmPassword" type="password" required
              class="appearance-none rounded-lg relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="确认密码">
          </div>

        </div>

        <div>
          <button type="submit"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500">
            {{ isLogin ? '登录' : '注册' }}
          </button>
        </div>
      </form>

      <div class="text-center">
        <button @click="toggleMode" class="text-sm text-green-600 hover:text-green-500">
          {{ isLogin ? '没有账户？立即注册' : '已有账户？立即登录' }}
        </button>
      </div>
    </div>
  </div>
</template>