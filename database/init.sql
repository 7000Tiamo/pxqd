-- 初始化数据库：training_db

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(64) NOT NULL,
    employee_no VARCHAR(64),
    dept VARCHAR(64),
    phone VARCHAR(32),
    role VARCHAR(16) NOT NULL DEFAULT 'employee',
    status INT NOT NULL DEFAULT 1,
    password VARCHAR(128) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

-- 用户表字段注释
COMMENT ON TABLE users IS '用户表';
COMMENT ON COLUMN users.id IS '用户ID，主键';
COMMENT ON COLUMN users.username IS '用户名，唯一标识';
COMMENT ON COLUMN users.name IS '姓名';
COMMENT ON COLUMN users.employee_no IS '工号';
COMMENT ON COLUMN users.dept IS '部门';
COMMENT ON COLUMN users.phone IS '手机号';
COMMENT ON COLUMN users.role IS '角色：admin-管理员，employee-员工';
COMMENT ON COLUMN users.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN users.password IS '密码（MD5加密）';
COMMENT ON COLUMN users.created_at IS '创建时间';
COMMENT ON COLUMN users.updated_at IS '更新时间';
COMMENT ON COLUMN users.deleted IS '删除标记：0-未删除，1-已删除';

-- 培训表
CREATE TABLE IF NOT EXISTS trainings (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    trainer VARCHAR(100),
    location VARCHAR(200),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    cover_url VARCHAR(255),
    status VARCHAR(20) DEFAULT 'draft',
    need_signup BOOLEAN DEFAULT TRUE,
    need_checkout BOOLEAN DEFAULT FALSE,
    gps_required BOOLEAN DEFAULT FALSE,
    late_minutes INT DEFAULT 15,
    early_leave_minutes INT DEFAULT 10,
    max_participants INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

-- 培训表字段注释
COMMENT ON TABLE trainings IS '培训表';
COMMENT ON COLUMN trainings.id IS '培训ID，主键';
COMMENT ON COLUMN trainings.title IS '培训名称';
COMMENT ON COLUMN trainings.description IS '培训介绍';
COMMENT ON COLUMN trainings.trainer IS '讲师姓名';
COMMENT ON COLUMN trainings.location IS '培训地点';
COMMENT ON COLUMN trainings.start_time IS '开始时间';
COMMENT ON COLUMN trainings.end_time IS '结束时间';
COMMENT ON COLUMN trainings.cover_url IS '封面图片URL';
COMMENT ON COLUMN trainings.status IS '状态：draft-草稿，open-报名中，ongoing-进行中，ended-已结束';
COMMENT ON COLUMN trainings.need_signup IS '是否需要报名：true-需要，false-不需要';
COMMENT ON COLUMN trainings.need_checkout IS '是否需要签退：true-需要，false-不需要';
COMMENT ON COLUMN trainings.gps_required IS '是否开启GPS限制：true-开启，false-不开启';
COMMENT ON COLUMN trainings.late_minutes IS '迟到阈值（分钟）';
COMMENT ON COLUMN trainings.early_leave_minutes IS '早退阈值（分钟）';
COMMENT ON COLUMN trainings.max_participants IS '最大报名人数，0表示不限制';
COMMENT ON COLUMN trainings.created_at IS '创建时间';
COMMENT ON COLUMN trainings.updated_at IS '更新时间';
COMMENT ON COLUMN trainings.deleted IS '删除标记：0-未删除，1-已删除';

-- 培训报名表
CREATE TABLE IF NOT EXISTS training_enrollments (
    id SERIAL PRIMARY KEY,
    training_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'enrolled',
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    CONSTRAINT fk_enroll_training FOREIGN KEY (training_id) REFERENCES trainings (id),
    CONSTRAINT fk_enroll_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 培训报名表字段注释
COMMENT ON TABLE training_enrollments IS '培训报名表';
COMMENT ON COLUMN training_enrollments.id IS '报名记录ID，主键';
COMMENT ON COLUMN training_enrollments.training_id IS '培训ID，外键关联trainings表';
COMMENT ON COLUMN training_enrollments.user_id IS '用户ID，外键关联users表';
COMMENT ON COLUMN training_enrollments.status IS '报名状态：enrolled-已报名，cancelled-已取消';
COMMENT ON COLUMN training_enrollments.enrolled_at IS '报名时间';
COMMENT ON COLUMN training_enrollments.cancelled_at IS '取消报名时间';
COMMENT ON COLUMN training_enrollments.created_at IS '创建时间';
COMMENT ON COLUMN training_enrollments.updated_at IS '更新时间';
COMMENT ON COLUMN training_enrollments.deleted IS '删除标记：0-未删除，1-已删除';

-- 培训签到表
CREATE TABLE IF NOT EXISTS training_checkins (
    id SERIAL PRIMARY KEY,
    training_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    checkin_time TIMESTAMP,
    checkout_time TIMESTAMP,
    is_late BOOLEAN DEFAULT FALSE,
    is_early_leave BOOLEAN DEFAULT FALSE,
    state VARCHAR(20) DEFAULT 'not_signed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    CONSTRAINT fk_checkin_training FOREIGN KEY (training_id) REFERENCES trainings (id),
    CONSTRAINT fk_checkin_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- 培训签到表字段注释
COMMENT ON TABLE training_checkins IS '培训签到表';
COMMENT ON COLUMN training_checkins.id IS '签到记录ID，主键';
COMMENT ON COLUMN training_checkins.training_id IS '培训ID，外键关联trainings表';
COMMENT ON COLUMN training_checkins.user_id IS '用户ID，外键关联users表';
COMMENT ON COLUMN training_checkins.checkin_time IS '签到时间';
COMMENT ON COLUMN training_checkins.checkout_time IS '签退时间';
COMMENT ON COLUMN training_checkins.is_late IS '是否迟到：true-迟到，false-未迟到';
COMMENT ON COLUMN training_checkins.is_early_leave IS '是否早退：true-早退，false-未早退';
COMMENT ON COLUMN training_checkins.state IS '签到状态：not_signed-未签到，signed-已签到，checked_out-已签退';
COMMENT ON COLUMN training_checkins.created_at IS '创建时间';
COMMENT ON COLUMN training_checkins.updated_at IS '更新时间';
COMMENT ON COLUMN training_checkins.deleted IS '删除标记：0-未删除，1-已删除';

-- 默认管理员：用户名 admin，密码 123456（MD5 简单示例：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO users (username, name, role, status, password, created_at, updated_at, deleted)
SELECT 'admin', '管理员', 'admin', 1, 'e10adc3949ba59abbe56e057f20f883e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

-- 默认普通用户：用户名 employee，密码 123456（MD5 简单示例：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO users (username, name, employee_no, dept, phone, role, status, password, created_at, updated_at, deleted)
SELECT 'employee', '张三', 'E001', '技术部', '13800138000', 'employee', 1, 'e10adc3949ba59abbe56e057f20f883e', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='employee');
