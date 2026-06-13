import { apiRequest } from './apiClient'

export const getTaskComments = (taskId) =>
  apiRequest(`/api/tareas/${taskId}/comentarios`)

export const createTaskComment = (taskId, contenido) =>
  apiRequest(`/api/tareas/${taskId}/comentarios`, {
    method: 'POST',
    body: { contenido },
  })

export const deleteComment = (commentId) =>
  apiRequest(`/api/comentarios/${commentId}`, { method: 'DELETE' })
