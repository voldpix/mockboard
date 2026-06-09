package dev.mockboard.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.mockboard.Constants;
import dev.mockboard.common.domain.MockExecutionResult;
import dev.mockboard.common.domain.RequestMetadata;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.domain.dto.MockRuleDto;
import dev.mockboard.common.domain.response.IdResponse;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.validator.MockRuleValidator;
import dev.mockboard.common.validator.RequestMetadataValidator;
import dev.mockboard.service.AppSecurityService;
import dev.mockboard.service.BoardService;
import dev.mockboard.service.MockExecutionService;
import dev.mockboard.service.MockRuleService;
import dev.mockboard.service.TemplateFakerService;
import dev.mockboard.service.WebhookService;
import dev.mockboard.web.api.BoardRoutes;
import dev.mockboard.web.api.MockExecutionRoutes;
import dev.mockboard.web.api.PreRoutes;
import dev.mockboard.web.sse.SseManager;
import dev.mockboard.web.sse.SseRoutes;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class RoutesTest {

    @Test
    void preRouteReturnsAppTokenContract() {
        var fixture = fixture();

        JavalinTest.test(fixture.app(), (server, client) -> {
            var response = client.get("/api/pre");

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(Constants.APP_TOKEN_HEADER_KEY);
            assertThat(response.body().string()).contains(fixture.security().getAppToken());
        });
    }

    @Test
    void protectedBoardRoutesRejectMissingToken() {
        var fixture = fixture();

        JavalinTest.test(fixture.app(), (server, client) -> {
            var response = client.get("/api/boards/board-123");

            assertThat(response.code()).isEqualTo(401);
            assertThat(response.body().string()).contains("Invalid app token");
        });
    }

    @Test
    void protectedBoardRoutesAcceptValidToken() {
        var fixture = fixture();

        JavalinTest.test(fixture.app(), (server, client) -> {
            var response = client.get("/api/boards/board-123",
                    request -> request.header(Constants.APP_TOKEN_HEADER_KEY, fixture.security().getAppToken()));

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("board-123");
        });
    }

    @Test
    void mockExecutionRouteRemainsPublic() {
        var fixture = fixture();

        JavalinTest.test(fixture.app(), (server, client) -> {
            var response = client.get("/m/board-123/api/test");

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).isEqualTo(Constants.DEFAULT_EXECUTION_RESPONSE);
        });
    }

    @Test
    void errorsUseJsonResponseShape() {
        var fixture = fixture();

        JavalinTest.test(fixture.app(), (server, client) -> {
            var response = client.get("/api/boards/missing",
                    request -> request.header(Constants.APP_TOKEN_HEADER_KEY, fixture.security().getAppToken()));

            assertThat(response.code()).isEqualTo(404);
            assertThat(response.body().string()).contains("Board not found");
            assertThat(response.body().string()).contains("timestamp");
        });
    }

    private TestFixture fixture() {
        var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        var security = new AppSecurityService();
        var boardService = new StubBoardService();
        var mockRuleService = new StubMockRuleService();
        var sseManager = new SseManager();
        var webhookService = new StubWebhookService(sseManager);
        var mockExecutionService = new MockExecutionService(objectMapper, mockRuleService, new StubTemplateFakerService());

        var routes = new Routes(
                security,
                new BoardRoutes(boardService, mockRuleService, webhookService),
                new PreRoutes(security),
                new MockExecutionRoutes(new RequestMetadataValidator(objectMapper), mockExecutionService, webhookService),
                new SseRoutes(security, boardService, sseManager)
        );

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.routes.apiBuilder(routes::register);
            ErrorHandlers.register(config.routes);
        });
        return new TestFixture(app, security);
    }

    private record TestFixture(Javalin app, AppSecurityService security) {}

    private static class StubBoardService extends BoardService {
        private StubBoardService() {
            super(null, null);
        }

        @Override
        public BoardDto createBoard() {
            return board("board-123");
        }

        @Override
        public BoardDto getBoardDto(String boardId) {
            if ("missing".equals(boardId)) {
                throw new NotFoundException("Board not found by id: " + boardId);
            }
            return board(boardId);
        }

        private BoardDto board(String boardId) {
            return BoardDto.builder()
                    .id(boardId)
                    .timestamp(Instant.now())
                    .build();
        }
    }

    private static class StubMockRuleService extends MockRuleService {
        private StubMockRuleService() {
            super(new MockRuleValidator(new ObjectMapper()), null);
        }

        @Override
        public IdResponse createMockRule(BoardDto boardDto, MockRuleDto mockRuleDto) {
            return new IdResponse("mock-123");
        }

        @Override
        public List<MockRuleDto> getMockRules(BoardDto boardDto) {
            return Collections.emptyList();
        }
    }

    private static class StubTemplateFakerService extends TemplateFakerService {
        private StubTemplateFakerService() {
            super(null);
        }
    }

    private static class StubWebhookService extends WebhookService {
        private StubWebhookService(SseManager sseManager) {
            super(sseManager, Executors.newSingleThreadExecutor());
        }

        @Override
        public void processWebhookAsync(String boardId, RequestMetadata metadata, MockExecutionResult result, long executionTime) {
            // no-op
        }
    }
}
