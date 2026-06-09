package dev.mockboard.repository;

import dev.mockboard.repository.model.Board;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;

import java.util.Optional;

@Slf4j
public class BoardRepository extends MapDbRepository<Board> {

    public BoardRepository(DB db) {
        super(db, "boards");
    }

    public synchronized Board save(Board board) {
        map.put(board.getId(), board);
        commit();
        return board;
    }

    public Optional<Board> findById(String id) {
        return Optional.ofNullable(map.get(id));
    }

    public synchronized void deleteById(String boardId) {
        map.remove(boardId);
        commit();
    }
}
