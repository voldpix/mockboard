package dev.mockboard.service;

import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.MockRuleRepository;
import dev.mockboard.repository.model.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MockRuleRepository mockRuleRepository;

    public BoardDto createBoard() {
        var boardId = IdGenerator.generateBoardId();

        var board = Board.builder()
                .id(boardId)
                .timestamp(Instant.now())
                .build();
        var persisted = boardRepository.save(board);

        log.info("Created board: {}", persisted.getId());
        return toDto(persisted);
    }

    public BoardDto getBoardDto(String boardId) {
        var boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardId);
        }

        return toDto(boardOpt.get());
    }

    public void deleteBoard(BoardDto boardDto) {
        log.info("Delete board: {}", boardDto.getId());
        mockRuleRepository.deleteByBoardId(boardDto.getId());
        boardRepository.deleteById(boardDto.getId());
    }

    private BoardDto toDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .timestamp(board.getTimestamp())
                .build();
    }
}
