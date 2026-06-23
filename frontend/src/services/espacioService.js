import { apiRequest } from './apiClient'

export const getEspacios = () => apiRequest('/api/espacios')

export const getEspacio = (id) => apiRequest(`/api/espacios/${id}`)

export const createEspacio = (payload) =>
  apiRequest('/api/espacios', { method: 'POST', body: payload })

export const updateEspacio = (id, payload) =>
  apiRequest(`/api/espacios/${id}`, { method: 'PUT', body: payload })

export const deleteEspacio = (id) =>
  apiRequest(`/api/espacios/${id}`, { method: 'DELETE' })

export const getProyectosByEspacio = (id) =>
  apiRequest(`/api/espacios/${id}/proyectos`)

export const getWorkspaceMembers = (id) =>
  apiRequest(`/api/espacios/${id}/miembros`)

export const updateWorkspaceMemberRole = (workspaceId, userId, role) =>
  apiRequest(`/api/espacios/${workspaceId}/miembros/${userId}/rol`, {
    method: 'PATCH',
    body: { rol: role },
  })

export const removeWorkspaceMember = (workspaceId, userId) =>
  apiRequest(`/api/espacios/${workspaceId}/miembros/${userId}`, {
    method: 'DELETE',
  })
