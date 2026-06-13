import { apiRequest } from './apiClient'

export const getProjectMembers = (projectId) =>
  apiRequest(`/api/proyectos/${projectId}/miembros`)

export const addProjectMember = (projectId, payload) =>
  apiRequest(`/api/proyectos/${projectId}/miembros`, {
    method: 'POST',
    body: payload,
  })

export const updateProjectMemberRole = (projectId, userId, role) =>
  apiRequest(`/api/proyectos/${projectId}/miembros/${userId}/rol`, {
    method: 'PATCH',
    body: { rol: role },
  })

export const removeProjectMember = (projectId, userId) =>
  apiRequest(`/api/proyectos/${projectId}/miembros/${userId}`, {
    method: 'DELETE',
  })

export const getProjectActivity = (projectId) =>
  apiRequest(`/api/proyectos/${projectId}/actividad`)
