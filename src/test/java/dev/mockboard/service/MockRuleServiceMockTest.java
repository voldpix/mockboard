package dev.mockboard.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockRuleServiceMockTest {

//    @Mock private BoardService boardService;
//    @Mock private MockRuleValidator mockRuleValidator;
//    @Mock private MockRuleRepository mockRuleRepository;
//    @Mock private MockRuleMapper mockRuleMapper;
//    @Mock private MockRuleCacheStore mockRuleCacheStore;
//    @Mock private MockExecutionCacheStore mockExecutionCacheStore;
//    @InjectMocks private MockRuleService mockRuleService;
//
//    private final BoardDto boardDto = BoardDto.builder().id("board-1").apiKey("api-key-1").build();
//    private final MockRuleDto mockRuleDto = MockRuleDto.builder().path("/test").method("GET").statusCode(200).build();
//    private final MockRule mockRule = MockRule.builder().id("mock-1").boardId("board-1").apiKey("api-key-1").build();
//
//    @Test
//    void addMockRule_Success() {
//        when(mockRuleMapper.mapMockRuleDtoToMockRuleDoc(mockRuleDto)).thenReturn(mockRule);
//        when(mockRuleRepository.save(mockRule)).thenReturn(mockRule);
//
//        var response = mockRuleService.addMockRule(boardDto, mockRuleDto);
//        assertThat(response.id()).isEqualTo("mock-1");
//
//        verify(mockRuleValidator).validateMockRule(mockRuleDto);
//        verify(mockRuleRepository).save(mockRule);
//
//        verify(mockRuleCacheStore).evict("api-key-1");
//        verify(mockExecutionCacheStore).evict("api-key-1");
//    }
//
//    @Test
//    void updateMockRule_Success() {
//        var mockId = "mock-1";
//        var updateDto = MockRuleDto.builder()
//                .method("POST")
//                .path("/updated")
//                .statusCode(201)
//                .body("{}")
//                .build();
//
//        when(mockRuleRepository.findByIdAndBoardId(mockId, boardDto.getId()))
//                .thenReturn(Optional.of(mockRule));
//
//        var response = mockRuleService.updateMockRule(boardDto, mockId, updateDto);
//        assertThat(response.id()).isEqualTo(mockId);
//        assertThat(mockRule.getMethod()).isEqualTo("POST");
//        assertThat(mockRule.getPath()).isEqualTo("/updated");
//        assertThat(mockRule.getStatusCode()).isEqualTo(201);
//
//        verify(mockRuleValidator).validateMockRule(updateDto);
//        verify(mockRuleRepository).save(mockRule);
//
//        verify(mockRuleCacheStore).evict("api-key-1");
//        verify(mockExecutionCacheStore).evict("api-key-1");
//    }
//
//    @Test
//    void updateMockRule_NotFound() {
//        when(mockRuleRepository.findByIdAndBoardId("missing-id", boardDto.getId()))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> mockRuleService.updateMockRule(boardDto, "missing-id", mockRuleDto))
//                .isInstanceOf(NotFoundException.class);
//        verify(mockRuleRepository, never()).save(any());
//        verify(mockRuleCacheStore, never()).evict(any());
//    }
//
//    @Test
//    void getMockRuleDtos_CacheHit() {
//        when(mockRuleCacheStore.getMockRules(boardDto.getApiKey()))
//                .thenReturn(List.of(mockRuleDto));
//
//        var result = mockRuleService.getMockRuleDtos(boardDto);
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst()).isEqualTo(mockRuleDto);
//        verify(mockRuleRepository, never()).findByBoardId(any());
//    }
//
//    @Test
//    void getMockRuleDtos_CacheMiss() {
//        when(mockRuleCacheStore.getMockRules(boardDto.getApiKey())).thenReturn(null);
//        when(mockRuleRepository.findByBoardId(boardDto.getId())).thenReturn(List.of(mockRule));
//        when(mockRuleMapper.mapMockRuleDocToMockRuleDto(mockRule)).thenReturn(mockRuleDto);
//
//        var result = mockRuleService.getMockRuleDtos(boardDto);
//        assertThat(result).hasSize(1);
//        verify(mockRuleRepository).findByBoardId(boardDto.getId());
//        verify(mockRuleCacheStore).initMockRulesCache(eq(boardDto.getApiKey()), anyList());
//    }
//
//    @Test
//    void deleteMockRule_Success() {
//        when(mockRuleRepository.findByIdAndBoardId("mock-1", boardDto.getId()))
//                .thenReturn(Optional.of(mockRule));
//        mockRuleService.deleteMockRule(boardDto, "mock-1");
//
//        verify(mockRuleRepository).delete(mockRule);
//        verify(mockRuleCacheStore).evict("api-key-1");
//        verify(mockExecutionCacheStore).evict("api-key-1");
//    }
//
//    @Test
//    void deleteMockRule_NotFound() {
//        when(mockRuleRepository.findByIdAndBoardId("missing", boardDto.getId()))
//                .thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> mockRuleService.deleteMockRule(boardDto, "missing"))
//                .isInstanceOf(NotFoundException.class);
//        verify(mockRuleRepository, never()).delete(any());
//    }
}