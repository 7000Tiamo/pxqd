# 培训签到系统 - 后端

## 技术栈
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- PostgreSQL
- JWT认证
- EasyExcel (Excel导入导出)
- Hutool工具类

## 项目结构
```
backend/
├── src/main/java/com/training/
│   ├── TrainingSigninApplication.java    # 启动类
│   ├── common/                           # 通用类
│   │   └── api/
│   │       └── ApiResponse.java         # 统一响应格式
│   ├── config/                          # 配置类
│   │   ├── JwtProperties.java          # JWT配置
│   │   ├── JwtAuthInterceptor.java     # JWT拦截器
│   │   ├── WebConfig.java              # Web配置
│   │   ├── MybatisPlusConfig.java      # MyBatis Plus配置
│   │   └── CorsConfig.java             # 跨域配置
│   ├── entity/                          # 实体类
│   │   ├── User.java
│   │   ├── Training.java
│   │   ├── Enrollment.java
│   │   ├── Checkin.java
│   │   └── UserExcel.java              # Excel导入导出实体
│   ├── mapper/                          # Mapper接口
│   │   ├── UserMapper.java
│   │   ├── TrainingMapper.java
│   │   ├── EnrollmentMapper.java
│   │   └── CheckinMapper.java
│   ├── service/                         # 服务层
│   │   ├── AuthService.java            # 认证服务
│   │   ├── UserService.java            # 用户服务
│   │   ├── TrainingService.java        # 培训服务
│   │   ├── EnrollmentService.java      # 报名服务
│   │   ├── CheckinService.java         # 签到服务
│   │   └── StatsService.java           # 统计服务
│   ├── controller/                      # 控制器
│   │   ├── AuthController.java        # 认证接口
│   │   ├── UserController.java        # 用户管理接口
│   │   ├── TrainingController.java     # 培训管理接口
│   │   ├── EnrollmentController.java  # 报名接口
│   │   ├── CheckinController.java     # 签到接口
│   │   └── StatsController.java       # 统计接口
│   ├── dto/                             # 数据传输对象
│   │   ├── LoginRequest.java
│   │   ├── TokenResponse.java
│   │   ├── UserCreateDTO.java
│   │   ├── UserUpdateDTO.java
│   │   ├── TrainingCreateDTO.java
│   │   ├── TrainingUpdateDTO.java
│   │   └── TrainingDetailVO.java
│   └── util/                            # 工具类
│       └── JwtUtil.java                # JWT工具
└── src/main/resources/
    ├── application.yml                  # 配置文件
    └── application-dev.yml            # 开发环境配置
```

## 数据库配置

1. 创建PostgreSQL数据库：
```sql
CREATE DATABASE training_db;
```

2. 执行初始化脚本：
```bash
psql -U postgres -d training_db -f ../database/init.sql
```

## 默认账号

- 管理员账号：`admin`
- 密码：`123456` (MD5: e10adc3949ba59abbe56e057f20f883e)

## 运行项目

1. 修改 `application.yml` 中的数据库连接信息
2. 运行主类 `TrainingSigninApplication`
3. 访问：http://localhost:8080/api

## API接口

### 认证接口
- `POST /api/auth/login` - 登录
- `GET /api/auth/me` - 获取当前用户信息

### 用户管理
- `GET /api/users` - 分页查询用户
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `POST /api/users/import` - Excel导入用户
- `GET /api/users/export` - Excel导出用户

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

## 注意事项

1. 所有需要认证的接口需要在请求头中携带JWT Token：
   ```
   Authorization: Bearer {token}
   ```

2. 不需要认证的接口：
   - `/api/auth/login`
   - `/api/auth/ping`

3. 文件上传路径配置在 `application.yml` 中的 `file.upload-path`


