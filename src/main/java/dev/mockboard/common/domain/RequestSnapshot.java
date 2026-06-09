package dev.mockboard.common.domain;

import java.util.Map;

public record RequestSnapshot(
        String method,
        String path,
        String fullUrl,
        String queryString,
        Map<String, String> headers,
        String body,
        String contentType,
        long contentLength
) {}
