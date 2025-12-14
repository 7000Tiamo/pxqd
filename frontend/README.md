# 培训签到系统 - 前端

## 技术栈
- Vue 3.3.4
- Vite 5.0.5
- Vue Router 4.2.5
- Pinia 2.1.7
- Element Plus 2.4.4
- Axios 1.6.2
- ECharts 5.4.3
- Day.js 1.11.10

## 项目结构
```
frontend/
├── src/
│   ├── api/                    # API接口
│   │   ├── request.js         # Axios封装
│   │   ├── auth.js            # 认证接口
│   │   ├── user.js            # 用户接口
│   │   ├── training.js         # 培训接口
│   │   ├── enrollment.js       # 报名接口
│   │   ├── checkin.js         # 签到接口
│   │   └── stats.js           # 统计接口
│   ├── assets/                # 静态资源
│   ├── components/            # 公共组件
│   ├── layouts/               # 布局组件
│   │   └── MainLayout.vue    # 主布局
│   ├── router/                # 路由配置
│   │   └── index.js
│   ├── stores/                # Pinia状态管理
│   │   └── auth.js           # 认证状态
│   ├── views/                 # 页面组件
│   │   ├── Login.vue         # 登录页
│   │   ├── Home.vue          # 首页
│   │   ├── Users.vue         # 用户管理
│   │   ├── Trainings.vue     # 培训管理
│   │   ├── TrainingForm.vue  # 培训表单
│   │   ├── TrainingDetail.vue # 培训详情
│   │   └── Stats.vue        # 数据统计
│   ├── App.vue               # 根组件
│   ├── main.js               # 入口文件
│   └── style.css             # 全局样式
├── index.html                # HTML模板
├── package.json              # 依赖配置
├── vite.config.js            # Vite配置
└── README.md                 # 项目说明
```

## 安装依赖
```bash
npm install
```

## 开发运行
```bash
npm run dev
```
访问：http://localhost:3000

## 构建生产版本
```bash
npm run build
```

## 预览生产版本
```bash
npm run preview
```

## 功能特性

### 1. 认证授权
- JWT Token认证
- 路由守卫
- 角色权限控制（管理员/员工）

### 2. 用户管理（管理员）
- 用户列表查询（分页、搜索）
- 创建/编辑/删除用户
- Excel批量导入
- Excel批量导出

### 3. 培训管理
- 培训列表查询（分页、搜索、筛选）
- 创建培训（草稿状态）
- 编辑培训
- 发布培训
- 培训详情查看
- 删除培训

### 4. 报名管理
- 员工报名培训
- 取消报名
- 查看报名列表

### 5. 数据统计（管理员）
- 首页概览统计
- 按培训项目统计
- 按部门统计（含图表）
- 按员工统计

## 页面说明

### 登录页 (`/login`)
- 账号密码登录
- 默认账号提示

### 首页 (`/home`)
- **管理员**：数据卡片、最近培训、快捷操作
- **员工**：待参加培训、可报名培训

### 用户管理 (`/users`) - 仅管理员
- 用户列表
- 搜索、分页
- 创建/编辑/删除
- 导入/导出

### 培训管理 (`/trainings`)
- 培训列表
- 搜索、筛选
- 创建/编辑/发布/删除

### 培训详情 (`/trainings/:id`)
- 培训基本信息
- 统计卡片
- 学员名单
- 签到记录

### 数据统计 (`/stats`) - 仅管理员
- 概览统计
- 多维度统计
- 图表展示

## 环境配置

### 开发环境
- 前端端口：3000
- 后端代理：http://localhost:8080

### 生产环境
修改 `vite.config.js` 中的 `baseURL` 配置后端API地址

## 注意事项

1. 所有API请求会自动携带JWT Token
2. 401错误会自动跳转到登录页
3. 路由守卫会检查角色权限
4. 使用Element Plus组件库，已配置中文语言包


