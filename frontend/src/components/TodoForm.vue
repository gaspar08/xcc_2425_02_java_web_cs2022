<script setup>
import { ref } from 'vue'

const newTodo = ref('')
const emit = defineEmits(['add'])

const addTodo = () => {
  if (newTodo.value.trim()) {
    emit('add', newTodo.value.trim())
    newTodo.value = ''
  }
}
</script>

<template>
  <div class="flex gap-4 mb-6 todo-form">
    <input 
      v-model="newTodo"
      @keyup.enter="addTodo"
      placeholder="请输入新的待办事项"
      class="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:border-green-500"
    >
    <button 
      @click="addTodo"
      class="px-6 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors"
    >
      添加
    </button>
  </div>
</template>