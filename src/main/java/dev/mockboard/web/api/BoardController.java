package dev.mockboard.web.api;

import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.service.BoardSecurityService;
import dev.mockboard.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dev.mockboard.Constants.OWNER_TOKEN_HEADER_KEY;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardSecurityService boardSecurityService;

    @PostMapping
    public ResponseEntity<BoardDto> createBoard(HttpServletRequest _request) {
        var boardDto = boardService.createBoard();
        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable String boardId,
                                             @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
        var boardDto = boardSecurityService.validateOwnershipAndGet(boardId, ownerToken);
        return new ResponseEntity<>(boardDto, HttpStatus.OK);
    }

    //    private final MockRuleService mockRuleService;
//    private final BoardSecurityService boardSecurityService;
//    private final BoardCreationRateLimiter boardCreationRateLimiter;

//    @GetMapping("/{boardId}/check")
//    public ResponseEntity<BoardDto> checkBoardExists(@PathVariable String boardId,
//                                                     @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        return new ResponseEntity<>(boardDto, HttpStatus.OK);
//    }
//
//    @PostMapping
//    public ResponseEntity<BoardDto> createBoard(HttpServletRequest request) {
//        var ipAddress = RequestUtils.getClientIp(request);
//        boardCreationRateLimiter.checkLimit(ipAddress);
//
//        var boardDto = boardService.createBoard();
//        return new ResponseEntity<>(boardDto, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{boardId}")
//    public ResponseEntity<BoardDto> getBoard(@PathVariable String boardId,
//                                             @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        return new ResponseEntity<>(boardDto, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{boardId}")
//    public ResponseEntity<Void> closeBoard(@PathVariable String boardId,
//                                           @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        // todo: impl
//        return ResponseEntity.notFound().build();
//    }
//
//    @PostMapping("/{boardId}/mocks")
//    public ResponseEntity<IdResponse> addMockRule(@PathVariable String boardId,
//                                                  @RequestBody MockRuleDto mockRuleDto,
//                                                  @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        var mockId = mockRuleService.addMockRule(boardDto, mockRuleDto);
//        return new ResponseEntity<>(mockId, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{boardId}/mocks")
//    public ResponseEntity<List<MockRuleDto>> getMockRules(@PathVariable String boardId,
//                                                          @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        var mockRules = mockRuleService.getMockRuleDtos(boardDto);
//        return new ResponseEntity<>(mockRules, HttpStatus.OK);
//    }
//
//    @PutMapping("/{boardId}/mocks/{mockId}")
//    public ResponseEntity<IdResponse> updateMockRule(@PathVariable String boardId,
//                                                     @PathVariable String mockId,
//                                                     @RequestBody MockRuleDto mockRuleDto,
//                                                     @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        var response = mockRuleService.updateMockRule(boardDto, mockId, mockRuleDto);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{boardId}/mocks/{mockId}")
//    public ResponseEntity<Void> deleteMockRule(@PathVariable String boardId,
//                                               @PathVariable String mockId,
//                                               @RequestHeader(OWNER_TOKEN_HEADER_KEY) String ownerToken) {
//        var boardDto = boardSecurityService.validateOwnership(boardId, ownerToken);
//        mockRuleService.deleteMockRule(boardDto, mockId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
