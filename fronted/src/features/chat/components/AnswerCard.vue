<template>
  <el-card shadow="never">
    <template #header>
      <span>Answer</span>
    </template>

    <el-alert v-if="error" type="error" :title="error" :closable="false" show-icon class="state-block" />

    <el-empty v-else-if="!response" description="提交问题后，这里会显示答案" />

    <template v-else>
      <p class="answer-text">{{ response.answer }}</p>
      <div class="meta-row">
        <el-tag type="success">cost {{ response.costMs }}ms</el-tag>
        <el-tag>{{ response.retrievalMode || 'default' }}</el-tag>
      </div>

      <h3>Citations</h3>
      <el-empty v-if="response.citations.length === 0" description="无引用片段" />
      <el-table v-else :data="response.citations" size="small">
        <el-table-column prop="documentId" label="Doc" width="80" />
        <el-table-column prop="chunkIndex" label="Chunk" width="90" />
        <el-table-column prop="score" label="Score" width="100">
          <template #default="{ row }">
            {{ Number(row.score).toFixed(4) }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="Content" min-width="260" show-overflow-tooltip />
      </el-table>
    </template>
  </el-card>
</template>

<script setup lang="ts">
import type { AskResponse } from '@/features/chat/api';

defineProps<{
  response: AskResponse | null;
  error: string;
}>();
</script>

<style scoped>
.answer-text {
  line-height: 1.7;
  margin: 0 0 12px;
  white-space: pre-wrap;
}

.meta-row {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
}

.state-block {
  margin-bottom: 10px;
}
</style>
