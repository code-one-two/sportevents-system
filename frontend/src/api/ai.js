import request from '@/utils/request'

export function askDocumentAssistant(data) {
  return request.post('/ai/document-assistant', data, { timeout: 120000 })
}
