export default {
    // base
    APP_VERSION: '0.0.1-alpha',
    SERVER_URL: 'http://localhost:8000',

    // ls
    BOARD_ID_LS_KEY: 'BOARD_ID',
    OWNER_TOKEN_LS_KEY: 'OWNER_TOKEN',
    SESSION_OVERLAY_REASK_TTL_LS_KEY: 'OVERLAY_REASK_TTL',
    REASK_TTL: 60 * 60 * 1000, // 1h

    // http
    OWNER_TOKEN_HEADER_KEY: 'X-Owner-Token',

    // app
    MAX_MOCKS: 12,
    DASHBOARD_VIEWS: {
        dashboard: 'dashboard',
        create_mock: 'create_mock',
        edit_mock: 'edit_mock',
        log_details: 'log_details',
    },
}
