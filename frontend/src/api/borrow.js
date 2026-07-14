import request from '@/utils/request'

export function getBorrowPage(params) {
  return request.get('/borrows', { params })
}

export function applyBorrow(data) {
  return request.post('/borrows/apply', data)
}

export function approveBorrow(data) {
  return request.post('/borrows/approve', data)
}

export function returnBorrow(id) {
  return request.post(`/borrows/${id}/return`)
}

export function checkOverdue() {
  return request.post('/borrows/check-overdue')
}
