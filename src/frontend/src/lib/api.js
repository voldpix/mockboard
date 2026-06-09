let appToken = null;
let tokenHeader = 'X-App-Token';

export function setAppConfig(preConfig) {
  appToken = preConfig?.app?.token ?? null;
  tokenHeader = preConfig?.app?.tokenHeader ?? 'X-App-Token';
}

export function getAppToken() {
  return appToken;
}

export async function loadPreConfig() {
  const data = await request('/api/pre', { auth: false });
  setAppConfig(data);
  return data;
}

export const api = {
  listBoards: () => request('/api/boards'),
  createBoard: () => request('/api/boards', { method: 'POST', body: {} }),
  getBoard: (boardId) => request(`/api/boards/${boardId}`),
  updateBoard: (boardId, payload) => request(`/api/boards/${boardId}`, { method: 'PUT', body: payload }),
  deleteBoard: (boardId) => request(`/api/boards/${boardId}`, { method: 'DELETE' }),
  listMockRules: (boardId) => request(`/api/boards/${boardId}/mocks`),
  createMockRule: (boardId, payload) => request(`/api/boards/${boardId}/mocks`, { method: 'POST', body: payload }),
  updateMockRule: (boardId, mockRuleId, payload) =>
    request(`/api/boards/${boardId}/mocks/${mockRuleId}`, { method: 'PUT', body: payload }),
  deleteMockRule: (boardId, mockRuleId) =>
    request(`/api/boards/${boardId}/mocks/${mockRuleId}`, { method: 'DELETE' }),
  listWebhooks: (boardId) => request(`/api/boards/${boardId}/webhooks`)
};

async function request(path, options = {}) {
  const { method = 'GET', body, auth = true } = options;
  const headers = {
    Accept: 'application/json'
  };

  if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }

  if (auth && appToken) {
    headers[tokenHeader] = appToken;
  }

  const response = await fetch(path, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  if (response.status === 204) {
    if (!response.ok) throw await asError(response);
    return null;
  }

  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message = typeof payload === 'object' && payload?.message
      ? payload.message
      : `Request failed with ${response.status}`;
    const error = new Error(message);
    error.status = response.status;
    error.payload = payload;
    throw error;
  }

  return payload;
}

async function asError(response) {
  const error = new Error(`Request failed with ${response.status}`);
  error.status = response.status;
  return error;
}
