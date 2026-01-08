package dev.mockboard.service;

import dev.mockboard.cache.BoardCache;
import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.common.utils.StringUtils;
import dev.mockboard.event.config.DomainEvent;
import dev.mockboard.event.config.EventQueue;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.model.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private static final int BOARD_ID_LENGTH = 16;
    private static final int API_KEY_LENGTH = 20;
    private static final int OWNER_TOKEN_LENGTH = 48;

    private final EventQueue eventQueue;
    private final BoardCache boardCache;
    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    public BoardDto createBoard() {
        var boardId = IdGenerator.generateId();
        var apiKey = StringUtils.generate(API_KEY_LENGTH);
        var ownerToken = StringUtils.generate(OWNER_TOKEN_LENGTH);

        var board = Board.builder()
                .id(boardId)
                .apiKey(apiKey)
                .ownerToken(ownerToken)
                .timestamp(Instant.now())
                .build();
        var boardDto = modelMapper.map(board, BoardDto.class);
        boardCache.put(board.getId(), boardDto);

        eventQueue.publish(DomainEvent.create(board, Board.class));
        log.info("Created board: {}", board.getId());
        return boardDto;
    }

    public BoardDto getBoardDto(String boardId) {
        var cachedOpt = boardCache.get(boardId);
        if (cachedOpt.isPresent()) {
            log.debug("Board cache hit: {}", boardId);
            return cachedOpt.get();
        }

        log.debug("Board cache miss: {}, fallback to DB", boardId);
        var boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardId);
        }

        var boardDto = modelMapper.map(boardOpt.get(), BoardDto.class);
        boardCache.put(boardDto.getId(), boardDto);
        return boardDto;
    }
}
