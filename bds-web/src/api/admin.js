import http from './http'

export function updateUserLabel(userId, payload) {
  return http.put(`/admin/users/${userId}/label`, payload)
}

