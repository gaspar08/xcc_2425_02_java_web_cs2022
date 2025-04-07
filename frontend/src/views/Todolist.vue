<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import TodoItem from '../components/TodoItem.vue'
import TodoForm from '../components/TodoForm.vue'
import TodoFilter from '../components/TodoFilter.vue'

const router = useRouter()
const currentUser = ref(null)
const todos = ref([])
const filter = ref('all') // 筛选状态：all, active, completed
const isLoading = ref(true)
const error = ref(null)

// 从localStorage加载用户数据
const loadUserData = () => {
  try {
    const userStr = localStorage.getItem('currentUser')
    if (!userStr) {
      error.value = '用户未登录'
      setTimeout(() => router.push('/login'), 2000)
      return
    }
    currentUser.value = JSON.parse(userStr)
    //console.log('当前用户数据:', currentUser.value) // 打印当前用户数据，用于调试
  } catch (err) {
    console.error('解析用户数据失败:', err)
    error.value = '获取用户信息失败，请重新登录'
    setTimeout(() => router.push('/login'), 2000)
  }
}

// 从后端加载待办事项数据
const loadTodos = async () => {
  error.value = null
  isLoading.value = true
  try {
    const controller = new AbortController()
    const timeoutId = setTimeout(() => controller.abort(), 10000) // 10秒超时
    //console.log('id:', currentUser.value.id) 
    const { data } = await axios.get('/api/v1/todos', {
      signal: controller.signal,
      params: {
        userId: currentUser.value.userId
      }
    })
    
    clearTimeout(timeoutId)
    
    if (data.code === 200) {
      todos.value = data.data?.items || []
    } else {
      error.value = data.message || '获取待办事项失败'
    }
  } catch (err) {
    console.error('获取待办事项失败:', err)
    if (err.name === 'AbortError') {
      error.value = '获取待办事项超时，请检查网络连接'
    } else {
      error.value = '获取待办事项失败，请稍后重试'
    }
  } finally {
    isLoading.value = false
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

// 在组件挂载时加载数据
onMounted(async () => {
  await loadUserData()
  if (currentUser.value) {
    await loadTodos()
  }
})

// 禁止键盘缩放
const disableZoom = (e) => {
  if ((e.ctrlKey || e.metaKey) && (e.key === '+' || e.key === '-' || e.key === '=' || e.wheelDelta)) {
    e.preventDefault()
    return false
  }
}

// 在组件挂载时添加事件监听
onMounted(() => {
  // 禁止Ctrl/Cmd + 滚轮缩放
  window.addEventListener('wheel', disableZoom, { passive: false })
  // 禁止Ctrl/Cmd + +/- 缩放
  window.addEventListener('keydown', disableZoom)
})

// 在组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('wheel', disableZoom)
  window.removeEventListener('keydown', disableZoom)
})

// 添加新任务
const addTodo = async (content) => {
  try {
    const { data } = await axios.post('/api/v1/todos', {
      title: content
    }, {
      params: {
        userId: currentUser.value.userId
      }
    })

    if (data.code === 200) {
      todos.value.push(data.data)
    } else {
      error.value = data.message || '创建待办事项失败'
    }
  } catch (err) {
    console.error('创建待办事项失败:', err)
    error.value = '创建待办事项失败，请稍后重试'
  }
}

// 删除任务
const removeTodo = async (id) => {
  try {
    const { data } = await axios.delete(`/api/v1/todos/${id}`, {
      params: {
        userId: currentUser.value.userId
      }
    })

    if (data.code === 200) {
      todos.value = todos.value.filter(todo => todo.id !== id)
    } else {
      error.value = data.message || '删除待办事项失败'
    }
  } catch (err) {
    console.error('删除待办事项失败:', err)
    error.value = '删除待办事项失败，请稍后重试'
  }
}

// 编辑任务
const editTodo = async (id, newTitle) => {
  try {
    const { data } = await axios.put(`/api/v1/todos/${id}`, {
      title: newTitle,
      completed: todos.value.find(todo => todo.id === id)?.completed || false
    }, {
      params: {
        userId: currentUser.value.userId
      }
    })

    if (data.code === 200) {
      const todo = todos.value.find(todo => todo.id === id)
      if (todo) {
        todo.title = newTitle
      }
    } else {
      error.value = data.message || '更新待办事项失败'
    }
  } catch (err) {
    console.error('更新待办事项失败:', err)
    error.value = '更新待办事项失败，请稍后重试'
  }
}

// 切换任务状态
const toggleTodo = (id) => {
  const todo = todos.value.find(todo => todo.id === id)
  if (todo) {
    todo.completed = !todo.completed
  }
}

// 删除这个重复的 editTodo 函数
// const editTodo = (id, newContent) => {
//   const todo = todos.value.find(todo => todo.id === id)
//   if (todo) {
//     todo.title = newContent
//   }
// }

// 筛选任务
const filteredTodos = computed(() => {
  switch (filter.value) {
    case 'active':
      return todos.value.filter(todo => !todo.completed)
    case 'completed':
      return todos.value.filter(todo => todo.completed)
    default:
      return todos.value
  }
})
</script>

<template>
  <div class="max-w-2xl mx-auto p-6">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold text-gray-800">待办事项</h1>
      <div class="flex items-center gap-4">
        <span class="text-gray-600">{{ currentUser?.username || '加载中...' }}</span>
        <button 
          @click="logout"
          class="px-4 py-2 text-sm text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors"
        >
          退出登录
        </button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="flex justify-center items-center py-8">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-green-500"></div>
    </div>

    <!-- 错误提示 -->
    <div v-else-if="error" class="text-center py-8 text-red-500">
      {{ error }}
    </div>

    <template v-else>
      <!-- 添加任务表单 -->
      <TodoForm @add="addTodo" />

      <!-- 任务筛选 -->
      <TodoFilter v-model:filter="filter" :current-filter="filter" />

      <!-- 空状态 -->
      <div v-if="todos.length === 0" class="text-center py-12 text-gray-500 bg-gray-50 rounded-lg shadow-sm border border-gray-100">
        <svg class="w-16 h-16 mx-auto mb-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
        </svg>
        <p class="text-xl font-medium mb-3">还没有待办事项</p>
        <p class="text-sm mb-6">创建你的第一个待办事项，开始规划你的一天吧！</p>
        <button 
          @click="document.querySelector('.todo-form input').focus()"
          class="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors focus:outline-none focus:ring-2 focus:ring-green-300"
        >
          添加新待办
        </button>
      </div>

      <!-- 任务列表 -->
      <ul v-else class="space-y-3">
        <TodoItem
          v-for="todo in filteredTodos"
          :key="todo.id"
          :todo="todo"
          @toggle="toggleTodo"
          @remove="removeTodo"
          @edit="editTodo"
        />
      </ul>
    </template>
  </div>
</template>

<style scoped>
/* 移除所有CSS样式，因为我们现在使用Tailwind CSS */
</style>