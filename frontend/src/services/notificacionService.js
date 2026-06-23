import { apiRequest } from './apiClient'

export const getNotifications = () => apiRequest('/api/notificaciones')

export const markNotificationRead = (id) =>
  apiRequest(`/api/notificaciones/${id}/leida`, { method: 'PATCH' })

export const markAllNotificationsRead = () =>
  apiRequest('/api/notificaciones/leer-todas', { method: 'PATCH' })
