import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: JSON.parse(localStorage.getItem('currentUser')),
    users: JSON.parse(localStorage.getItem('users')) || []
  }),

  actions: {
    login(username, password) {
      const user = this.users.find(u => u.username === username && u.password === password)
      if (user) {
        this.currentUser = user
        localStorage.setItem('currentUser', JSON.stringify(user))
        return true
      }
      return false
    },

    register(username, password) {
      if (this.users.some(u => u.username === username)) {
        return false
      }

      const newUser = {
        id: Date.now(),
        username,
        password
      }
      this.users.push(newUser)
      localStorage.setItem('users', JSON.stringify(this.users))
      return true
    },

    logout() {
      this.currentUser = null
      localStorage.removeItem('currentUser')
    }
  },

  getters: {
    isLoggedIn: (state) => !!state.currentUser
  }
}) 