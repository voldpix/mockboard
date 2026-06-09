package dev.mockboard.repository;

import dev.mockboard.repository.model.Board;
import dev.mockboard.repository.model.MockRule;
import org.mapdb.DB;
import org.mapdb.Serializer;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class BoardRepository {

    private static final Comparator<MockRule> MOCK_RULE_TIMESTAMP_DESC = Comparator
            .comparing(MockRule::getTimestamp, Comparator.nullsFirst(Comparator.naturalOrder()))
            .reversed();

    private final DB db;
    private final ConcurrentMap<String, Board> boards;

    public BoardRepository(DB db) {
        this.db = db;
        this.boards = openBoardMap(db);
    }

    @SuppressWarnings("unchecked")
    private ConcurrentMap<String, Board> openBoardMap(DB db) {
        return db
                .hashMap("boards", Serializer.STRING, Serializer.JAVA)
                .createOrOpen();
    }

    public synchronized Board save(Board board) {
        var copy = copyOf(board);
        boards.put(copy.getId(), copy);
        commit();
        return copyOf(copy);
    }

    public synchronized Optional<Board> findById(String id) {
        return Optional.ofNullable(boards.get(id)).map(this::copyOf);
    }

    public synchronized List<MockRule> findMockRulesByBoardIdOrderByTimestampDesc(String boardId) {
        return Optional.ofNullable(boards.get(boardId))
                .map(Board::getMockRules)
                .map(this::sortedRules)
                .orElseGet(List::of);
    }

    public synchronized Optional<Board> updateById(String boardId, UnaryOperator<Board> updater) {
        var persisted = boards.get(boardId);
        if (persisted == null) {
            return Optional.empty();
        }

        var updated = copyOf(updater.apply(copyOf(persisted)));
        boards.put(boardId, updated);
        commit();
        return Optional.of(copyOf(updated));
    }

    public synchronized void deleteById(String boardId) {
        boards.remove(boardId);
        commit();
    }

    private void commit() {
        db.commit();
    }

    private Board copyOf(Board board) {
        return Board.builder()
                .id(board.getId())
                .timestamp(board.getTimestamp())
                .mockRules(sortedRules(safeRules(board)))
                .build();
    }

    private List<MockRule> safeRules(Board board) {
        return board.getMockRules() == null ? List.of() : board.getMockRules();
    }

    private List<MockRule> sortedRules(List<MockRule> rules) {
        return rules.stream()
                .sorted(MOCK_RULE_TIMESTAMP_DESC)
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
