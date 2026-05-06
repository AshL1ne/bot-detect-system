import http from './http'

export function searchAuthUsers(payload) {
  return http.post('/admin/auth-users/search', payload)
}

export function updateAuthUserRole(userId, payload) {
  return http.put(`/admin/auth-users/${encodeURIComponent(userId)}/role`, payload)
}

export function updateAuthUserStatus(userId, payload) {
  return http.put(`/admin/auth-users/${encodeURIComponent(userId)}/status`, payload)
}

export function deleteAuthUser(userId) {
  return http.delete(`/admin/auth-users/${encodeURIComponent(userId)}`)
}

export function updateUserLabel(userId, payload) {
  return http.put(`/admin/users/${encodeURIComponent(userId)}/label`, payload)
}


