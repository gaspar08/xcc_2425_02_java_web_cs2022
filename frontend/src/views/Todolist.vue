<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'
import { PencilIcon, TrashIcon, CheckIcon, XMarkIcon, ArrowRightOnRectangleIcon } from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 添加登出函数
const handleLogout = () => {
  userStore.clearUser()
  router.push('/login')
}

const todos = ref([])
const newTodo = ref('')
const isLoading = ref(false)

// 获取待办事项列表
const fetchTodos = async () => {
  try {
    isLoading.value = true
    const { data } = await axios.get('/api/v1/todos', {
      params: {
        userId: userStore.userId
      }
    })
    
    if (data.code === 200) {
      todos.value = data.data.items
    }
  } catch (error) {
    console.error('获取待办事项失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 添加新待办事项
const addTodo = async () => {
  if (!newTodo.value.trim()) return
  
  try {
    const { data } = await axios.post('/api/v1/todos', {
      title: newTodo.value
    }, {
      params: {
        userId: userStore.userId
      }
    })
    
    if (data.code === 200) {
      todos.value.push(data.data)
      newTodo.value = ''
    }
  } catch (error) {
    console.error('添加待办事项失败:', error)
  }
}

const editingTodo = ref(null)
const editContent = ref('')

// 删除待办事项
const deleteTodo = async (id) => {
  try {
    const { data } = await axios.delete(`/api/v1/todos/${id}`, {
      params: { userId: userStore.userId }
    })
    
    if (data.code === 200) {
      todos.value = todos.value.filter(todo => todo.id !== id)
    }
  } catch (error) {
    console.error('删除待办事项失败:', error)
  }
}

// 开始编辑
const startEdit = (todo) => {
  editingTodo.value = todo.id
  editContent.value = todo.title
}

// 保存编辑
const saveEdit = async (todo) => {
  try {
    const { data } = await axios.put(`/api/v1/todos/${todo.id}`, {
      title: editContent.value,
      completed: todo.completed
    }, {
      params: { userId: userStore.userId }
    })
    
    if (data.code === 200) {
      const index = todos.value.findIndex(t => t.id === todo.id)
      todos.value[index] = data.data
      editingTodo.value = null
    }
  } catch (error) {
    console.error('更新待办事项失败:', error)
  }
}

onMounted(() => {
  fetchTodos()
})
</script>

<template>
  <div class="container mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-2xl font-bold">待办事项</h1>
      <div class="flex items-center space-x-4">
        <span class="text-gray-600">欢迎, {{ userStore.username }}</span>
        <button 
          @click="handleLogout"
          class="text-gray-500 hover:text-gray-700"
          title="登出"
        >
          <ArrowRightOnRectangleIcon class="h-6 w-6" />
        </button>
      </div>
    </div>

    <!-- 添加新待办事项 -->
    <div class="mb-6">
      <form @submit.prevent="addTodo" class="flex">
        <input 
          v-model="newTodo" 
          type="text" 
          placeholder="添加新的待办事项..." 
          class="flex-1 p-2 border border-gray-300 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-green-500"
        >
        <button 
          type="submit" 
          class="bg-green-500 text-white px-4 py-2 rounded-r-lg hover:bg-green-600 focus:outline-none"
        >
          添加
        </button>
      </form>
    </div>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="text-center py-4">
      <p class="text-gray-500">加载中...</p>
    </div>

    <!-- 待办事项列表 -->
    <div v-else-if="todos.length > 0" class="space-y-4">
      <div v-for="todo in todos" :key="todo.id" class="bg-white p-4 rounded-lg shadow flex justify-between items-center">
        <!-- 编辑模式 -->
        <div v-if="editingTodo === todo.id" class="flex-1 flex space-x-2">
          <input 
            v-model="editContent"
            type="text"
            class="flex-1 p-1 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-green-500"
            @keyup.enter="saveEdit(todo)"
          >
          <button 
            @click="saveEdit(todo)"
            class="text-green-500 hover:text-green-700"
          >
            <CheckIcon class="h-5 w-5" />
          </button>
          <button 
            @click="editingTodo = null"
            class="text-gray-500 hover:text-gray-700"
          >
            <XMarkIcon class="h-5 w-5" />
          </button>
        </div>
        
        <!-- 显示模式 -->
        <span v-else>{{ todo.title }}</span>
        <div v-if="editingTodo !== todo.id" class="flex space-x-3">
          <button 
            @click="startEdit(todo)"
            class="text-blue-500 hover:text-blue-700"
          >
            <PencilIcon class="h-5 w-5" />
          </button>
          <button 
            @click="deleteTodo(todo.id)"
            class="text-red-500 hover:text-red-700"
          >
            <TrashIcon class="h-5 w-5" />
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-10 bg-gray-50 rounded-lg">
      <p class="text-gray-500 mb-4">您还没有待办事项</p>
      <p class="text-gray-400">使用上方的输入框添加您的第一个待办事项</p>
    </div>
  </div>
</template>