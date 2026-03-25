import client from '@/shared/http/client';

export interface WeakPointItem {
  topic: string;
  avgScore: number;
  attemptCount: number;
  suggestion: string;
}

export function fetchWeakPoints(): Promise<WeakPointItem[]> {
  return client.get('/api/review/weak-points');
}
