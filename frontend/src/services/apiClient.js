const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080')
  .replace(/\/+$/, '')
const TOKEN_KEY = 'fisihub_token'

export async function apiRequest(path, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY)
  const response = await fetch(`${API_URL}${path}`, {
    method: options.method || 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...(options.body ? { body: JSON.stringify(options.body) } : {}),
  })

  if (response.status === 204) return null

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    const error = new Error(data.message || 'No se pudo completar la solicitud')
    error.status = response.status
    error.validationErrors = data.validationErrors || {}
    throw error
  }

  return data
}
