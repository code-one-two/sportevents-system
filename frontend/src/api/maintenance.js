import request from '@/utils/request'

export function getMaintenancePage(params) {
  return request.get('/maintenances', { params })
}

export function createMaintenance(data) {
  return request.post('/maintenances', data)
}

export function updateMaintenance(id, data) {
  return request.put(`/maintenances/${id}`, data)
}

export function startMaintenance(id) {
  return request.post(`/maintenances/${id}/start`)
}

export function completeMaintenance(id, data) {
  return request.post(`/maintenances/${id}/complete`, data)
}

export function archiveMaintenance(id) {
  return request.post(`/maintenances/${id}/archive`)
}
