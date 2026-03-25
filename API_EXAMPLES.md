# API Examples

## Base URL

```bash
http://localhost:8080
```

## 1) Login

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

Success response:

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "userId": 1,
    "username": "admin",
    "token": "<JWT_TOKEN>",
    "expireHours": 24
  },
  "traceId": "..."
}
```

## 2) Upload document

```bash
curl -X POST "http://localhost:8080/api/docs/upload" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F "file=@./sample.pdf"
```

## 3) Index document

```bash
curl -X POST "http://localhost:8080/api/docs/1/index" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 4) List documents

```bash
curl -X GET "http://localhost:8080/api/docs" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 5) Ask question

```bash
curl -X POST "http://localhost:8080/api/chat/ask" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"question":"JVM内存结构是什么？","topK":4}'
```

## 6) Start quiz session

```bash
curl -X POST "http://localhost:8080/api/quiz/session/start" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"topic":"Spring","difficulty":"medium"}'
```

## 7) Submit quiz answer

```bash
curl -X POST "http://localhost:8080/api/quiz/session/1/answer" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"answer":"Spring IOC 是控制反转..."}'
```

## 8) Weak point summary

```bash
curl -X GET "http://localhost:8080/api/review/weak-points" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Error Codes

- `1001` 请求参数错误
- `1002` 未授权访问
- `1003` 禁止访问
- `2001` 文档上传失败
- `2002` 文档索引失败
- `2003` 文档不存在
- `3001` 模型调用失败
- `3002` 向量生成失败
- `4001` 训练会话不存在
- `4002` 训练评分失败
- `9000` 系统内部错误
