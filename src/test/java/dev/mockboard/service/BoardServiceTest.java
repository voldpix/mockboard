package dev.mockboard.service;

import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.model.Board;
import dev.mockboard.repository.model.MockRule;
import org.junit.jupiter.api.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardServiceTest {

    @Test
    void getBoardsReturnsNewestFirstWithMockRuleCount() {
        try (var fixture = fixture()) {
            fixture.repository().save(board("older", Instant.parse("2026-01-01T00:00:00Z"), 1));
            fixture.repository().save(board("newer", Instant.parse("2026-01-02T00:00:00Z"), 2));

            var boards = fixture.service().getBoards();

            assertThat(boards).extracting("id").containsExactly("newer", "older");
            assertThat(boards).extracting("name").containsExactly("Newer board", "Older board");
            assertThat(boards.getFirst().getMockRuleCount()).isEqualTo(2);
            assertThat(boards.get(1).getMockRuleCount()).isEqualTo(1);
        }
    }

    @Test
    void updateBoardNameStoresTrimmedDisplayName() {
        try (var fixture = fixture()) {
            fixture.repository().save(board("board-123", Instant.parse("2026-01-01T00:00:00Z"), 0));

            var updated = fixture.service().updateBoardName("board-123", "  Checkout mocks  ");

            assertThat(updated.getName()).isEqualTo("Checkout mocks");
            assertThat(fixture.service().getBoardDto("board-123").getName()).isEqualTo("Checkout mocks");
        }
    }

    @Test
    void updateBoardNameClearsBlankDisplayName() {
        try (var fixture = fixture()) {
            fixture.repository().save(Board.builder()
                    .id("board-123")
                    .name("Existing")
                    .timestamp(Instant.parse("2026-01-01T00:00:00Z"))
                    .build());

            var updated = fixture.service().updateBoardName("board-123", " ");

            assertThat(updated.getName()).isNull();
        }
    }

    @Test
    void updateBoardNameRejectsOversizedName() {
        try (var fixture = fixture()) {
            fixture.repository().save(board("board-123", Instant.parse("2026-01-01T00:00:00Z"), 0));

            assertThatThrownBy(() -> fixture.service().updateBoardName("board-123", "x".repeat(81)))
                    .hasMessageContaining("Board name exceeds maximum length");
        }
    }

    private Board board(String boardId, Instant timestamp, int mockRuleCount) {
        return Board.builder()
                .id(boardId)
                .name(switch (boardId) {
                    case "older" -> "Older board";
                    case "newer" -> "Newer board";
                    default -> null;
                })
                .timestamp(timestamp)
                .mockRules(mockRules(boardId, mockRuleCount))
                .build();
    }

    private List<MockRule> mockRules(String boardId, int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> MockRule.builder()
                        .id("mock-" + index)
                        .boardId(boardId)
                        .timestamp(Instant.parse("2026-01-01T00:00:00Z").plusSeconds(index))
                        .build())
                .toList();
    }

    private TestFixture fixture() {
        var db = DBMaker.memoryDB()
                .transactionEnable()
                .make();
        var repository = new BoardRepository(db);
        var service = new BoardService(repository);
        return new TestFixture(db, repository, service);
    }

    private record TestFixture(DB db,
                               BoardRepository repository,
                               BoardService service) implements AutoCloseable {

        @Override
        public void close() {
            db.close();
        }
    }
}
