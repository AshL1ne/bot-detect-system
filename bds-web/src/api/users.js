import http from './http'

export function searchUsers(payload) {
  return http.post('/users/search', payload)
}

export function getUserDetail(userId) {
  return http.get(`/users/${userId}`)
}

export function getUserWordCloud(userId) {
  return http.get(`/users/${userId}/wordcloud`)
}

export function getUserActiveHours(userId) {
  return http.get(`/users/${userId}/active-hours`)
}

