# 培训签到系统

企业培训签到管理系统，支持培训发布、员工报名、签到签退、数据统计等功能。

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- PostgreSQL
- JWT认证
- EasyExcel

### 前端
- Vue 3.3.4
- Vite 5.0.5
- Element Plus 2.4.4
- Pinia 2.1.7
- Vue Router 4.2.5
- Axios 1.6.2
- ECharts 5.4.3

## 项目结构

```
pxqd/
├── backend/              # Spring Boot后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/     # Java源码
│   │       └── resources/ # 配置文件
│   └── pom.xml          # Maven配置
├── frontend/            # Vue3前端项目
│   ├── src/             # 前端源码
│   ├── package.json     # 依赖配置
│   └── vite.config.js   # Vite配置
├── database/            # 数据库脚本
│   └── init.sql        # 初始化SQL
└── README.md           # 项目说明
```

## 快速开始

### 1. 数据库准备

创建PostgreSQL数据库：
```sql
CREATE DATABASE training_db;
```

执行初始化脚本：
```bash
psql -U postgres -d training_db -f database/init.sql
```

### 2. 后端启动

进入后端目录：
```bash
cd backend
```

修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/training_db
    username: postgres
    password: postgres
```

运行项目：
```bash
mvn spring-boot:run
```

后端服务启动在：http://localhost:8080/api

### 3. 前端启动

进入前端目录：
```bash
cd frontend
```

安装依赖：
```bash
npm install
```

启动开发服务器：
```bash
npm run dev
```

前端服务启动在：http://localhost:3000

## 默认账号

- **管理员账号**：`admin`
- **密码**：`123456`

## 功能模块

### 1. 用户管理（管理员）
- ✅ 用户列表查询（分页、搜索）
- ✅ 创建/编辑/删除用户
- ✅ Excel批量导入
- ✅ Excel批量导出
- ✅ 用户状态管理（启用/禁用）

### 2. 培训管理
- ✅ 创建培训（草稿状态）
- ✅ 编辑培训信息
- ✅ 发布培训
- ✅ 培训列表查询（分页、搜索、筛选）
- ✅ 培训详情查看
- ✅ 删除培训（软删除）

### 3. 报名管理
- ✅ 员工报名培训
- ✅ 取消报名
- ✅ 报名人数限制校验
- ✅ 查看报名列表

### 4. 签到管理
- ✅ 签到功能（含迟到判断）
- ✅ 签退功能（含早退判断）
- ✅ GPS定位校验（可选）
- ✅ 签到记录查询
- ✅ 签到统计

### 5. 数据统计（管理员）
- ✅ 首页概览统计
- ✅ 按培训项目统计
- ✅ 按部门统计（含图表）
- ✅ 按员工统计

## API接口文档

### 认证接口
- `POST /api/auth/login` - 登录
- `GET /api/auth/me` - 获取当前用户信息

### 用户管理
- `GET /api/users` - 分页查询用户
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `POST /api/users/import` - Excel导入
- `GET /api/users/export` - Excel导出

### 培训管理
- `GET /api/trainings` - 分页查询培训
- `POST /api/trainings` - 创建培训
- `PUT /api/trainings/{id}` - 更新培训
- `GET /api/trainings/{id}` - 获取培训详情
- `POST /api/trainings/{id}/publish` - 发布培训
- `DELETE /api/trainings/{id}` - 删除培训

### 报名管理
- `POST /api/enrollments` - 报名培训
- `POST /api/enrollments/cancel` - 取消报名
- `GET /api/enrollments/user/{userId}` - 获取用户报名列表
- `GET /api/enrollments/training/{trainingId}` - 获取培训报名列表
- `GET /api/enrollments/check` - 检查是否已报名

### 签到管理
- `POST /api/checkins` - 签到
- `POST /api/checkins/checkout` - 签退
- `GET /api/checkins/user/{userId}` - 获取用户签到记录
- `GET /api/checkins/training/{trainingId}` - 获取培训签到记录
- `GET /api/checkins/stats/{trainingId}` - 获取签到统计

### 统计接口
- `GET /api/stats/overview` - 首页统计数据
- `GET /api/stats/by-training` - 按培训项目统计
- `GET /api/stats/by-department` - 按部门统计
- `GET /api/stats/by-employee` - 按员工统计

## 开发说明

### 后端开发
- 使用MyBatis Plus进行数据库操作
- JWT Token认证，Token存储在请求头 `Authorization: Bearer {token}`
- 统一响应格式：`{ code: 0, message: "success", data: {} }`
- 软删除机制（deleted字段）

### 前端开发
- 使用Pinia进行状态管理
- 路由守卫实现权限控制
- Axios拦截器自动处理Token和错误
- Element Plus组件库，已配置中文

## 部署说明

### 后端部署
1. 打包：`mvn clean package`
2. 运行：`java -jar target/training-signin-system-1.0.0.jar`
3. 配置生产环境数据库连接

### 前端部署
1. 构建：`npm run build`
2. 将 `dist` 目录部署到Nginx或静态服务器
3. 配置API代理或修改API地址

## 注意事项

1. 默认密码为MD5加密的"123456"，生产环境请修改
2. 文件上传路径配置在 `application.yml` 中
3. JWT密钥配置在 `application.yml` 中，生产环境请修改
4. 前端开发环境已配置代理，生产环境需配置CORS或Nginx反向代理

## 待实现功能

- [ ] 手机扫码签到页面（H5）
- [ ] RAG智能问答助手
- [ ] 培训通知推送
- [ ] 二维码生成功能
- [ ] 文件上传功能

## 许可证

MIT License


