# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 行为准则

### 1. 先思考再写代码

**不要假设。不要隐藏困惑。主动说明权衡。**

实现之前：
- 明确说明你的假设。如有不确定，请提问。
- 如果存在多种理解，都列出来——不要默默选一个。
- 如果有更简单的方案，提出来。必要时提出异议。
- 如果有什么不清楚的，停下来，说明困惑点，再提问。

### 2. 简洁优先

**用最少的代码解决问题，不做任何投机性编码。**

- 不添加需求之外的功能。
- 不为单次使用的代码创建抽象。
- 不添加未请求的"灵活性"或"可配置性"。
- 不为不可能发生的场景写错误处理。
- 如果 200 行代码能精简到 50 行，重写它。

自问："资深工程师会说这太复杂吗？"如果是，简化它。

### 3. 精准修改

**只动必须动的地方。只清理自己引入的混乱。**

修改现有代码时：
- 不要"顺便改进"相邻代码、注释或格式。
- 不要重构没有问题的部分。
- 遵循现有风格，即使你不认同。
- 如果发现无关的死代码，提出来——不要擅自删除。

当你的修改产生孤儿代码时：
- 移除**你的修改**导致的未使用 import/变量/函数。
- 除非被要求，不要移除已存在的死代码。

检验标准：每一行改动都应直接追溯到用户的需求。

### 4. 目标驱动执行

**定义成功标准，直到验证为止。**

将任务转化为可验证的目标：
- "添加验证" → "写无效输入的测试，然后让它们通过"
- "修复 bug" → "写一个复现 bug 的测试，然后修复它"
- "重构 X" → "确保重构前后测试都通过"

多步骤任务时，声明简要计划：
```
1. [步骤] → 验证：[检查项]
2. [步骤] → 验证：[检查项]
3. [步骤] → 验证：[检查项]
```

## 项目概述

PDAD-system 是一个抑郁症评估与诊断辅助系统，支持多角色认证（医生、患者、管理员）、心理量表测试、AI 驱动的 fMRI 脑部图像分析、医患聊天和医学知识文章管理。

## 技术栈

| 用途 | 技术栈 | 版本 |
|------|--------|------|
| 编程语言 | Java | 22 |
| 后端框架 | Spring Boot | 3.3.4 |
| ORM 框架 | MyBatis | 3.0.3 |
| ORM 框架 | Spring Data JPA | — |
| 数据库 | MySQL | 8.0.29 |
| 安全认证 | Spring Security + BCrypt | — |
| Token 认证 | JWT (jjwt) | 0.9.1 |
| 前端框架 | Vue | 3.5.13 |
| 前端构建工具 | Vite | 6.0.1 |
| UI 组件库 | Element Plus | 2.9.0 |
| 状态管理 | Pinia | 2.3.0 |
| 前端路由 | Vue Router | 4.5.0 |
| HTTP 客户端 | Axios | 1.7.9 |
| fMRI 预测与模型训练 | Python ML 服务 (`http://127.0.0.1:18000`) | — |

## 项目结构

这是一个包含两个主要组件的单体仓库：

- **后端：** `src/main/java/com/alan/PDAD_system/`（Spring Boot 应用）
- **前端：** `Web-View/PDAD-system/`（Vue.js 应用）
- **遗留 JSP：** `web/`（旧的 JSP 文件）

### 后端包结构

- `controller/` — REST API 端点（用户、患者、医生、聊天、fMRI、文章）
- `service/` + `service/impl/` — 业务逻辑层
- `mapper/` — MyBatis Mapper 接口
- `entity/` — JPA/MyBatis 实体类
- `dto/` — 数据传输对象（31 个请求/响应类）
- `config/` — 安全与 Web 配置
- `interceptors/` — LoginInterceptor 用于 JWT 认证拦截
- `utils/` — JwtUtil, Md5Util, ThreadLocalUtil
- `exception/` — 全局异常处理器

## 常用命令

### 后端（Spring Boot）

```bash
# 构建
mvnw clean package

# 运行开发服务器（端口 8080）
mvnw spring-boot:run

# 运行测试
mvnw test
```

### 前端（Vue.js）

```bash
cd Web-View/PDAD-system

# 安装依赖
npm install

# 开发服务器
npm run dev

# 生产环境构建
npm run build

# 预览生产构建
npm run preview
```

## 架构

### 认证流程

基于 JWT 的认证，Token 有效期 30 分钟。`LoginInterceptor` 拦截受保护路由。密码使用 BCrypt 哈希加密。角色：医生、患者、管理员。

### 核心模块

1. **用户管理** — 通过 `UserController` 注册/登录
2. **患者操作** — 测试分数、诊断、病历、量表提交，通过 `PatientController`
3. **医生操作** — 患者管理、病历、测试结果、文章发布，通过 `DoctorController`
4. **聊天系统** — 实时消息与未读追踪，通过 `ChatController`
5. **fMRI 分析** — 与 Python ML 服务集成进行脑部图像预测，通过 `FmriController`
6. **文章系统** — 健康文章增删改查，通过 `ArticleController`

### 抑郁评估量表

三个量表（ID: 2、3、5），带评分算法。数据库触发器 `after_patient_answer_insert_final_score` 在患者完成量表所有题目后自动计算分数。

### 外部 ML 服务

后端通过 RestTemplate 与 Python ML 服务（`http://127.0.0.1:18000`）通信，用于 fMRI 预测和模型训练。配置项在 `application.properties` 中为 `ml.base-url`。

## 配置

**数据库：** MySQL，连接地址 `jdbc:mysql://127.0.0.1:3306/itcast`（见 `src/main/resources/application.properties`）
**后端端口：** 8080
**前端开发服务器：** 配置在 `Web-View/PDAD-system/vite.config.js`
