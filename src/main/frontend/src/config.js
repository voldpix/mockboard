const DEFAULT_CONFIG = {
    app: {
        version: '0.1-default',
    },
    boards: {
        activeBoards: 0,
        maxActiveBoards: 500
    },
    validations: {
        maxMocks: 12,
        maxWebhooks: 15,
        maxMockPathLength: 200,
        mockPathPattern: /^\/[a-zA-Z0-9/_\-*{}]+$/,
        maxMockPathWildcards: 3,
        maxMockHeaders: 5,
        maxMockHeaderKeyLength: 100,
        maxMockHeaderValueLength: 500,
        maxMockBodyLength: 5000,
        supportedHttpMethods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS'],
        minStatusCode: 100,
        maxStatusCode: 600,
    }
}

let config = null

function mapConfig(backendData) {
    return {
        app: {
            version: backendData.app?.version ?? DEFAULT_CONFIG.app?.version,
        },
        boards: {
            activeBoards: backendData.boards?.activeBoards ?? DEFAULT_CONFIG.boards.activeBoards,
            maxActiveBoards: backendData.boards?.maxActiveBoards ?? DEFAULT_CONFIG.boards.maxActiveBoards
        },
        validations: {
            maxMocks: backendData.validations?.maxMocks ?? DEFAULT_CONFIG.validations.maxMocks,
            maxWebhooks: backendData.validations?.maxWebhooks ?? DEFAULT_CONFIG.validations.maxWebhooks,
            maxMockPathLength: backendData.validations?.maxMockPathLength ?? DEFAULT_CONFIG.validations.maxMockPathLength,
            maxMockPathWildcards: backendData.validations?.maxMockPathWildcards ?? DEFAULT_CONFIG.validations.maxMockPathWildcards,
            maxMockHeaders: backendData.validations?.maxMockHeaders ?? DEFAULT_CONFIG.validations.maxMockHeaders,
            maxMockHeaderKeyLength: backendData.validations?.maxMockHeaderKeyLength ?? DEFAULT_CONFIG.validations.maxMockHeaderKeyLength,
            maxMockHeaderValueLength: backendData.validations?.maxMockHeaderValueLength ?? DEFAULT_CONFIG.validations.maxMockHeaderValueLength,
            maxMockBodyLength: backendData.validations?.maxMockBodyLength ?? DEFAULT_CONFIG.validations.maxMockBodyLength,
            supportedHttpMethods: backendData.validations?.supportedHttpMethods ?? DEFAULT_CONFIG.validations.supportedHttpMethods,

            // no BE update expected, use default
            mockPathPattern: backendData.validations?.mockPathPattern ?? DEFAULT_CONFIG.validations.mockPathPattern,
            minStatusCode: backendData.validations?.minStatusCode ?? DEFAULT_CONFIG.validations.minStatusCode,
            maxStatusCode: backendData.validations?.maxStatusCode ?? DEFAULT_CONFIG.validations.maxStatusCode,
        }
    }
}

export async function initConfig(apiCall) {
    try {
        const backendData = await apiCall(apiCall);
        config = mapConfig(backendData);
        console.log('Config loaded');
    } catch (err) {
        console.error('Config load failed, fallback to default');
        config = DEFAULT_CONFIG
    }
}

export function getConfig() {
    return config
}