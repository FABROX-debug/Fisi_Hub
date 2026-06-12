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
