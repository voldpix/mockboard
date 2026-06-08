package dev.mockboard.repository;

import dev.mockboard.repository.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Repository
public class BoardRepository {

    private final DB db;
    private final ConcurrentMap<String, Board> boards;

    public BoardRepository(DB db) {
        this.db = db;
        this.boards = db
                .hashMap("boards", Serializer.STRING, Serializer.JAVA)
                .createOrOpen();
    }

    public synchronized Board save(Board board) {
        board.markNotNew();
        boards.put(board.getId(), board);
        db.commit();
        return board;
    }

    public Optional<Board> findByIdAndDeletedFalse(String id) {
        return Optional.ofNullable(boards.get(id))
                .filter(board -> !board.isDeleted());
    }

    public synchronized void markDeleted(String boardId) {
        var board = boards.get(boardId);
        if (board == null) {
            return;
        }

        board.setDeleted(true);
        boards.put(boardId, board);
        db.commit();
    }

    public synchronized int hardDeleteMarkedBoards() {
        var deletedIds = new ArrayList<String>();
        boards.forEach((id, board) -> {
            if (board.isDeleted()) {
                deletedIds.add(id);
            }
        });
        deletedIds.forEach(boards::remove);
        if (!deletedIds.isEmpty()) {
            db.commit();
        }
        return deletedIds.size();
    }
}
