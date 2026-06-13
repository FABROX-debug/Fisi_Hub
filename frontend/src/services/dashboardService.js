import { apiRequest } from './apiClient'

export const getDashboardResumen = () =>
  apiRequest('/api/dashboard/resumen')
