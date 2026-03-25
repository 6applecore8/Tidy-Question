<template>
  <el-card class="upload-panel" shadow="never">
    <template #header>
      <div class="panel-header">
        <span>Upload Knowledge File</span>
        <span class="helper-text">支持 PDF / DOC / TXT 等常见格式</span>
      </div>
    </template>

    <div class="upload-content">
      <el-upload
        :auto-upload="false"
        :show-file-list="false"
        :before-upload="preventUpload"
        :on-change="handleFileChange"
      >
        <el-button>Select File</el-button>
      </el-upload>

      <span class="file-name">{{ selectedName || '未选择文件' }}</span>

      <el-button type="primary" :loading="uploading" :disabled="!selectedFile" @click="handleUpload">
        Upload
      </el-button>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { UploadFile, UploadRawFile } from 'element-plus';

defineProps<{
  uploading: boolean;
}>();

const emit = defineEmits<{
  upload: [file: File];
}>();

const selectedFile = ref<File | null>(null);
const selectedName = ref('');

function preventUpload(): false {
  return false;
}

function handleFileChange(file: UploadFile): void {
  const raw = file.raw as UploadRawFile | undefined;
  if (!raw) {
    return;
  }
  selectedFile.value = raw;
  selectedName.value = raw.name;
}

function handleUpload(): void {
  if (!selectedFile.value) {
    return;
  }
  emit('upload', selectedFile.value);
}
</script>

<style scoped>
.upload-panel {
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.upload-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.file-name {
  color: #475569;
  font-size: 13px;
}
</style>

