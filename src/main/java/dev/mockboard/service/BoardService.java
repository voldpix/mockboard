package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.exception.BadRequestException;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.model.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

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

    public List<BoardDto> getBoards() {
        return boardRepository.findAllOrderByTimestampDesc().stream()
                .map(this::toDto)
                .toList();
    }

    public BoardDto getBoardDto(String boardId) {
        var boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardId);
        }

        return toDto(boardOpt.get());
    }

    public BoardDto updateBoardName(String boardId, String name) {
        var normalizedName = normalizeName(name);
        var updated = boardRepository.updateById(boardId, board -> {
            board.setName(normalizedName);
            return board;
        });
        if (updated.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardId);
        }

        log.info("Updated board display name: {}", boardId);
        return toDto(updated.get());
    }

    public void deleteBoard(BoardDto boardDto) {
        log.info("Delete board: {}", boardDto.getId());
        boardRepository.deleteById(boardDto.getId());
    }

    private BoardDto toDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .name(board.getName())
                .timestamp(board.getTimestamp())
                .mockRuleCount(board.getMockRules() == null ? 0 : board.getMockRules().size())
                .build();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        var normalized = name.trim();
        if (normalized.length() > Constants.MAX_BOARD_NAME_LENGTH) {
            throw new BadRequestException("Board name exceeds maximum length of " + Constants.MAX_BOARD_NAME_LENGTH);
        }
        return normalized;
    }
}
