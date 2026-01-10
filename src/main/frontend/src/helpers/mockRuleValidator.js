import constants from "@/constants.js"
const { VALIDATION } = constants

export const validateMockRule = (formData) => {
    const errors = {}

    const pathErrors = validatePath(formData.path)
    if (pathErrors) errors.path = pathErrors

    const methodErrors = validateMethod(formData.method)
    if (methodErrors) errors.method = methodErrors

    const statusErrors = validateStatusCode(formData.statusCode)
    if (statusErrors) errors.statusCode = statusErrors

    const bodyErrors = validateBody(formData.body)
    if (bodyErrors) errors.body = bodyErrors

    const headersErrors = validateHeaders(formData.headers)
    if (headersErrors) {
        Object.assign(errors, headersErrors)
    }

    return errors;
};

const validatePath = (path) => {
    if (!path || path.trim() === '') {
        return 'Path cannot be empty'
    }

    if (!path.startsWith('/')) {
        return 'Path must start with /'
    }

    if (path.length > VALIDATION.MAX_PATH_LENGTH) {
        return `Path exceeds maximum length of ${VALIDATION.MAX_PATH_LENGTH} characters`
    }

    if (!VALIDATION.VALID_PATH_PATTERN.test(path)) {
        return 'Path contains invalid characters. Allowed: a-z, A-Z, 0-9, /, _, -, *'
    }

    const wildcardCount = (path.match(/\*/g) || []).length;
    if (wildcardCount > VALIDATION.MAX_WILDCARDS) {
        return `Path cannot have more than ${VALIDATION.MAX_WILDCARDS} wildcards`
    }

    if (path.includes('**')) {
        return 'Adjacent wildcards (**) are not allowed'
    }

    return null;
}

const validateMethod = (method) => {
    if (!method || method.trim() === '') {
        return 'HTTP method cannot be empty'
    }

    if (!VALIDATION.VALID_HTTP_METHODS.includes(method.toUpperCase())) {
        return `Invalid HTTP method: ${method}`
    }
    return null;
}

const validateStatusCode = (statusCode) => {
    const code = parseInt(statusCode, 10)

    if (isNaN(code)) {
        return 'Status code must be a number'
    }

    if (code < 100 || code > 599) {
        return 'Invalid HTTP status code (must be 100-599)'
    }

    return null;
};

const validateBody = (body) => {
    if (!body || body.trim() === '') {
        return null;
    }

    try {
        JSON.parse(body)
    } catch (e) {
        return 'Body must be valid JSON'
    }

    const bodyBytes = new TextEncoder().encode(body).length
    if (bodyBytes > VALIDATION.MAX_BODY_LENGTH) {
        const maxKB = (VALIDATION.MAX_BODY_LENGTH / 1000).toFixed(1)
        return `Body too large (max ${maxKB}KB)`
    }

    return null
}

const validateHeaders = (headers) => {
    if (!Array.isArray(headers)) {
        return null
    }

    const errors = {}

    const nonEmptyHeaders = headers.filter(h => h.key && h.key.trim() !== '')

    if (nonEmptyHeaders.length > VALIDATION.MAX_HEADERS) {
        errors.headers = `Too many headers (max ${VALIDATION.MAX_HEADERS} allowed)`
        return errors;
    }

    nonEmptyHeaders.forEach((header, index) => {
        const { key, value } = header;

        if (!key || key.trim() === '') {
            return;
        }

        if (!value || value.trim() === '') {
            errors[`header_${index}`] = 'Header value is required when key is provided'
            return
        }

        if (key.length > VALIDATION.MAX_HEADER_KEY_LENGTH) {
            errors[`header_${index}`] = `Header key too long (max ${VALIDATION.MAX_HEADER_KEY_LENGTH} characters)`
        }

        if (value.length > VALIDATION.MAX_HEADER_VALUE_LENGTH) {
            errors[`header_${index}`] = `Header value too long (max ${VALIDATION.MAX_HEADER_VALUE_LENGTH} characters)`
        }
    })

    return Object.keys(errors).length > 0 ? errors : null
}

export const getCharacterCount = (text) => {
    if (!text) return { chars: 0, bytes: 0 }
    return {
        chars: text.length,
        bytes: new TextEncoder().encode(text).length
    }
}

export const getLimitStatus = (current, max) => {
    const percentage = (current / max) * 100
    return {
        current,
        max,
        percentage: Math.min(percentage, 100),
        isNearLimit: percentage >= 80,
        isAtLimit: percentage >= 100,
        remaining: Math.max(0, max - current)
    }
}