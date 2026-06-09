export const supportedMethods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS'];

export function formFromRule(rule) {
  const headers = parseHeaders(rule?.headers);
  return {
    method: rule?.method || 'GET',
    path: rule?.path || '',
    statusCode: rule?.statusCode ?? 200,
    delay: rule?.delay ?? 0,
    headers: headers.length ? headers : [{ key: 'Content-Type', value: 'application/json' }],
    body: rule?.body || ''
  };
}

export function validateMockForm(form, appConfig) {
  const limits = appConfig?.validations || {};
  const errors = {};
  const path = (form.path || '').trim();
  const statusCode = Number(form.statusCode);
  const delay = Number(form.delay);

  if (!supportedMethods.includes((form.method || '').toUpperCase())) {
    errors.method = 'Unsupported HTTP method';
  }

  if (!path) {
    errors.path = 'Path is required';
  } else if (!path.startsWith('/')) {
    errors.path = 'Path must start with /';
  } else if (limits.maxMockPathLength && path.length > limits.maxMockPathLength) {
    errors.path = `Path must be ${limits.maxMockPathLength} characters or less`;
  }

  const wildcardCount = [...path].filter((char) => char === '*').length;
  if (limits.maxMockPathWildcards && wildcardCount > limits.maxMockPathWildcards) {
    errors.path = `Path can use at most ${limits.maxMockPathWildcards} wildcards`;
  }

  if (!Number.isInteger(statusCode) || statusCode < 100 || statusCode > 599) {
    errors.statusCode = 'Status must be an HTTP status code from 100 to 599';
  }

  if (!Number.isInteger(delay) || delay < 0 || delay > 10000) {
    errors.delay = 'Delay must be between 0 and 10000 ms';
  }

  const nonEmptyHeaders = form.headers.filter((header) => header.key.trim() || header.value.trim());
  if (limits.maxMockHeaders && nonEmptyHeaders.length > limits.maxMockHeaders) {
    errors.headers = `Use ${limits.maxMockHeaders} headers or fewer`;
  }

  nonEmptyHeaders.forEach((header, index) => {
    if (!header.key.trim()) {
      errors[`header-${index}`] = 'Header name is required';
    }
  });

  const body = (form.body || '').trim();
  if (body) {
    if (limits.maxMockBodyLength && body.length > limits.maxMockBodyLength) {
      errors.body = `Body must be ${limits.maxMockBodyLength} characters or less`;
    } else {
      try {
        JSON.parse(body);
      } catch {
        errors.body = 'Body must be valid JSON';
      }
    }
  }

  return errors;
}

export function toMockPayload(form) {
  const headers = {};
  form.headers.forEach((header) => {
    const key = header.key.trim();
    if (!key) return;
    headers[key] = header.value;
  });

  const body = (form.body || '').trim();
  return {
    method: form.method.toUpperCase(),
    path: form.path.trim(),
    statusCode: Number(form.statusCode),
    delay: Number(form.delay),
    headers: Object.keys(headers).length ? JSON.stringify(headers) : null,
    body: body ? JSON.stringify(JSON.parse(body)) : null
  };
}

function parseHeaders(headers) {
  if (!headers) return [];
  try {
    return Object.entries(JSON.parse(headers)).map(([key, value]) => ({
      key,
      value: value == null ? '' : String(value)
    }));
  } catch {
    return [];
  }
}
