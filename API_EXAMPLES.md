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
  -d '{"question":"JVM閸愬懎鐡ㄧ紒鎾寸€弰顖欑矆娑斿牞绱?,"topK":4}'
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
  -d '{"answer":"Spring IOC 閺勵垱甯堕崚璺哄冀鏉?.."}'
```

## 8) Weak point summary

```bash
curl -X GET "http://localhost:8080/api/review/weak-points" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Error Codes

- `1001` 鐠囬攱鐪伴崣鍌涙殶闁挎瑨顕?- `1002` 閺堫亝宸块弶鍐問闂?- `1003` 缁備焦顒涚拋鍧楁６
- `2001` 閺傚洦銆傛稉濠佺炊婢惰精瑙?- `2002` 閺傚洦銆傜槐銏犵穿婢惰精瑙?- `2003` 閺傚洦銆傛稉宥呯摠閸?- `3001` 濡€崇€风拫鍐暏婢惰精瑙?- `3002` 閸氭垿鍣洪悽鐔稿灇婢惰精瑙?- `4001` 鐠侇厾绮屾导姘崇樈娑撳秴鐡ㄩ崷?- `4002` 鐠侇厾绮岀拠鍕瀻婢惰精瑙?- `9000` 缁崵绮洪崘鍛村劥闁挎瑨顕?


## 9) View document content

```bash
curl -X GET "http://localhost:8080/api/docs/1/content?maxChars=8000" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

Success response example:

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "docId": 1,
    "fileName": "java-notes.pdf",
    "status": "INDEXED",
    "content": "...preview text...",
    "totalChars": 24567,
    "returnedChars": 8000,
    "truncated": true
  },
  "traceId": "..."
}
```
