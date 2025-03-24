import { defineStore } from 'pinia'
import { useUserStore } from './user'

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: [],
    filter: 'all'
  }),

  actions: {
    loadTodos() {
      const userStore = useUserStore()
      const savedTodos = localStorage.getItem(`todos_${userStore.currentUser.id}`)
      this.todos = savedTodos ? JSON.parse(savedTodos) : []
    },

    saveTodos() {
      const userStore = useUserStore()
      localStorage.setItem(`todos_${userStore.currentUser.id}`, JSON.stringify(this.todos))
    },

    addTodo(content) {
      this.todos.push({
        id: Date.now(),
        content,
        completed: false,
        createdAt: new Date().toLocaleString()
      })
      this.saveTodos()
    },

    removeTodo(id) {
      this.todos = this.todos.filter(todo => todo.id !== id)
      this.saveTodos()
    },

    toggleTodo(id) {
      const todo = this.todos.find(todo => todo.id === id)
      if (todo) {
        todo.completed = !todo.completed
        this.saveTodos()
      }
    },

    editTodo(id, newContent) {
      const todo = this.todos.find(todo => todo.id === id)
      if (todo) {
        todo.content = newContent
        this.saveTodos()
      }
    },

    setFilter(filter) {
      this.filter = filter
    }
  },

  getters: {
    filteredTodos: (state) => {
      switch (state.filter) {
        case 'active':
          return state.todos.filter(todo => !todo.completed)
        case 'completed':
          return state.todos.filter(todo => todo.completed)
        default:
          return state.todos
      }
    }
  }
}) 