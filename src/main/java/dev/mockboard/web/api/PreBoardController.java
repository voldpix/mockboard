package dev.mockboard.web.api;

import dev.mockboard.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pre")
public class PreBoardController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPreBoards() {
        return new ResponseEntity<>(getAppConfigs(), HttpStatus.OK);
    }

    private Map<String, Object> getAppConfigs() {
        return Map.of(
                "app", Map.of(
                        "version", Constants.APP_VERSION
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
