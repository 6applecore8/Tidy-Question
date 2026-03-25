import client from '@/shared/http/client';

export interface DocumentUploadResponse {
  docId: number;
  status: string;
}

export interface DocumentIndexResponse {
  docId: number;
  status: string;
  chunkCount: number;
}

export interface DocumentView {
  id: number;
  fileName: string;
  contentType: string;
  status: string;
  chunkCount: number;
  createdAt: string;
  updatedAt: string;
}

export async function uploadDocument(file: File): Promise<DocumentUploadResponse> {
  const formData = new FormData();
  formData.append('file', file);
  return client.post('/api/docs/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

export function indexDocument(docId: number): Promise<DocumentIndexResponse> {
  return client.post(`/api/docs/${docId}/index`);
}

export function listDocuments(): Promise<DocumentView[]> {
  return client.get('/api/docs');
}
