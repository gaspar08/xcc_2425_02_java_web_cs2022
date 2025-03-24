<script setup>
import { ref } from 'vue'

const props = defineProps({
  todo: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['toggle', 'remove', 'edit'])

const isEditing = ref(false)
const editedContent = ref(props.todo.content)

const startEdit = () => {
  isEditing.value = true
  editedContent.value = props.todo.content
}

const saveEdit = () => {
  if (editedContent.value.trim() && editedContent.value !== props.todo.content) {
    emit('edit', props.todo.id, editedContent.value.trim())
  }
  isEditing.value = false
}

const cancelEdit = () => {
  isEditing.value = false
  editedContent.value = props.todo.content
}

const handleKeydown = (e) => {
  if (e.key === 'Enter') {
    saveEdit()
  } else if (e.key === 'Escape') {
    cancelEdit()
  }
}
</script>

<template>
  <li class="flex items-center gap-4 p-4 bg-white rounded-lg shadow-sm border border-gray-100">
    <input 
      type="checkbox"
      :checked="todo.completed"
      @change="emit('toggle', todo.id)"
      class="w-5 h-5 text-green-500 rounded border-gray-300 focus:ring-green-500"
    >
    
    <!-- 显示模式 -->
    <template v-if="!isEditing">
      <span 
        :class="[
          'flex-1',
          todo.completed ? 'text-gray-400 line-through' : 'text-gray-700'
        ]"
      >{{ todo.content }}</span>
      <span class="text-sm text-gray-400">{{ todo.createdAt }}</span>
      
      <!-- 操作按钮 -->
      <div class="flex gap-2">
        <button 
          @click="startEdit"
          class="p-1 text-blue-500 hover:text-blue-600 transition-colors"
          title="编辑"
        >
          <i class="fas fa-edit"></i>
        </button>
        <button 
          @click="emit('remove', todo.id)"
          class="p-1 text-red-500 hover:text-red-600 transition-colors"
          title="删除"
        >
          <i class="fas fa-trash"></i>
        </button>
      </div>
    </template>

    <!-- 编辑模式 -->
    <template v-else>
      <input
        v-model="editedContent"
        @keydown="handleKeydown"
        @blur="saveEdit"
        class="flex-1 px-2 py-1 border border-gray-300 rounded focus:outline-none focus:border-blue-500"
        ref="editInput"
        v-focus
      >
      <div class="flex gap-2">
        <button 
          @click="saveEdit"
          class="p-1 text-green-500 hover:text-green-600 transition-colors"
          title="保存"
        >
          <i class="fas fa-check"></i>
        </button>
        <button 
          @click="cancelEdit"
          class="p-1 text-gray-500 hover:text-gray-600 transition-colors"
          title="取消"
        >
          <i class="fas fa-times"></i>
        </button>
      </div>
    </template>
  </li>
</template>

<script>
// 自定义指令：自动聚焦
const vFocus = {
  mounted: (el) => el.focus()
}
</script> 