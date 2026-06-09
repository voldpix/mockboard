package dev.mockboard.web;

import dev.mockboard.web.api.BoardRoutes;
import dev.mockboard.web.api.MockExecutionRoutes;
import dev.mockboard.web.api.PreRoutes;
import dev.mockboard.web.sse.SseRoutes;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Routes {

    private final AuthHandler authHandler;
    private final BoardRoutes boardRoutes;
    private final PreRoutes preRoutes;
    private final MockExecutionRoutes mockExecutionRoutes;
    private final SseRoutes sseRoutes;

    public Routes(dev.mockboard.service.AppSecurityService appSecurityService,
                  BoardRoutes boardRoutes,
                  PreRoutes preRoutes,
                  MockExecutionRoutes mockExecutionRoutes,
                  SseRoutes sseRoutes) {
        this(new AuthHandler(appSecurityService), boardRoutes, preRoutes, mockExecutionRoutes, sseRoutes);
    }

    public void register() {
        ApiBuilder.before("/api/boards", authHandler::requireAppToken);
        ApiBuilder.before("/api/boards/*", authHandler::requireAppToken);

        ApiBuilder.path("/api/pre", () -> ApiBuilder.get(preRoutes::getPreBoards));

        ApiBuilder.path("/api/boards", () -> {
            ApiBuilder.post(boardRoutes::createBoard);
            ApiBuilder.get(boardRoutes::getBoards);
            ApiBuilder.path("/{boardId}", () -> {
                ApiBuilder.get(boardRoutes::getBoard);
                ApiBuilder.put(boardRoutes::updateBoard);
                ApiBuilder.delete(boardRoutes::deleteBoard);
                ApiBuilder.sse("/stream", sseRoutes::subscribe);
                ApiBuilder.path("/mocks", () -> {
                    ApiBuilder.post(boardRoutes::addMockRule);
                    ApiBuilder.get(boardRoutes::getMockRules);
                    ApiBuilder.path("/{mockRuleId}", () -> {
                        ApiBuilder.put(boardRoutes::updateMockRule);
                        ApiBuilder.delete(boardRoutes::deleteMockRule);
                    });
                });
                ApiBuilder.get("/webhooks", boardRoutes::getWebhooks);
            });
        });

        registerMockExecutionRoutes();
    }

    private void registerMockExecutionRoutes() {
        Handler handler = mockExecutionRoutes::executeMock;
        for (var path : new String[] {"/m/{boardId}", "/m/{boardId}/*"}) {
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.GET, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.POST, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.PUT, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.PATCH, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.DELETE, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.OPTIONS, path, handler);
            ApiBuilder.staticInstance().addHttpHandler(HandlerType.HEAD, path, handler);
        }
    }
}
