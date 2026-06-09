package dev.mockboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mockboard.Constants;
import dev.mockboard.common.domain.dto.MockRuleDto;
import dev.mockboard.common.exception.BadRequestException;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.validator.MockRuleValidator;
import dev.mockboard.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MockRuleServiceTest {

    @Test
    void concurrentCreatesDoNotExceedBoardLimit() throws Exception {
        try (var fixture = fixture();
             var executor = Executors.newFixedThreadPool(8)) {
            var board = fixture.boardService().createBoard();
            var start = new CountDownLatch(1);
            var attempts = Constants.MAX_MOCK_RULES + 24;

            var futures = IntStream.range(0, attempts)
                    .mapToObj(i -> executor.submit(() -> {
                        start.await();
                        try {
                            fixture.mockRuleService().createMockRule(board, mockRule(i));
                            return true;
                        } catch (BadRequestException ignored) {
                            return false;
                        }
                    }))
                    .toList();

            start.countDown();

            var successfulCreates = 0;
            for (var future : futures) {
                if (future.get()) {
                    successfulCreates++;
                }
            }

            assertThat(successfulCreates).isEqualTo(Constants.MAX_MOCK_RULES);
            assertThat(fixture.mockRuleService().getMockRules(board)).hasSize(Constants.MAX_MOCK_RULES);
        }
    }

    @Test
    void deletingBoardRemovesNestedMockRules() {
        try (var fixture = fixture()) {
            var board = fixture.boardService().createBoard();
            fixture.mockRuleService().createMockRule(board, mockRule(1));

            fixture.boardService().deleteBoard(board);

            assertThat(fixture.mockRuleService().getMockRules(board)).isEmpty();
            assertThatThrownBy(() -> fixture.boardService().getBoardDto(board.getId()))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    private TestFixture fixture() {
        var db = DBMaker.memoryDB()
                .transactionEnable()
                .make();
        var boardRepository = new BoardRepository(db);
        var boardService = new BoardService(boardRepository);
        var mockRuleService = new MockRuleService(new MockRuleValidator(new ObjectMapper()), boardRepository);
        return new TestFixture(db, boardService, mockRuleService);
    }

    private MockRuleDto mockRule(int index) {
        return MockRuleDto.builder()
                .method("GET")
                .path("/api/test-" + index)
                .headers(null)
                .body("{}")
                .statusCode(200)
                .delay(0)
                .build();
    }

    private record TestFixture(DB db,
                               BoardService boardService,
                               MockRuleService mockRuleService) implements AutoCloseable {

        @Override
        public void close() {
            db.close();
        }
    }
}
