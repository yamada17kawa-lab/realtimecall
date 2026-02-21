# RealtimeCall 微服务项目

## 项目概述

RealtimeCall 是一个基于 Spring Boot 3.0 + Spring Cloud Alibaba 的实时视频/语音通话微服务系统。系统采用微服务架构设计，包含用户管理、认证授权、文件存储和信令服务等核心功能，支持 WebSocket 信令交互和 WebRTC 实时通信。

## 技术栈

- **后端框架**: Spring Boot 3.0.12
- **微服务架构**: Spring Cloud 2022.0.4
- **服务注册与发现**: Alibaba Nacos
- **API 网关**: Spring Cloud Gateway
- **服务间通信**: OpenFeign + LoadBalancer
- **数据库**: MySQL 8.0.33
- **ORM 框架**: MyBatis-Plus
- **缓存**: Redis
- **安全认证**: JWT
- **实时通信**: WebSocket + WebRTC
- **构建工具**: Maven
- **JDK 版本**: JDK 17

## 项目结构

```
realtimecall/
├── common/                    # 公共模块
│   ├── config/               # 配置模块
│   ├── exception/            # 异常处理
│   ├── feign/                # Feign 客户端
│   ├── model/                # 实体类、DTO、VO
│   └── utils/                # 工具类
├── gateway/                  # 网关模块
│   └── gw/                   # 网关服务
├── services/                 # 业务服务模块
│   ├── auth-service/         # 认证服务
│   ├── files-service/        # 文件服务
│   └── user-service/         # 用户服务
├── signal/                   # 信令服务模块
│   └── signal-service/       # WebSocket 信令服务
└── pom.xml                   # 父 POM 文件
```

## 模块说明

### 1. 公共模块 (common)

#### config
- 提供 Jackson 自动配置
- Web 配置类
- 跨域处理等通用配置

#### exception
- `BizException`: 业务异常类
- `ErrorCode`: 错误码枚举
- `GlobalExceptionHandler`: 全局异常处理器

#### feign
- `UserFeign`: 用户服务 Feign 客户端

#### model
- **实体类**:
  - `UserEntity`: 用户实体
  - `FriendEntity`: 好友关系实体
  - `FilesEntity`: 文件实体
- **DTO**:
  - `LoginDto`: 登录数据传输对象
  - `RegisterDto`: 注册数据传输对象
  - `UserUpdateDto`: 用户更新数据传输对象
- **VO**:
  - `UserVo`: 用户视图对象
  - `FilesVo`: 文件视图对象
- **结果集**:
  - `Result`: 统一响应结果
  - `PageResult`: 分页结果

#### utils
- `JwtUtil`: JWT 工具类，用于 Token 生成和验证
- `RedisUtil`: Redis 工具类

### 2. 网关服务 (gateway)

- **端口**: 80
- **功能**:
  - 请求路由
  - 负载均衡
  - 全局过滤器
- **路由配置**:
  - `/user/**` → user-service
  - `/auth/**` → auth-service
  - `/files/**` → files-service
  - `/wsnuliyang/**` → signal-service (WebSocket)

### 3. 认证服务 (auth-service)

- **端口**: 8000
- **功能**:
  - 用户注册
  - 用户登录
  - JWT Token 颁发和验证

### 4. 用户服务 (user-service)

- **端口**: 9000
- **功能**:
  - 用户信息管理
  - 好友关系管理
  - 用户查询与更新

### 5. 文件服务 (files-service)

- **端口**: 7896
- **功能**:
  - 文件上传
  - 文件下载
  - 文件存储与访问

### 6. 信令服务 (signal-service)

- **端口**: 6666
- **功能**:
  - WebSocket 信令服务器
  - SDP 交换
  - ICE 候选交换
  - 通话控制
  - Redis 会话存储

## 数据库设计

### 4.1 用户表 (users)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 用户ID，自增主键 | PRIMARY KEY, AUTO_INCREMENT |
| user_name | VARCHAR(50) | 用户名（唯一） | NOT NULL, UNIQUE |
| password | VARCHAR(255) | 密码（哈希存储，如 bcrypt） | NOT NULL |
| nick_name | VARCHAR(100) | 昵称 | DEFAULT NULL |
| phone | VARCHAR(20) | 手机号 | DEFAULT NULL |
| email | VARCHAR(100) | 邮箱 | DEFAULT NULL |
| avatar | VARCHAR(255) | 头像URL或路径 | DEFAULT NULL |
| status | TINYINT | 状态：0=正常，1=禁用等 | DEFAULT 0 |
| is_deleted | TINYINT | 逻辑删除：0=未删除，1=已删除 | DEFAULT 0 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

