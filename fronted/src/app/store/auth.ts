import { defineStore } from 'pinia';

import type { AuthSnapshot } from '@/shared/types/api';
import { clearAuthSnapshot, readAuthSnapshot, writeAuthSnapshot } from '@/shared/utils/auth-storage';

interface AuthState {
  userId: number | null;
  username: string;
  token: string;
  expireHours: number;
  hydrated: boolean;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    userId: null,
    username: '',
    token: '',
    expireHours: 24,
    hydrated: false,
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token),
  },
  actions: {
    hydrate() {
      if (this.hydrated) {
        return;
      }
      const snapshot = readAuthSnapshot();
      if (snapshot) {
        this.userId = snapshot.userId;
        this.username = snapshot.username;
        this.token = snapshot.token;
        this.expireHours = snapshot.expireHours;
      }
      this.hydrated = true;
    },
    setSession(snapshot: AuthSnapshot) {
      this.userId = snapshot.userId;
      this.username = snapshot.username;
      this.token = snapshot.token;
      this.expireHours = snapshot.expireHours;
      this.hydrated = true;
      writeAuthSnapshot(snapshot);
    },
    logout() {
      this.userId = null;
      this.username = '';
      this.token = '';
      this.expireHours = 24;
      this.hydrated = true;
      clearAuthSnapshot();
    },
  },
});
