package dev.mockboard.web;

import dev.mockboard.core.common.domain.dto.BoardDto;
import dev.mockboard.core.common.domain.dto.IdResponse;
import dev.mockboard.core.common.domain.dto.MockRuleDto;
import dev.mockboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardDto> createBoard() {
        var boardDto = boardService.createBoard();
        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
    }

    @PostMapping("/{boardId}/mocks")
    public ResponseEntity<IdResponse> addMockRule(@PathVariable String boardId,
                                                  @RequestBody MockRuleDto mockRuleDto) {
        var mockId = boardService.addMockRule(boardId, mockRuleDto);
        return new ResponseEntity<>(mockId, HttpStatus.OK);
    }

    @GetMapping("/{boardId}/mocks")
    public ResponseEntity<List<MockRuleDto>> getMockRules(@PathVariable String boardId) {
        var mockRuleDtos = boardService.getMockRules(boardId);
        return new ResponseEntity<>(mockRuleDtos, HttpStatus.OK);
    }

    @DeleteMapping("/{boardId}/mocks/{mockId}")
    public ResponseEntity<Void> deleteMockRule(@PathVariable String boardId, @PathVariable String mockId) {
        boardService.deleteMockRule(boardId, mockId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
