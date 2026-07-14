CREATE DATABASE IF NOT EXISTS gym_equipment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gym_equipment;

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code   VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色编码',
    role_name   VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) DEFAULT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type   VARCHAR(20)  DEFAULT 'API' COMMENT '资源类型',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP
) COMMENT '权限表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    real_name   VARCHAR(100) DEFAULT NULL,
    phone       VARCHAR(20)  DEFAULT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    status      TINYINT      DEFAULT 1 COMMENT '1启用 0禁用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 用户角色关联
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联';

-- 角色权限关联
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联';

-- 器材分类
CREATE TABLE IF NOT EXISTS equipment_category (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    description   VARCHAR(255) DEFAULT NULL,
    sort_order    INT          DEFAULT 0,
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '器材分类';

-- 器材
CREATE TABLE IF NOT EXISTS equipment (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_code  VARCHAR(50)  NOT NULL UNIQUE,
    equipment_name  VARCHAR(100) NOT NULL,
    category_id     BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT 'NORMAL/REPAIR/SCRAPPED/BORROWED',
    total_quantity  INT          NOT NULL DEFAULT 0,
    available_quantity INT       NOT NULL DEFAULT 0,
    location        VARCHAR(200) DEFAULT NULL,
    description     VARCHAR(500) DEFAULT NULL,
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) COMMENT '器材';

-- 借用记录
CREATE TABLE IF NOT EXISTS borrow_record (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    borrow_no       VARCHAR(50)  NOT NULL UNIQUE,
    user_id         BIGINT       NOT NULL,
    equipment_id    BIGINT       NOT NULL,
    quantity        INT          NOT NULL DEFAULT 1,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/BORROWED/RETURNED/OVERDUE',
    apply_reason    VARCHAR(500) DEFAULT NULL,
    expected_return DATETIME     NOT NULL,
    actual_return   DATETIME     DEFAULT NULL,
    approver_id     BIGINT       DEFAULT NULL,
    approve_time    DATETIME     DEFAULT NULL,
    approve_remark  VARCHAR(500) DEFAULT NULL,
    overdue_remind  TINYINT      DEFAULT 0 COMMENT '超时提醒 0否 1是',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_equipment (equipment_id),
    INDEX idx_status (status)
) COMMENT '借用记录';

-- 维修记录
CREATE TABLE IF NOT EXISTS maintenance_record (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    maintenance_no  VARCHAR(50)  NOT NULL UNIQUE,
    equipment_id    BIGINT       NOT NULL,
    reporter_id     BIGINT       NOT NULL,
    handler_id      BIGINT       DEFAULT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/IN_PROGRESS/COMPLETED/ARCHIVED',
    fault_desc      VARCHAR(500) NOT NULL,
    repair_desc     VARCHAR(500) DEFAULT NULL,
    cost            DECIMAL(10,2) DEFAULT 0,
    start_time      DATETIME     DEFAULT NULL,
    finish_time     DATETIME     DEFAULT NULL,
    archive_time    DATETIME     DEFAULT NULL,
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipment (equipment_id),
    INDEX idx_status (status)
) COMMENT '维修记录';

-- 初始化角色
INSERT IGNORE INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '管理员', '系统管理员，拥有全部权限'),
('EQUIPMENT_ADMIN', '设备管理员', '负责器材与维修管理'),
('USER', '普通用户', '可借用器材');

-- 初始化权限
INSERT IGNORE INTO sys_permission (permission_code, permission_name) VALUES
('user:manage', '用户管理'),
('equipment:manage', '器材管理'),
('category:manage', '分类管理'),
('borrow:manage', '借用管理'),
('borrow:apply', '借用申请'),
('maintenance:manage', '维修管理'),
('statistics:view', '统计查看');

-- 管理员拥有全部权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 设备管理员权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code IN ('equipment:manage','category:manage','maintenance:manage','statistics:view','borrow:manage');

-- 普通用户权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT 3, id FROM sys_permission WHERE permission_code IN ('borrow:apply','statistics:view');

-- 用户由应用启动时 DataInitializer 自动初始化（theRenter/admin 密码均为 123123）

-- 初始化分类与器材
INSERT IGNORE INTO equipment_category (category_name, description, sort_order) VALUES
('球类', '篮球、足球等', 1),
('健身器材', '哑铃、跑步机等', 2),
('羽毛球', '球拍、羽毛球等', 3);

INSERT IGNORE INTO equipment (equipment_code, equipment_name, category_id, status, total_quantity, available_quantity, location) VALUES
('EQ001', '篮球', 1, 'NORMAL', 20, 20, 'A区-1号柜'),
('EQ002', '哑铃 10kg', 2, 'NORMAL', 10, 10, 'B区-2号架'),
('EQ003', '羽毛球拍', 3, 'NORMAL', 15, 15, 'C区-1号柜');
