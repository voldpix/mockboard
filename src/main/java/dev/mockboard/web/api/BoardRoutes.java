package dev.mockboard.web.api;

import dev.mockboard.common.domain.dto.MockRuleDto;
import dev.mockboard.common.domain.dto.BoardUpdateDto;
import dev.mockboard.common.exception.BadRequestException;
import dev.mockboard.service.BoardService;
import dev.mockboard.service.MockRuleService;
import dev.mockboard.service.WebhookService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardRoutes {

    private final BoardService boardService;
    private final MockRuleService mockRuleService;
    private final WebhookService webhookService;

    public void createBoard(Context ctx) {
        var boardDto = boardService.createBoard();
        ctx.status(HttpStatus.CREATED).json(boardDto);
    }

    public void getBoards(Context ctx) {
        ctx.status(HttpStatus.OK).json(boardService.getBoards());
    }

    public void getBoard(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        ctx.status(HttpStatus.OK).json(boardDto);
    }

    public void updateBoard(Context ctx) {
        var boardDto = boardService.updateBoardName(boardId(ctx), boardUpdate(ctx).getName());
        ctx.status(HttpStatus.OK).json(boardDto);
    }

    public void deleteBoard(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        boardService.deleteBoard(boardDto);
        webhookService.deleteByBoardId(boardDto.getId());
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void addMockRule(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        var mockId = mockRuleService.createMockRule(boardDto, mockRule(ctx));
        ctx.status(HttpStatus.CREATED).json(mockId);
    }

    public void getMockRules(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        var mockRules = mockRuleService.getMockRules(boardDto);
        ctx.status(HttpStatus.OK).json(mockRules);
    }

    public void updateMockRule(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        var response = mockRuleService.updateMockRule(boardDto, ctx.pathParam("mockRuleId"), mockRule(ctx));
        ctx.status(HttpStatus.OK).json(response);
    }

    public void deleteMockRule(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        mockRuleService.deleteMockRule(boardDto, ctx.pathParam("mockRuleId"));
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void getWebhooks(Context ctx) {
        var boardDto = boardService.getBoardDto(boardId(ctx));
        var webhooks = webhookService.getWebhooks(boardDto);
        ctx.status(HttpStatus.OK).json(webhooks);
    }

    private String boardId(Context ctx) {
        return ctx.pathParam("boardId");
    }

    private MockRuleDto mockRule(Context ctx) {
        try {
            return ctx.bodyAsClass(MockRuleDto.class);
        } catch (Exception e) {
            throw new BadRequestException("Invalid request body");
        }
    }

    private BoardUpdateDto boardUpdate(Context ctx) {
        try {
            return ctx.bodyAsClass(BoardUpdateDto.class);
        } catch (Exception e) {
            throw new BadRequestException("Invalid request body");
        }
    }
}
