const dateFormatter = new Intl.DateTimeFormat(undefined, {
  dateStyle: 'medium',
  timeStyle: 'short'
});

export function formatDate(value) {
  if (!value) return 'No timestamp';
  const date = new Date(normalizeDateValue(value));
  return Number.isNaN(date.getTime()) ? 'Invalid timestamp' : dateFormatter.format(date);
}

export function formatMs(value) {
  return Number.isFinite(Number(value)) ? `${value} ms` : '0 ms';
}

export function mockUrl(boardId) {
  if (!boardId) return '';
  return `${window.location.origin}/m/${boardId}`;
}

export function prettyJson(value, fallback = '') {
  if (value == null || value === '') return fallback;
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch {
    return String(value);
  }
}

export function methodClass(method) {
  switch ((method || '').toUpperCase()) {
    case 'GET':
      return 'method-get';
    case 'POST':
      return 'method-post';
    case 'PUT':
      return 'method-put';
    case 'PATCH':
      return 'method-patch';
    case 'DELETE':
      return 'method-delete';
    default:
      return 'method-default';
  }
}

function normalizeDateValue(value) {
  if (typeof value === 'number') {
    return value < 1_000_000_000_000 ? value * 1000 : value;
  }
  return value;
}
