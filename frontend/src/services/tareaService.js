import { apiRequest } from './apiClient'

export function getTareas(filters = {}) {
  const params = new URLSearchParams()
  Object.entries(filters).forEach(([key, value]) => {
    if (value) params.set(key, value)
  })
  const query = params.toString()
  return apiRequest(`/api/tareas${query ? `?${query}` : ''}`)
}

export const getMiTrabajo = () => apiRequest('/api/tareas/mi-trabajo')

export const getTareasByProyecto = (projectId) =>
  apiRequest(`/api/proyectos/${projectId}/tareas`)

export const getTarea = (id) => apiRequest(`/api/tareas/${id}`)

export const getTareaDetalle = (id) => apiRequest(`/api/tareas/${id}/detalle`)

export const createTarea = (payload) =>
  apiRequest('/api/tareas', { method: 'POST', body: payload })

export const updateTarea = (id, payload) =>
  apiRequest(`/api/tareas/${id}`, { method: 'PUT', body: payload })

export const updateEstadoTarea = (id, estado) =>
  apiRequest(`/api/tareas/${id}/estado`, {
    method: 'PATCH',
    body: { estado },
  })

export const deleteTarea = (id) =>
  apiRequest(`/api/tareas/${id}`, { method: 'DELETE' })
