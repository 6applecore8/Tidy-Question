import client from '@/shared/http/client';

export type QuizDifficulty = 'easy' | 'medium' | 'hard';

export interface StartSessionRequest {
  topic: string;
  difficulty: QuizDifficulty;
}

export interface StartSessionResponse {
  sessionId: number;
  topic: string;
  difficulty: QuizDifficulty;
  question: string;
  status: string;
}

export interface AnswerResponse {
  sessionId: number;
  question: string;
  userAnswer: string;
  llmScore: number;
  ruleScore: number;
  finalScore: number;
  keyPoints: string[];
  strengths: string;
  improvements: string;
  idealAnswer: string;
  nextQuestion: string;
}

export function startSession(payload: StartSessionRequest): Promise<StartSessionResponse> {
  return client.post('/api/quiz/session/start', payload);
}

export function submitAnswer(sessionId: number, answer: string): Promise<AnswerResponse> {
  return client.post(`/api/quiz/session/${sessionId}/answer`, { answer });
}
