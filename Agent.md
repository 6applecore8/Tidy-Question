# Agent.md

## 1. Agent Role

- 名称：`TidyQuestionMasterAgent`
- 目标：帮助用户完成“文档整理 + 问答学习 + 八股训练 + 复盘”闭环。
- 模式：单主 Agent（检索、提问、评分、总结在同一条编排链路中完成）。

## 2. Input / Output Contract

### Input

```json
{
  "mode": "qa|quiz|review",
  "question": "string",
  "topic": "string",
  "difficulty": "easy|medium|hard",
  "contextChunks": ["string"],
  "userAnswer": "string"
}
```

### Output

```json
{
  "answer": "string",
  "citations": [{"chunkId": 1, "score": 0.12}],
  "score": {
    "llm": 80,
    "rule": 72,
    "final": 78
  },
  "improvements": "string",
  "nextQuestion": "string"
}
```

## 3. Orchestration Flow

1. `qa` 模式
- 将用户问题向量化。
- 在 pgvector 中做 TopK 召回。
- 用召回片段拼接上下文，调用聊天模型生成答案。
- 返回答案和引用片段。

2. `quiz` 模式
- 根据主题和难度生成题目。
- 接收用户作答后调用模型评分。
- 同时执行规则评分（关键点命中 + 回答长度）。
- 计算最终分并生成下一题。

3. `review` 模式
- 聚合历史答题记录。
- 输出薄弱点、建议复习方向。

## 4. Prompt Templates

### QA System Prompt

- 你是学习助手，只能基于提供资料回答。
- 若资料不足，明确说明“资料中未覆盖”。
- 回答要结构化，最后给复习建议。

### Quiz Evaluate System Prompt

- 你是严格面试官。
- 只输出 JSON。
- 字段必须包含 `llmScore/strengths/improvements/idealAnswer/keyPoints`。

## 5. Failure & Fallback Policy

- 检索无结果：返回“知识库未命中”，引导用户上传资料。
- 模型调用超时/失败：
  - 问答场景返回可读错误和 traceId。
  - 出题场景回退到模板题。
- JSON 解析失败：触发评分失败错误码 `4002`。

## 6. Safety Rules

- 不输出 API Key、JWT、数据库密码等敏感信息。
- 不根据用户输入执行危险系统命令。
- 仅在授权接口中读写训练和文档数据。
-隐私信息统一放在application-local文件中，而非放在application中，且在gitinore中隐藏local

## 7. Observability

- 必打日志字段：`traceId`、`userId`、`module`、`action`、`costMs`。
- 错误日志必须包含异常栈。
- 对外响应统一包含 `traceId`，便于前后端联调排障。
