import { create } from 'zustand'
import {
  getNotifications,
  markAllNotificationsRead,
  markNotificationRead,
} from '../services/notificacionService'

let pendingLoad = null

const useNotificationStore = create((set, get) => ({
  notifications: [],
  loading: false,
  error: '',

  load: () => {
    if (pendingLoad) return pendingLoad
    set({ loading: true, error: '' })
    pendingLoad = getNotifications()
      .then((notifications) => {
        set({ notifications, loading: false })
        return notifications
      })
      .catch((error) => {
        set({ error: error.message, loading: false })
        throw error
      })
      .finally(() => {
        pendingLoad = null
      })
    return pendingLoad
  },

  markRead: async (id) => {
    try {
      const updated = await markNotificationRead(id)
      set({
        notifications: get().notifications.map((item) =>
          item.id === id ? updated : item),
        error: '',
      })
    } catch (error) {
      set({ error: error.message })
      throw error
    }
  },

  markAllRead: async () => {
    try {
      await markAllNotificationsRead()
      set({
        notifications: get().notifications.map((item) => ({
          ...item,
          leida: true,
        })),
        error: '',
      })
    } catch (error) {
      set({ error: error.message })
      throw error
    }
  },

  clear: () => set({ notifications: [], error: '', loading: false }),
}))

export default useNotificationStore
