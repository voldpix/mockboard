import { writable } from 'svelte/store';

export const config = writable(null);
export const appToken = writable(null);
export const boards = writable([]);
export const selectedBoard = writable(null);
export const mockRules = writable([]);
export const webhooks = writable([]);
export const selectedWebhook = writable(null);
export const sseStatus = writable({ state: 'idle', label: 'Not connected' });
export const toasts = writable([]);

let toastId = 0;

export function pushToast(type, message) {
  const id = ++toastId;
  toasts.update((items) => [...items, { id, type, message }]);
  window.setTimeout(() => dismissToast(id), 4500);
}

export function dismissToast(id) {
  toasts.update((items) => items.filter((toast) => toast.id !== id));
}

export function resetBoardState() {
  selectedBoard.set(null);
  mockRules.set([]);
  webhooks.set([]);
  selectedWebhook.set(null);
  sseStatus.set({ state: 'idle', label: 'Not connected' });
}
