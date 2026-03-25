import client from '@/shared/http/client';

export interface AskRequest {
  question: string;
  topK?: number;
}

export interface Citation {
  chunkId: number;
  documentId: number;
  chunkIndex: number;
  content: string;
  score: number;
}

export interface AskResponse {
  answer: string;
  citations: Citation[];
  costMs: number;
  retrievalMode: string;
}

export function askQuestion(payload: AskRequest): Promise<AskResponse> {
  return client.post('/api/chat/ask', payload);
}
