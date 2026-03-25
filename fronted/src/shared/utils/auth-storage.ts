import { AUTH_STORAGE_KEY } from '@/shared/constants/auth';
import type { AuthSnapshot } from '@/shared/types/api';

export function readAuthSnapshot(): AuthSnapshot | null {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as Partial<AuthSnapshot>;
    if (!parsed.token || !parsed.username || typeof parsed.userId !== 'number') {
      return null;
    }

    return {
      userId: parsed.userId,
      username: parsed.username,
      token: parsed.token,
      expireHours: parsed.expireHours ?? 24,
    };
  } catch {
    return null;
  }
}

export function writeAuthSnapshot(snapshot: AuthSnapshot): void {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(snapshot));
}

export function clearAuthSnapshot(): void {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}
