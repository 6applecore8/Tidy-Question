export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
  traceId: string;
}

export interface AuthSnapshot {
  userId: number;
  username: string;
  token: string;
  expireHours: number;
}

export class ApiBusinessError extends Error {
  code: number;
  traceId?: string;
  status?: number;

  constructor(message: string, code: number, traceId?: string, status?: number) {
    super(message);
    this.name = 'ApiBusinessError';
    this.code = code;
    this.traceId = traceId;
    this.status = status;
  }
}
