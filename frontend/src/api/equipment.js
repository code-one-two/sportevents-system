import request from '@/utils/request'

export function getEquipmentPage(params) {
  return request.get('/equipments', { params })
}

export function getEquipmentById(id) {
  return request.get(`/equipments/${id}`)
}

export function createEquipment(data) {
  return request.post('/equipments', data)
}

export function updateEquipment(id, data) {
  return request.put(`/equipments/${id}`, data)
}

export function deleteEquipment(id) {
  return request.delete(`/equipments/${id}`)
}

export function getCategories() {
  return request.get('/categories')
}

export function createCategory(data) {
  return request.post('/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}
