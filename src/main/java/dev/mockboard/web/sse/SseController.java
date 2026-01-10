package dev.mockboard.web.sse;

import dev.mockboard.service.BoardSecurityService;
import dev.mockboard.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static dev.mockboard.Constants.OWNER_TOKEN_HEADER_KEY;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class SseController {

    private final BoardSecurityService boardSecurityService;
    private final SseService sseService;

    @GetMapping("/{boardId}/stream")
    public SseEmitter subscribe(@PathVariable String boardId,
                                @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
        var boardDto = boardSecurityService.validateOwnershipAndGet(boardId, ownerToken);
        return sseService.subscribe(boardDto);
    }
}
