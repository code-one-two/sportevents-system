# 体育馆器材管理系统

基于 Vue 3 与 Spring Boot 3 的体育馆器材、借用、维修和用户管理系统，包含 DeepSeek 文档助手。

## 技术栈

- 后端：Java 17、Spring Boot 3.3、MyBatis-Flex、Spring Security、JWT、Jakarta Validation
- 前端：Vue 3、Vite、Pinia、Vue Router、Axios、Element Plus、ESLint、Prettier
- 数据库：默认使用本地 H2 文件库，也支持 MySQL 8
- AI：DeepSeek Chat Completions API

## 环境要求

- JDK 17 或更高版本
- Node.js 18 或更高版本
- npm 9 或更高版本

先确认 Java 版本：

```bash
java -version
```

输出中应包含 `17` 或更高版本。本项目已经通过 Java 17.0.15 验证。

## DeepSeek 配置

不要把 API Key 写入前端代码。前端只访问本项目后端，由后端调用 DeepSeek。

推荐复制本地配置模板：

```bash
cp src/main/resources/application-local.example.yml \
  src/main/resources/application-local.yml
```

然后修改 `application-local.yml`：

```yaml
deepseek:
  api-key: your-deepseek-api-key
```

`application-local.yml` 已加入 `.gitignore`，不会被 Git 跟踪。也可以通过环境变量配置：

```bash
export DEEPSEEK_API_KEY=your-deepseek-api-key
export DEEPSEEK_MODEL=deepseek-v4-flash
```

相关配置位于 `src/main/resources/application.yml`：

```yaml
deepseek:
  base-url: ${DEEPSEEK_BASE_URL:https://api.deepseek.com}
  api-key: ${DEEPSEEK_API_KEY:}
  model: ${DEEPSEEK_MODEL:deepseek-v4-flash}
```

## 快速启动

### 1. 启动后端

默认使用 H2 文件数据库，首次启动时会自动建表并初始化演示数据，不需要预先安装或配置 MySQL。

```bash
./mvnw spring-boot:run
```

后端地址：<http://localhost:1194>

如果提示 `Permission denied`，执行一次：

```bash
chmod +x mvnw
```

### 2. 启动前端

打开另一个终端：

```bash
cd frontend
npm ci
npm run dev
```

前端地址：<http://localhost:5176>

### 3. 默认账户

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| `theRenter` | `123123` | 普通用户 |
| `admin` | `123123` | 管理员 |

管理员可管理用户、器材、借用和维修；普通用户可查看器材、申请借用及使用文档助手。

## 使用 MySQL

先创建数据库：

```bash
mysql -u root -p < sql/schema.sql
```

通过 `mysql` Profile 启动：

```bash
export DB_USERNAME=root
export DB_PASSWORD=your-mysql-password
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

可选环境变量：

- `DB_URL`：MySQL JDBC 地址
- `DB_USERNAME`：数据库用户名
- `DB_PASSWORD`：数据库密码
- `SERVER_PORT`：后端端口，默认 `1194`

## 验证命令

```bash
./mvnw test

cd frontend
npm run lint:check
npm run build
```

## 主要功能

- JWT 登录与基于权限的接口访问
- 器材和器材分类管理
- 借用申请、审批、归还和逾期检查
- 维修创建、开始、完成和归档
- 用户与角色管理
- 统计控制台
- DeepSeek 文档导入、总结、问答、润色和清单提取

## 项目结构

```text
sportevents-system/
├── sql/schema.sql
├── src/main/java/
├── src/main/resources/
│   ├── application.yml
│   ├── application-mysql.yml
│   ├── application-local.example.yml
│   ├── schema.sql
│   └── data.sql
└── frontend/src/
    ├── api/
    ├── components/
    ├── router/
    ├── store/
    ├── utils/
    └── views/
```
