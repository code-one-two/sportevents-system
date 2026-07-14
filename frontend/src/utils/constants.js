export const EQUIPMENT_STATUS = {
  NORMAL: '正常',
  REPAIR: '维修',
  SCRAPPED: '报废',
  BORROWED: '借出'
}

export const BORROW_STATUS = {
  PENDING: '待审批',
  APPROVED: '已批准',
  REJECTED: '已拒绝',
  BORROWED: '借用中',
  RETURNED: '已归还',
  OVERDUE: '已超时'
}

export const MAINTENANCE_STATUS = {
  PENDING: '待处理',
  IN_PROGRESS: '维修中',
  COMPLETED: '已完成',
  ARCHIVED: '已归档'
}

export function formatDateTime(value) {
  if (!value) return '-'
  return value.replace('T', ' ').substring(0, 19)
}
