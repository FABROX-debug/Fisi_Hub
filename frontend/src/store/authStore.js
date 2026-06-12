import { create } from 'zustand'
import {
  getMe,
  getStoredToken,
  login as loginRequest,
  logout as clearStoredToken,
  register as registerRequest,
  saveToken,
} from '../services/authService'

const useAuthStore = create((set, get) => ({
  token: getStoredToken(),
  user: null,
  initialized: false,
  loading: false,
  error: null,

  initialize: async () => {
    if (get().initialized) return

    const token = get().token
    if (!token) {
      set({ initialized: true })
      return
    }

    try {
      const user = await getMe(token)
      set({ user, initialized: true })
    } catch {
      clearStoredToken()
      set({ token: null, user: null, initialized: true })
    }
  },

  login: async (credentials) => {
    set({ loading: true, error: null })
    try {
      const response = await loginRequest(credentials)
      saveToken(response.token)
      set({
        token: response.token,
        user: response.usuario,
        initialized: true,
        loading: false,
      })
      return response.usuario
    } catch (error) {
      set({ loading: false, error: error.message })
      throw error
    }
  },

  register: async (payload) => {
    set({ loading: true, error: null })
    try {
      const response = await registerRequest(payload)
      saveToken(response.token)
      set({
        token: response.token,
        user: response.usuario,
        initialized: true,
        loading: false,
      })
      return response.usuario
    } catch (error) {
      set({ loading: false, error: error.message })
      throw error
    }
  },

  logout: () => {
    clearStoredToken()
    set({
      token: null,
      user: null,
      initialized: true,
      loading: false,
      error: null,
    })
  },

  clearError: () => set({ error: null }),
}))

export default useAuthStore

