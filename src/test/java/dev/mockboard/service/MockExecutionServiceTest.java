package dev.mockboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mockboard.Constants;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.MockRuleDto;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MockExecutionServiceTest {

    @Test
    void executeDefaultResponse() {
        var result = service(Collections.emptyList()).execute("board-123", metadata("GET", "/api/test"));

        assertThat(result.statusCode()).isEqualTo(200);
        assertThat(result.responseBody()).isEqualTo(Constants.DEFAULT_EXECUTION_RESPONSE);
        assertThat(result.matchingMockRuleDto()).isNull();
        assertThat(result.headers().get(MockExecutionService.CONTENT_TYPE_HEADER))
                .containsExactly(MockExecutionService.JSON_CONTENT_TYPE);
    }

    @Test
    void executeExactPathMatch() {
        var rule = rule("POST", "/api/users", "{\"id\":123}", 201);

        var result = service(List.of(rule)).execute("board-123", metadata("POST", "/api/users"));

        assertThat(result.statusCode()).isEqualTo(201);
        assertThat(result.responseBody()).isEqualTo("{\"id\":123}");
        assertThat(result.matchingMockRuleDto()).isEqualTo(rule);
    }

    @Test
    void executeWildcardPath() {
        var rule = rule("GET", "/api/users/*/profile", "{\"profile\":\"data\"}", 200);

        var result = service(List.of(rule)).execute("board-123", metadata("GET", "/api/users/123/profile"));

        assertThat(result.statusCode()).isEqualTo(200);
        assertThat(result.matchingMockRuleDto()).isEqualTo(rule);
    }

    @Test
    void executeMethodMismatch() {
        var rule = rule("GET", "/api/test", null, 200);

        var result = service(List.of(rule)).execute("board-123", metadata("POST", "/api/test"));

        assertThat(result.matchingMockRuleDto()).isNull();
        assertThat(result.statusCode()).isEqualTo(200);
    }

    @Test
    void executeTemplatesInBody() {
        var rule = rule("GET", "/api/test", "{\"name\":\"{{user.fullName}}\"}", 200);

        var result = service(List.of(rule)).execute("board-123", metadata("GET", "/api/test"));

        assertThat(result.responseBody()).isEqualTo("{\"name\":\"John Doe\"}");
    }

    private MockExecutionService service(List<MockRuleDto> rules) {
        return new MockExecutionService(
                new ObjectMapper(),
                new StaticMockRuleService(rules),
                new StaticTemplateFakerService()
        );
    }

    private RequestMetadata metadata(String method, String path) {
        return new RequestMetadata(method, path, path, "http://localhost" + path, "", "", null, null);
    }

    private MockRuleDto rule(String method, String path, String body, int statusCode) {
        var rule = new MockRuleDto();
        rule.setId(method + "-" + path);
        rule.setMethod(method);
        rule.setPath(path);
        rule.setBody(body);
        rule.setStatusCode(statusCode);
        rule.setDelay(0);
        rule.compilePattern();
        return rule;
    }

    private static class StaticMockRuleService extends MockRuleService {

        private final List<MockRuleDto> rules;

        private StaticMockRuleService(List<MockRuleDto> rules) {
            super(null, null);
            this.rules = rules;
        }

        @Override
        public List<MockRuleDto> getMockRules(BoardDto boardDto) {
            return rules;
        }
    }

    private static class StaticTemplateFakerService extends TemplateFakerService {

        private StaticTemplateFakerService() {
            super(null);
        }

        @Override
        public String processTemplates(String body) {
            return body.replace("{{user.fullName}}", "John Doe");
        }
    }
}
