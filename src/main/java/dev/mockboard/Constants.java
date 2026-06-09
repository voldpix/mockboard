package dev.mockboard;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String APP_VERSION = "0.3-beta";

    // defaults
    public static final int BOARD_ID_LENGTH = 24;
    public static final int APP_TOKEN_LENGTH = 48;
    public static final String WILDCARD = "*";
    public static final String APP_TOKEN_HEADER_KEY = "X-App-Token";
    public static final String STORE_PATH = Env.get("MBD_STORE_PATH", "data/mockboard.db");

    // app limits
    public static final int MAX_MOCK_RULES = Env.getInt("MBD_MAX_MOCK_RULES", 1_000);
    public static final int MAX_WEBHOOKS = Env.getInt("MBD_MAX_WEBHOOKS", 100);

    // validations
    public static final int MAX_BOARD_NAME_LENGTH = Env.getInt("MBD_VALIDATION_BOARD_MAX_NAME_LENGTH", 80);
    public static final Pattern VALID_PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9/_\\-*]+$");
    public static final Set<String> VALID_HTTP_METHODS = Set.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS");
    public static final int MAX_PATH_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_PATH_LENGTH", 1_000);
    public static final int MAX_QUERY_STRING_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_QUERY_STRING_LENGTH", 250);
    public static final int MAX_BODY_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_BODY_LENGTH", 100_000);
    public static final int MAX_WILDCARDS = Env.getInt("MBD_VALIDATION_MOCK_MAX_WILDCARDS", 10);
    public static final int MAX_HEADERS_SIZE = Env.getInt("MBD_VALIDATION_MOCK_MAX_HEADERS_SIZE", 50);
    public static final int MAX_WEBHOOK_HEADERS_SIZE = Env.getInt("MBD_VALIDATION_MOCK_MAX_WEBHOOK_HEADERS_SIZE", 50);
    public static final int MAX_HEADER_KEY_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_HEADER_KEY_LENGTH", 100);
    public static final int MAX_HEADER_VALUE_LENGTH = Env.getInt("MBD_VALIDATION_MOCK_MAX_HEADER_VALUE_LENGTH", 500);
    public static final int MAX_ALLOWED_DELAY = Env.getInt("MBD_VALIDATION_MOCK_MAX_ALLOWED_DELAY", 10_000);

    // sse
    public static final long SSE_EMITTER_HEARTBEAT_RATE = 30_000L; // 30sec
    public static final String SSE_EMITTER_EVENT_WEBHOOK = "webhook-event";
    public static final String SSE_EMITTER_EVENT_PING = "ping";
    public static final String SSE_EMITTER_EVENT_SHUTDOWN = "server-shutdown";

    // default messages
    public static final String DEFAULT_EXECUTION_RESPONSE = "{\"message\": \"Hello from Mockboard.dev\"}";
}
