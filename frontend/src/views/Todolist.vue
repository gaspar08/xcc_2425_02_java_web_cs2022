<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import TodoItem from '../components/TodoItem.vue'
import TodoForm from '../components/TodoForm.vue'
import TodoFilter from '../components/TodoFilter.vue'

const router = useRouter()
const currentUser = JSON.parse(localStorage.getItem('currentUser'))

// 从localStorage加载数据
const loadTodos = () => {
  const savedTodos = localStorage.getItem(`todos_${currentUser.id}`)
  return savedTodos ? JSON.parse(savedTodos) : []
}

// 定义任务列表数据结构
const todos = ref(loadTodos())
const filter = ref('all') // 筛选状态：all, active, completed

// 监听todos的变化，保存到localStorage
watch(todos, (newTodos) => {
  localStorage.setItem(`todos_${currentUser.id}`, JSON.stringify(newTodos))
}, { deep: true })

// 退出登录
const logout = () => {
  localStorage.removeItem('currentUser')
  router.push('/login')
}

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
const addTodo = (content) => {
  todos.value.push({
    id: Date.now(),
    content,
    completed: false,
    createdAt: new Date().toLocaleString()
  })
}

// 删除任务
const removeTodo = (id) => {
  todos.value = todos.value.filter(todo => todo.id !== id)
}

// 切换任务状态
const toggleTodo = (id) => {
  const todo = todos.value.find(todo => todo.id === id)
  if (todo) {
    todo.completed = !todo.completed
  }
}

// 编辑任务
const editTodo = (id, newContent) => {
  const todo = todos.value.find(todo => todo.id === id)
  if (todo) {
    todo.content = newContent
  }
}

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
        <span class="text-gray-600">{{ currentUser.username }}</span>
        <button 
          @click="logout"
          class="px-4 py-2 text-sm text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors"
        >
          退出登录
        </button>
      </div>
    </div>
    
    <!-- 添加任务表单 -->
    <TodoForm @add="addTodo" />

    <!-- 任务筛选 -->
    <TodoFilter v-model:filter="filter" :current-filter="filter" />

    <!-- 任务列表 -->
    <ul class="space-y-3">
      <TodoItem
        v-for="todo in filteredTodos"
        :key="todo.id"
        :todo="todo"
        @toggle="toggleTodo"
        @remove="removeTodo"
        @edit="editTodo"
      />
    </ul>
  </div>
</template>

<style scoped>
/* 移除所有CSS样式，因为我们现在使用Tailwind CSS */
</style>