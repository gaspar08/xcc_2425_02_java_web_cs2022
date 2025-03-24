<script setup>
defineProps({
  currentFilter: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:filter'])
</script>

<template>
  <div class="flex gap-4 mb-6">
    <button 
      :class="[
        'px-4 py-2 rounded-lg transition-colors',
        currentFilter === 'all' 
          ? 'bg-green-500 text-white' 
          : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
      ]"
      @click="emit('update:filter', 'all')"
    >全部</button>
    <button 
      :class="[
        'px-4 py-2 rounded-lg transition-colors',
        currentFilter === 'active' 
          ? 'bg-green-500 text-white' 
          : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
      ]"
      @click="emit('update:filter', 'active')"
    >未完成</button>
    <button 
      :class="[
        'px-4 py-2 rounded-lg transition-colors',
        currentFilter === 'completed' 
          ? 'bg-green-500 text-white' 
          : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
      ]"
      @click="emit('update:filter', 'completed')"
    >已完成</button>
  </div>
</template> 