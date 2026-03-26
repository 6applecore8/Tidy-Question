<template>
  <section class="page-shell">
    <h1 class="section-title">Document Library</h1>
    <p class="helper-text">Upload files, build index, and preview parsed text content.</p>

    <document-upload-panel :uploading="uploading" @upload="handleUpload" />

    <document-list-panel
      :documents="documents"
      :loading="listLoading"
      :error="listError"
      :indexing-ids="indexingIds"
      :viewing-ids="viewingIds"
      @refresh="loadDocuments"
      @index="handleIndex"
      @view-content="handleViewContent"
    />

    <el-dialog
      v-model="previewDialogVisible"
      title="Document Content"
      width="760px"
      destroy-on-close
    >
      <div class="preview-meta">
        <span><strong>File:</strong> {{ previewFileName || '-' }}</span>
        <el-tag size="small">{{ previewStatus || '-' }}</el-tag>
      </div>

      <el-skeleton v-if="previewLoading" :rows="10" animated />

      <el-empty v-else-if="!previewContent" description="No parsable text content." />

      <pre v-else class="preview-content">{{ previewContent }}</pre>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';

import {
  getDocumentContent,
  indexDocument,
  listDocuments,
  type DocumentView,
  uploadDocument,
} from '@/features/docs/api';
import DocumentListPanel from '@/features/docs/components/DocumentListPanel.vue';
import DocumentUploadPanel from '@/features/docs/components/DocumentUploadPanel.vue';
import { ApiBusinessError } from '@/shared/types/api';

const documents = ref<DocumentView[]>([]);
const listLoading = ref(false);
const uploading = ref(false);
const listError = ref('');
const indexingIds = ref<Set<number>>(new Set());
const viewingIds = ref<Set<number>>(new Set());

const previewDialogVisible = ref(false);
const previewLoading = ref(false);
const previewFileName = ref('');
const previewStatus = ref('');
const previewContent = ref('');

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
    ElMessage.success(`Upload succeeded. Document ID: ${result.docId}`);
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
    ElMessage.success(`Index succeeded. Chunk count: ${result.chunkCount}`);
    await loadDocuments();
  } catch (error) {
    ElMessage.error(formatError(error));
  } finally {
    const next = new Set(indexingIds.value);
    next.delete(docId);
    indexingIds.value = next;
  }
}

async function handleViewContent(docId: number, fileName: string): Promise<void> {
  const current = new Set(viewingIds.value);
  current.add(docId);
  viewingIds.value = current;

  previewDialogVisible.value = true;
  previewLoading.value = true;
  previewFileName.value = fileName;
  previewStatus.value = '';
  previewContent.value = '';

  try {
    const result = await getDocumentContent(docId);
    previewFileName.value = result.fileName;
    previewStatus.value = result.status;
    previewContent.value = result.content;
  } catch (error) {
    ElMessage.error(formatError(error));
    previewDialogVisible.value = false;
  } finally {
    previewLoading.value = false;
    const next = new Set(viewingIds.value);
    next.delete(docId);
    viewingIds.value = next;
  }
}

function formatError(error: unknown): string {
  if (error instanceof ApiBusinessError) {
    return `${error.message}${error.traceId ? ` (traceId: ${error.traceId})` : ''}`;
  }
  return 'Request failed, please retry later.';
}
</script>

<style scoped>
.preview-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.preview-content {
  max-height: 58vh;
  overflow: auto;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
}
</style>
