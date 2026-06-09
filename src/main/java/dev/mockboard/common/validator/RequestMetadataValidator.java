package dev.mockboard.common.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mockboard.Constants;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.RequestSnapshot;
import dev.mockboard.common.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class RequestMetadataValidator {

    private final ObjectMapper objectMapper;

    public RequestMetadata validateAndGet(String boardId, RequestSnapshot request) {
        if (!Constants.VALID_HTTP_METHODS.contains(request.method())) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + request.method());
        }

        if (request.path().length() > Constants.MAX_PATH_LENGTH) {
            throw new IllegalArgumentException("Allowed path length exceeded");
        }
        if (request.queryString() != null
                && !request.queryString().isEmpty()
                && request.queryString().length() > Constants.MAX_QUERY_STRING_LENGTH) {
            throw new IllegalArgumentException("Allowed query string length exceeded");
        }

        var body = validateBody(request);
        if (!body.isBlank() && !isValidJson(body)) {
            throw new IllegalArgumentException("Invalid JSON payload");
        }

        var headers = extractHeaders(request.headers());
        return new RequestMetadata(
                request.method(),
                request.path(),
                RequestUtils.extractMockPath(boardId, request.path()),
                request.fullUrl(),
                request.queryString(),
                serializeHeaders(headers),
                body,
                request.contentType()
        );
    }

    private Map<String, String> extractHeaders(Map<String, String> requestHeaders) {
        return requestHeaders.entrySet().stream()
                .filter(entry -> entry.getKey().length() < Constants.MAX_HEADER_KEY_LENGTH)
                .filter(entry -> entry.getValue() != null && entry.getValue().length() <= Constants.MAX_HEADER_VALUE_LENGTH)
                .limit(Constants.MAX_WEBHOOK_HEADERS_SIZE)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String validateBody(RequestSnapshot request) {
        if (request.contentLength() > Constants.MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("Payload too large");
        }

        var body = request.body() == null ? "" : request.body();
        if (body.length() > Constants.MAX_BODY_LENGTH) {
            throw new IllegalArgumentException("Payload exceeds maximum size");
        }
        return body;
    }

    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            log.error("Provided invalid json", e);
            return false;
        }
    }

    private String serializeHeaders(Map<String, String> headers) {
        try {
            return objectMapper.writeValueAsString(headers);
        } catch (Exception e) {
            log.error("Unable to serialize headers", e);
            return "{}";
        }
    }
}
