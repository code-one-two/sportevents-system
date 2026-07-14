MERGE INTO sys_role (id, role_code, role_name, description) KEY(id) VALUES
(1, 'ADMIN', '管理员', '系统管理员，拥有全部权限'),
(2, 'EQUIPMENT_ADMIN', '设备管理员', '负责器材与维修管理'),
(3, 'USER', '普通用户', '可借用器材');

MERGE INTO sys_permission (id, permission_code, permission_name, resource_type) KEY(id) VALUES
(1, 'user:manage', '用户管理', 'API'),
(2, 'equipment:manage', '器材管理', 'API'),
(3, 'category:manage', '分类管理', 'API'),
(4, 'borrow:manage', '借用管理', 'API'),
(5, 'borrow:apply', '借用申请', 'API'),
(6, 'maintenance:manage', '维修管理', 'API'),
(7, 'statistics:view', '统计查看', 'API');

MERGE INTO sys_role_permission (role_id, permission_id) KEY(role_id, permission_id)
SELECT 1, id FROM sys_permission;

MERGE INTO sys_role_permission (role_id, permission_id) KEY(role_id, permission_id)
SELECT 2, id FROM sys_permission
WHERE permission_code IN ('equipment:manage', 'category:manage', 'maintenance:manage', 'statistics:view', 'borrow:manage');

MERGE INTO sys_role_permission (role_id, permission_id) KEY(role_id, permission_id)
SELECT 3, id FROM sys_permission
WHERE permission_code IN ('borrow:apply', 'statistics:view');

MERGE INTO equipment_category (id, category_name, description, sort_order) KEY(id) VALUES
(1, '球类', '篮球、足球等', 1),
(2, '健身器材', '哑铃、跑步机等', 2),
(3, '羽毛球', '球拍、羽毛球等', 3);

MERGE INTO equipment (id, equipment_code, equipment_name, category_id, status, total_quantity, available_quantity, location) KEY(id) VALUES
(1, 'EQ001', '篮球', 1, 'NORMAL', 20, 20, 'A区-1号柜'),
(2, 'EQ002', '哑铃 10kg', 2, 'NORMAL', 10, 10, 'B区-2号架'),
(3, 'EQ003', '羽毛球拍', 3, 'NORMAL', 15, 15, 'C区-1号柜');
