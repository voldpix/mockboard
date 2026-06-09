package dev.mockboard.web.api;

import dev.mockboard.common.validator.RequestMetadataValidator;
import dev.mockboard.service.MockExecutionService;
import dev.mockboard.service.WebhookService;
import dev.mockboard.web.HttpResponses;
import dev.mockboard.web.RequestSnapshots;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class MockExecutionRoutes {

    private final RequestMetadataValidator requestMetadataValidator;
    private final MockExecutionService mockExecutionService;
    private final WebhookService webhookService;

    public void executeMock(Context ctx) {
        var executionStart = System.currentTimeMillis();
        var boardId = ctx.pathParam("boardId");
        var metadata = requestMetadataValidator.validateAndGet(boardId, RequestSnapshots.from(ctx));
        var result = mockExecutionService.execute(boardId, metadata);

        var executionTime = System.currentTimeMillis() - executionStart;
        log.debug("Execution time: {}ms", executionTime);
        webhookService.processWebhookAsync(boardId, metadata, result, executionTime);

        ctx.status(result.statusCode());
        HttpResponses.applyHeaders(ctx, result.headers());
        ctx.result(result.responseBody());
    }
}
