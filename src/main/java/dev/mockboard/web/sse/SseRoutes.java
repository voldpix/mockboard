package dev.mockboard.web.sse;

import dev.mockboard.Constants;
import dev.mockboard.service.AppSecurityService;
import dev.mockboard.service.BoardService;
import io.javalin.http.sse.SseClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SseRoutes {

    private final AppSecurityService appSecurityService;
    private final BoardService boardService;
    private final SseManager sseManager;

    public void subscribe(SseClient client) {
        var ctx = client.ctx();
        appSecurityService.validateAnyToken(ctx.header(Constants.APP_TOKEN_HEADER_KEY), ctx.queryParam("token"));
        var boardDto = boardService.getBoardDto(ctx.pathParam("boardId"));
        sseManager.subscribe(boardDto, client);
    }
}
