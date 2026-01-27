import {getConfig} from "@/config.js";

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
    const v = getConfig().validations
    if (!path || path.trim() === '') {
        return 'Path cannot be empty'
    }

    if (!path.startsWith('/')) {
        return 'Path must start with /'
    }

    if (path.length > v.maxMockPathLength) {
        return `Path exceeds maximum length of ${v.maxMockPathLength} characters`
    }

    if (!v.mockPathPattern.test(path)) {
        return 'Path contains invalid characters. Allowed: a-z, A-Z, 0-9, /, _, -, *'
    }

    const wildcardCount = (path.match(/\*/g) || []).length;
    if (wildcardCount > v.maxMockPathWildcards) {
        return `Path cannot have more than ${v.maxMockPathWildcards} wildcards`
    }

    if (path.includes('**')) {
        return 'Adjacent wildcards (**) are not allowed'
    }

    return null;
}

const validateMethod = (method) => {
    const v = getConfig().validations
    if (!method || method.trim() === '') {
        return 'HTTP method cannot be empty'
    }

    if (!v.supportedHttpMethods.includes(method.toUpperCase())) {
        return `Invalid HTTP method: ${method}`
    }
    return null;
}

const validateStatusCode = (statusCode) => {
    const v = getConfig().validations
    const code = parseInt(statusCode, 10)

    if (isNaN(code)) {
        return 'Status code must be a number'
    }

    if (code < v.minStatusCode || code > v.maxStatusCode) {
        return `Invalid HTTP status code (must be ${v.minStatusCode}-${v.maxStatusCode})`
    }

    return null;
};

const validateBody = (body) => {
    const v = getConfig().validations

    if (!body || body.trim() === '') {
        return null;
    }

    try {
        JSON.parse(body)
    } catch (e) {
        return 'Body must be valid JSON'
    }

    const bodyBytes = new TextEncoder().encode(body).length
    if (bodyBytes > v.maxBodyLength) {
        const maxKB = (v.maxBodyLength / 1000).toFixed(1)
        return `Body too large (max ${maxKB}KB)`
    }

    return null
}

const validateHeaders = (headers) => {
    const v = getConfig().validations
    if (!Array.isArray(headers)) {
        return null
    }

    const errors = {}

    const nonEmptyHeaders = headers.filter(h => h.key && h.key.trim() !== '')

    if (nonEmptyHeaders.length > v.maxMockHeaders) {
        errors.headers = `Too many headers (max ${v.maxMockHeaders} allowed)`
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

        if (key.length > v.maxMockHeaderKeyLength) {
            errors[`header_${index}`] = `Header key too long (max ${v.maxMockHeaderKeyLength} characters)`
        }

        if (value.length > v.maxMockHeaderValueLength) {
            errors[`header_${index}`] = `Header value too long (max ${v.maxMockHeaderValueLength} characters)`
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