import axios, { AxiosError, type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios';

import { API_BASE_URL } from '@/shared/constants/env';
import { clearAuthSnapshot, readAuthSnapshot } from '@/shared/utils/auth-storage';
import { ApiBusinessError, type ApiResponse } from '@/shared/types/api';

function redirectToLogin(): void {
  if (window.location.pathname !== '/login') {
    window.location.replace('/login');
  }
}

const instance: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 45000,
});

instance.interceptors.request.use((config) => {
  const auth = readAuthSnapshot();
  if (auth?.token) {
    config.headers.Authorization = `Bearer ${auth.token}`;
  }
  return config;
});

instance.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiResponse<null>>) => {
    const status = error.response?.status;
    const payload = error.response?.data;

    if (status === 401 || payload?.code === 1002) {
      clearAuthSnapshot();
      redirectToLogin();
    }

    const message = payload?.message || error.message || '网络异常，请稍后重试';
    const code = payload?.code ?? 9000;
    const traceId = payload?.traceId;

    return Promise.reject(new ApiBusinessError(message, code, traceId, status));
  },
);

async function unwrap<T>(request: Promise<AxiosResponse<ApiResponse<T>>>): Promise<T> {
  const response = await request;
  const payload = response.data;

  if (typeof payload?.code !== 'number') {
    throw new ApiBusinessError('服务响应格式异常', 9000, undefined, response.status);
  }

  if (payload.code !== 0) {
    if (payload.code === 1002) {
      clearAuthSnapshot();
      redirectToLogin();
    }
    throw new ApiBusinessError(payload.message || '请求失败', payload.code, payload.traceId, response.status);
  }

  return payload.data;
}

const client = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return unwrap<T>(instance.get<ApiResponse<T>>(url, config));
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return unwrap<T>(instance.post<ApiResponse<T>>(url, data, config));
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return unwrap<T>(instance.put<ApiResponse<T>>(url, data, config));
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return unwrap<T>(instance.delete<ApiResponse<T>>(url, config));
  },
};

export default client;
