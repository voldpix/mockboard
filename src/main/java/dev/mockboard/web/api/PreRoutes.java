package dev.mockboard.web.api;

import dev.mockboard.Constants;
import dev.mockboard.service.AppSecurityService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class PreRoutes {

    private final AppSecurityService appSecurityService;

    public void getPreBoards(Context ctx) {
        ctx.status(HttpStatus.OK).json(getAppConfigs());
    }

    private Map<String, Object> getAppConfigs() {
        return Map.of(
                "app", Map.of(
                        "version", Constants.APP_VERSION,
                        "token", appSecurityService.getAppToken(),
                        "tokenHeader", Constants.APP_TOKEN_HEADER_KEY
                ),
                "boards", Map.of(
                        "activeBoards", 0,
                        "maxActiveBoards", Integer.MAX_VALUE
                ),
                "validations", Map.of(
                        "maxMocks", Constants.MAX_MOCK_RULES,
                        "maxWebhooks", Constants.MAX_WEBHOOKS,
                        "maxMockPathLength", Constants.MAX_PATH_LENGTH,
                        "maxMockPathWildcards", Constants.MAX_WILDCARDS,
                        "maxMockHeaders", Constants.MAX_HEADERS_SIZE,
                        "maxMockHeaderKeyLength", Constants.MAX_HEADER_KEY_LENGTH,
                        "maxMockHeaderValueLength", Constants.MAX_HEADER_VALUE_LENGTH,
                        "maxMockBodyLength", Constants.MAX_BODY_LENGTH,
                        "supportedHttpMethods", Constants.VALID_HTTP_METHODS
                )
        );
    }
}
