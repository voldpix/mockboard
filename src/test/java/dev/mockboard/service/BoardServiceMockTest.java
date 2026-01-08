package dev.mockboard.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceMockTest {

//    @Mock private BoardRepository boardRepository;
//    @Mock private BoardCacheStore boardCacheStore;
//    @Mock private BoardMapper boardMapper;
//    @InjectMocks private BoardService boardService;

//    @Test
//    void createBoard_Success() {
//        var savedDoc = Board.builder()
//                .id("generated-id")
//                .apiKey("generated-api-key")
//                .ownerToken("generated-token")
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        var expectedDto = BoardDto.builder()
//                .id("generated-id")
//                .apiKey("generated-api-key")
//                .build();
//
//        when(boardRepository.save(any(Board.class))).thenReturn(savedDoc);
//        when(boardMapper.mapBoardDocToBoardDto(savedDoc)).thenReturn(expectedDto);
//
//        var result = boardService.createBoard();
//
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo("generated-id");
//        assertThat(result.getApiKey()).isEqualTo("generated-api-key");
//
//        verify(boardRepository, times(1)).save(any(Board.class));
//        verify(boardCacheStore, times(1)).initBoardCache("generated-id", expectedDto);
//    }
//
//    @Test
//    void getBoardDto_CacheHit() {
//        var boardId = "board-123";
//        var cachedDto = BoardDto.builder().id(boardId).build();
//
//        when(boardCacheStore.getBoardCache(boardId)).thenReturn(cachedDto);
//
//        var result = boardService.getBoardDto(boardId);
//        assertThat(result).isEqualTo(cachedDto);
//        verify(boardRepository, never()).findById(any());
//    }
//
//    @Test
//    void getBoardDto_CacheMiss() {
//        var boardId = "board-123";
//        var doc = Board.builder().id(boardId).build();
//        var dto = BoardDto.builder().id(boardId).build();
//
//        when(boardCacheStore.getBoardCache(boardId)).thenReturn(null);
//        when(boardRepository.findById(boardId)).thenReturn(Optional.of(doc));
//        when(boardMapper.mapBoardDocToBoardDto(doc)).thenReturn(dto);
//
//        var result = boardService.getBoardDto(boardId);
//
//        assertThat(result.getId()).isEqualTo(boardId);
//        verify(boardCacheStore).getBoardCache(boardId);
//        verify(boardRepository).findById(boardId);
//    }
//
//    @Test
//    void getBoardDto_NotFound() {
//        var boardId = "missing-id";
//
//        when(boardCacheStore.getBoardCache(boardId)).thenReturn(null);
//        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> boardService.getBoardDto(boardId))
//                .isInstanceOf(NotFoundException.class)
//                .hasMessageContaining("Board not found by id: missing-id");
//    }
}