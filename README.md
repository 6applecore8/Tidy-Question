# Tidy-Question

本仓库包含：
- `backend`：Spring Boot 后端
- `infra`：本地依赖与编排（PostgreSQL / Redis）
- `fronted`：Vue3 + TypeScript 前端

## 快速启动

1. 准备后端环境变量（在 `infra` 目录下）：

```bash
cp .env.example .env
# 编辑 .env，填入 APP_MODEL_API_KEY
```

2. 启动依赖和后端：

```bash
cd infra
docker compose --env-file .env up --build
```

3. 启动前端：

```bash
cd fronted
pnpm install
pnpm dev
```

如果本机暂未安装 `pnpm`，可用：

```bash
npm install
npm run dev
```

4. 健康检查与访问：

- 后端健康检查：`http://localhost:8080/actuator/health`
- 前端开发地址：`http://localhost:5173`

## 默认账号

- username: `admin`
- password: `123456`

## 文档

- 接口契约：`openapi.yaml`
- 请求示例：`API_EXAMPLES.md`
- Agent 规则：`Agent.md`
- 前端规则：`fronted/FRONTEND_PROMPTS.md`

## docker pgvector启动
启动 pgvector 数据库
docker compose up -d postgres
查看状态
docker compose ps
停止
docker compose stop postgres
