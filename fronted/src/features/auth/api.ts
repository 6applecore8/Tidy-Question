import client from '@/shared/http/client';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  username: string;
  token: string;
  expireHours: number;
}

export function login(payload: LoginRequest): Promise<LoginResponse> {
  return client.post('/api/auth/login', payload);
}
