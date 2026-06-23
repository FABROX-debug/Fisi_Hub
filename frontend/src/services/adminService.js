import { apiRequest } from './apiClient'

export const getAdminUsers = () => apiRequest('/api/admin/usuarios')

export const activateUser = (id) =>
  apiRequest(`/api/admin/usuarios/${id}/activar`, { method: 'PATCH' })

export const deactivateUser = (id) =>
  apiRequest(`/api/admin/usuarios/${id}/desactivar`, { method: 'PATCH' })

export const getAdminProjects = () => apiRequest('/api/admin/proyectos')

export const getAdminStats = () => apiRequest('/api/admin/estadisticas')
