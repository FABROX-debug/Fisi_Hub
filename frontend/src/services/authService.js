const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080')
  .replace(/\/+$/, '')
const TOKEN_KEY = 'fisihub_token'

async function request(path, { method = 'GET', body, token } = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...(body ? { body: JSON.stringify(body) } : {}),
  })

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    const error = new Error(data.message || 'No se pudo completar la solicitud')
    error.status = response.status
    error.validationErrors = data.validationErrors || {}
    throw error
  }

  return data
}

export function register(payload) {
  return request('/api/auth/register', { method: 'POST', body: payload })
}

export function login(payload) {
  return request('/api/auth/login', { method: 'POST', body: payload })
}

export function forgotPassword(payload) {
  return request('/api/auth/forgot-password', {
    method: 'POST',
    body: payload,
  })
}

export function validateResetToken(token) {
  return request(`/api/auth/reset-password/${token}`)
}

export function resetPassword(payload) {
  return request('/api/auth/reset-password', {
    method: 'POST',
    body: payload,
  })
}

export function getMe(token) {
  return request('/api/auth/me', { token })
}

export function saveToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function logout() {
  localStorage.removeItem(TOKEN_KEY)
}
