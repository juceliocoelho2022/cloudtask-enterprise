export const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function api(path, options = {}) {
  const token = localStorage.getItem('cloudtask_token')
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  }

  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers
  })

  if (response.status === 204) return null

  const body = await response.json().catch(() => null)

  if (!response.ok) {
    const message = body?.message || `Erro HTTP ${response.status}`
    throw new Error(message)
  }

  return body
}
