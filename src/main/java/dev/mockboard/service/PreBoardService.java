package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.cache.BoardCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PreBoardService {

    private static final Map<String, Object> STATE = new HashMap<>();

    private final BoardCache boardCache;

    public Map<String, Object> getAppConfigs() {
        var resultMap = new HashMap<String, Map<String, Object>>();
        if (STATE.isEmpty()) {
            var boardConfigs = new HashMap<String, Object>();
            boardConfigs.put("activeBoards", boardCache.size());
            boardConfigs.put("maxActiveBoards", Constants.MAX_ACTIVE_BOARDS);
            resultMap.put("boards", boardConfigs);

            // sync be + fe app configs
            var validationConfigs = new HashMap<String, Object>();
            validationConfigs.put("maxMocks", Constants.MAX_MOCK_RULES);
            validationConfigs.put("maxWebhooks", Constants.MAX_WEBHOOKS);
            validationConfigs.put("maxMockPathLength", Constants.MAX_PATH_LENGTH);
            validationConfigs.put("maxMockPathWildcards", Constants.MAX_WILDCARDS);
            validationConfigs.put("maxMockHeaders", Constants.MAX_HEADERS_SIZE);
            validationConfigs.put("maxMockHeaderKeyLength", Constants.MAX_HEADER_KEY_LENGTH);
            validationConfigs.put("maxMockHeaderValueLength", Constants.MAX_HEADER_VALUE_LENGTH);
            validationConfigs.put("maxMockBodyLength", Constants.MAX_BODY_LENGTH);
            validationConfigs.put("supportedHttpMethods", Constants.VALID_HTTP_METHODS);
            resultMap.put("validations", validationConfigs);
            STATE.putAll(resultMap);
        }
        return STATE;
    }
}
