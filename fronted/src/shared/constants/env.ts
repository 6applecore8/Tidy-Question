const defaultBaseUrl = 'http://localhost:8080';

export const API_BASE_URL =
  typeof import.meta.env.VITE_API_BASE_URL === 'string' && import.meta.env.VITE_API_BASE_URL.trim().length > 0
    ? import.meta.env.VITE_API_BASE_URL.trim()
    : defaultBaseUrl;
