import { apiRequest } from './apiClient'

export const getProjectProgressReport = (projectId) =>
  apiRequest(`/api/proyectos/${projectId}/reportes/avance`)
