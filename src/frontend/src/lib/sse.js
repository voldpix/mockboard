import { selectedWebhook, sseStatus, webhooks } from './stores.js';

let source = null;
let activeBoardId = null;
let activeToken = null;
let retryCount = 0;
let reconnectTimer = null;

export function connectSse(boardId, token, maxWebhooks = 100) {
  closeSse();
  activeBoardId = boardId;
  activeToken = token;

  if (!boardId || !token) {
    sseStatus.set({ state: 'offline', label: 'Missing token' });
    return;
  }

  openSource(maxWebhooks);
}

export function closeSse() {
  if (reconnectTimer) {
    window.clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }

  if (source) {
    source.close();
    source = null;
  }

  activeBoardId = null;
  activeToken = null;
  retryCount = 0;
  sseStatus.set({ state: 'idle', label: 'Not connected' });
}

function openSource(maxWebhooks) {
  const token = encodeURIComponent(activeToken);
  source = new EventSource(`/api/boards/${activeBoardId}/stream?token=${token}`);
  sseStatus.set({ state: 'connecting', label: 'Connecting' });

  source.addEventListener('ping', () => {
    retryCount = 0;
    sseStatus.set({ state: 'connected', label: 'Live' });
  });

  source.addEventListener('webhook-event', (event) => {
    retryCount = 0;
    sseStatus.set({ state: 'connected', label: 'Live' });
    upsertWebhook(JSON.parse(event.data), maxWebhooks);
  });

  source.addEventListener('server-shutdown', () => {
    sseStatus.set({ state: 'reconnecting', label: 'Server restarting' });
    scheduleReconnect(maxWebhooks);
  });

  source.onerror = () => {
    if (!activeBoardId || !activeToken) return;
    sseStatus.set({ state: 'reconnecting', label: 'Reconnecting' });
    scheduleReconnect(maxWebhooks);
  };
}

function scheduleReconnect(maxWebhooks) {
  if (source) {
    source.close();
    source = null;
  }

  if (reconnectTimer || !activeBoardId || !activeToken) return;

  const delay = Math.min(1000 * 2 ** retryCount, 15000);
  retryCount += 1;
  reconnectTimer = window.setTimeout(() => {
    reconnectTimer = null;
    openSource(maxWebhooks);
  }, delay);
}

function upsertWebhook(item, maxWebhooks) {
  webhooks.update((items) => {
    const next = [item, ...items.filter((existing) => existing.id !== item.id)];
    return next.slice(0, maxWebhooks);
  });
  selectedWebhook.update((current) => current || item);
}
