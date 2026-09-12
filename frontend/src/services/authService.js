import api from './api'

const authService = {
  // Login
  login: async (username, password) => {
    const response = await api.post('/auth/login', { username, password })
    return response.data
  },

  // Register
  register: async (userData) => {
    const response = await api.post('/auth/register', userData)
    return response.data
  },

  // Save user data to localStorage
  saveUserData: (token, username, role) => {
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify({ username, role }))
  },

  // Get current user
  getCurrentUser: () => {
    const userStr = localStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },

  // Logout
  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('token')
  }
}

export default authService