### 4.2 用户好友关系表 (user_friends)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 关系自增ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL, FOREIGN KEY |
| friend_id | BIGINT | 好友ID | NOT NULL, FOREIGN KEY |

**索引**:
- `uk_user_friend`: (user_id, friend_id) 唯一索引，防止重复添加
- `idx_user_id`: user_id 索引，优化查询
- `idx_friend_id`: friend_id 索引

### 4.3 文件存储表 (file_storage)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 文件ID | PRIMARY KEY, AUTO_INCREMENT |
| storage_path | VARCHAR(512) | 本地存储路径 | NOT NULL |
| access_url | VARCHAR(512) | 访问URL | NOT NULL |
| create_time | DATETIME | 上传时间 | DEFAULT CURRENT_TIMESTAMP |

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+

### 数据库初始化

执行以下 SQL 脚本创建数据库和表：

```sql
CREATE DATABASE IF NOT EXISTS realtimecall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE realtimecall;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID，自增主键',
    user_name VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（唯一）',
    password VARCHAR(255) NOT NULL COMMENT '密码（哈希存储，如 bcrypt）',
    nick_name VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL或路径',
    status TINYINT DEFAULT 0 COMMENT '状态：0=正常，1=禁用等',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户好友关系表
CREATE TABLE user_friends (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关系自增ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    friend_id BIGINT NOT NULL COMMENT '好友ID',
    UNIQUE KEY uk_user_friend (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户好友关系表';

-- 文件存储表
CREATE TABLE file_storage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    storage_path VARCHAR(512) NOT NULL COMMENT '本地存储路径',
    access_url VARCHAR(512) NOT NULL COMMENT '访问URL',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件存储表';
```

### 配置说明

1. **Nacos 配置**
   - 确保 Nacos 服务已启动
   - 默认地址: `127.0.0.1:8848`
   - 各服务配置中心地址可在 `application.yaml` 中修改

2. **MySQL 配置**
   - 修改各服务中的数据库连接信息
   - 主要配置项:
     - `spring.datasource.url`
     - `spring.datasource.username`
     - `spring.datasource.password`

3. **Redis 配置**
   - 信令服务需要 Redis 支持
   - 配置项在 `signal-service` 的 `application.yaml` 中

### 启动顺序

1. 启动 Nacos
2. 启动 Redis
3. 启动 MySQL
4. 按以下顺序启动服务:
   - 网关服务 (gateway)
   - 认证服务 (auth-service)
   - 用户服务 (user-service)
   - 文件服务 (files-service)
   - 信令服务 (signal-service)

### 构建项目

```bash
# 在项目根目录执行
mvn clean install -DskipTests
```

## API 接口说明

### 认证服务

- `POST /auth/register` - 用户注册
- `POST /auth/login` - 用户登录

### 用户服务

- `GET /user/{id}` - 获取用户信息
- `PUT /user/update` - 更新用户信息
- `POST /user/friend/add` - 添加好友
- `GET /user/friend/list` - 获取好友列表

### 文件服务

- `POST /files/upload` - 文件上传
- `GET /files/download/{id}` - 文件下载
- `GET /files/{id}` - 获取文件信息

### WebSocket 信令

- `ws://localhost:80/wsnuliyang` - WebSocket 连接地址
- 支持的信令类型: SDP offer/answer, ICE candidates, 通话控制等

## 开发规范

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理
- 统一响应格式

### Git 提交规范

```
<type>(<scope>): <subject>

类型说明:
- feat: 新功能
- fix: 修复 bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关
```

## 常见问题

### 1. 服务注册失败

检查 Nacos 是否正常启动，配置地址是否正确。

### 2. 数据库连接失败

确认 MySQL 服务状态，检查连接参数和权限配置。

### 3. WebSocket 连接失败

检查信令服务是否正常启动，网关路由配置是否正确。

## 许可证

本项目仅供学习交流使用。

## 联系方式

如有问题，请提交 Issue 或联系项目维护者。
