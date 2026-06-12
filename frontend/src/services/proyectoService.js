import { apiRequest } from './apiClient'

export const getProyectos = () => apiRequest('/api/proyectos')

export const getProyecto = (id) => apiRequest(`/api/proyectos/${id}`)

export const createProyecto = (payload) =>
  apiRequest('/api/proyectos', { method: 'POST', body: payload })

export const updateProyecto = (id, payload) =>
  apiRequest(`/api/proyectos/${id}`, { method: 'PUT', body: payload })

export const deleteProyecto = (id) =>
  apiRequest(`/api/proyectos/${id}`, { method: 'DELETE' })
