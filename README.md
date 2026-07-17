# PDAD-system

抑郁症评估与诊断辅助系统（Postpartum Depression Assessment & Diagnosis System）。

支持多角色认证（医生、患者、管理员）、心理量表测试、AI 驱动的 fMRI 脑部图像分析、医患在线聊天和医学知识文章管理。

## 功能特性

- **用户管理** — 注册 / 登录，JWT 认证（Token 有效期 30 分钟），BCrypt 密码加密
- **心理量表测试** — 三套抑郁评估量表（ID: 2、3、5），数据库触发器自动计算总分
- **fMRI 脑部图像分析** — 对接 Python ML 服务进行预测与模型训练
- **医患聊天** — 实时消息与未读消息追踪
- **文章系统** — 医学知识文章的发布与管理
- **多角色权限** — 医生 / 患者 / 管理员三种角色

## 技术栈

| 用途 | 技术 | 版本 |
|------|------|------|
| 编程语言 | Java | 22 |
| 后端框架 | Spring Boot | 3.3.4 |
| ORM | MyBatis + Spring Data JPA | 3.0.3 |
| 数据库 | MySQL | 8.0.29 |
| 安全认证 | Spring Security + JWT (jjwt) | 0.9.1 |
| 前端框架 | Vue | 3.5.13 |
| 构建工具 | Vite | 6.0.1 |
| UI 组件库 | Element Plus | 2.9.0 |
| 状态管理 | Pinia | 2.3.0 |
| HTTP 客户端 | Axios | 1.7.9 |
| ML 服务 | Python（fMRI 预测与训练） | — |

## 项目结构

```
PDAD-system/
├── src/main/java/com/alan/PDAD_system/   # Spring Boot 后端
│   ├── controller/                       # REST API（用户/患者/医生/聊天/fMRI/文章）
│   ├── service/ + service/impl/          # 业务逻辑层
│   ├── mapper/                           # MyBatis Mapper 接口
│   ├── entity/                           # 实体类
│   ├── dto/                              # 数据传输对象
│   ├── config/                           # 安全与 Web 配置
│   ├── interceptors/                     # JWT 登录拦截器
│   ├── utils/                            # JwtUtil / Md5Util / ThreadLocalUtil
│   └── exception/                        # 全局异常处理
├── src/main/resources/
│   ├── application.properties            # 数据库与 ML 服务配置
│   └── mapper/                           # MyBatis XML 映射
├── Web-View/PDAD-system/                 # Vue 3 前端
├── web/                                  # 遗留 JSP 页面
└── pom.xml
```

## 快速开始

### 前置要求

- JDK 22+
- Node.js 18+
- MySQL 8.0+
- （可选）Python ML 服务，运行于 `http://127.0.0.1:18000`

### 1. 数据库

创建 MySQL 数据库 `itcast`，并导入业务表结构与三套量表数据。

### 2. 配置

编辑 `src/main/resources/application.properties`，填入你的数据库账号密码：

```properties
spring.datasource.username=your-username
spring.datasource.password=your-password
```

ML 服务地址通过 `ml.base-url` 配置（默认 `http://127.0.0.1:18000`）。

### 3. 启动后端（端口 8080）

```bash
./mvnw spring-boot:run
```

### 4. 启动前端

```bash
cd Web-View/PDAD-system
npm install
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`。

## 常用命令

| 操作 | 命令 |
|------|------|
| 后端打包 | `./mvnw clean package` |
| 后端测试 | `./mvnw test` |
| 前端构建 | `npm run build` |
| 前端预览 | `npm run preview` |

## 说明

- 本项目为课程综合设计作品，仅供学习参考。
- `application.properties` 中的数据库密码为占位符，请勿提交真实密码。
