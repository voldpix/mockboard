package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.cache.BoardCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PreBoardService {

    private final BoardCache boardCache;

    private final Map<String, Map<String, Object>> staticConfigs;

    public PreBoardService(BoardCache boardCache) {
        this.boardCache = boardCache;
        this.staticConfigs = buildStaticConfigs();
        log.info("PreBoardService initialized");
    }

    public Map<String, Object> getAppConfigs() {
        var result = new HashMap<String, Object>(staticConfigs);
        result.put("boards", buildBoardConfigs());
        return result;
    }

    private Map<String, Object> buildBoardConfigs() {
        var boards = new HashMap<String, Object>();
        boards.put("activeBoards", boardCache.size());
        boards.put("maxActiveBoards", Constants.MAX_ACTIVE_BOARDS);
        return boards;
    }

    private Map<String, Map<String, Object>> buildStaticConfigs() {
        var configs = new HashMap<String, Map<String, Object>>();

        var appData = new HashMap<String, Object>();
        appData.put("version", Constants.APP_VERSION);
        configs.put("app", Map.copyOf(appData));

        var validations = new HashMap<String, Object>();
        validations.put("maxMocks", Constants.MAX_MOCK_RULES);
        validations.put("maxWebhooks", Constants.MAX_WEBHOOKS);
        validations.put("maxMockPathLength", Constants.MAX_PATH_LENGTH);
        validations.put("maxMockPathWildcards", Constants.MAX_WILDCARDS);
        validations.put("maxMockHeaders", Constants.MAX_HEADERS_SIZE);
        validations.put("maxMockHeaderKeyLength", Constants.MAX_HEADER_KEY_LENGTH);
        validations.put("maxMockHeaderValueLength", Constants.MAX_HEADER_VALUE_LENGTH);
        validations.put("maxMockBodyLength", Constants.MAX_BODY_LENGTH);
        validations.put("supportedHttpMethods", Constants.VALID_HTTP_METHODS);
        configs.put("validations", Map.copyOf(validations));
        return Map.copyOf(configs);
    }
}
