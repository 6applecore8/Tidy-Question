<template>
  <section class="page-shell">
    <h1 class="section-title">文档库</h1>
    <p class="helper-text">上传资料后可建立索引，供问答与训练引用。</p>

    <document-upload-panel :uploading="uploading" @upload="handleUpload" />

    <document-list-panel
      :documents="documents"
      :loading="listLoading"
      :error="listError"
      :indexing-ids="indexingIds"
      @refresh="loadDocuments"
      @index="handleIndex"
    />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { indexDocument, listDocuments, type DocumentView, uploadDocument } from '@/features/docs/api';
import DocumentListPanel from '@/features/docs/components/DocumentListPanel.vue';
import DocumentUploadPanel from '@/features/docs/components/DocumentUploadPanel.vue';
import { ApiBusinessError } from '@/shared/types/api';

const documents = ref<DocumentView[]>([]);
const listLoading = ref(false);
const uploading = ref(false);
const listError = ref('');
const indexingIds = ref<Set<number>>(new Set());

onMounted(() => {
  void loadDocuments();
});

async function loadDocuments(): Promise<void> {
  listLoading.value = true;
  listError.value = '';
  try {
    documents.value = await listDocuments();
  } catch (error) {
    listError.value = formatError(error);
  } finally {
    listLoading.value = false;
  }
}

async function handleUpload(file: File): Promise<void> {
  uploading.value = true;
  try {
    const result = await uploadDocument(file);
    ElMessage.success(`上传成功，文档ID: ${result.docId}`);
    await loadDocuments();
  } catch (error) {
    ElMessage.error(formatError(error));
  } finally {
    uploading.value = false;
  }
}

async function handleIndex(docId: number): Promise<void> {
  const current = new Set(indexingIds.value);
  current.add(docId);
  indexingIds.value = current;

  try {
    const result = await indexDocument(docId);
    ElMessage.success(`索引完成，chunk 数: ${result.chunkCount}`);
    await loadDocuments();
  } catch (error) {
    ElMessage.error(formatError(error));
  } finally {
    const next = new Set(indexingIds.value);
    next.delete(docId);
    indexingIds.value = next;
  }
}

function formatError(error: unknown): string {
  if (error instanceof ApiBusinessError) {
    return `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}`;
  }
  return '请求失败，请稍后重试';
}
</script>
