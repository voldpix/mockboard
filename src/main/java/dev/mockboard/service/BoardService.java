package dev.mockboard.service;

import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.utils.IdGenerator;
import dev.mockboard.common.utils.StringUtils;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.repository.MockRuleRepository;
import dev.mockboard.repository.model.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static dev.mockboard.Constants.BOARD_OWNER_TOKEN_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final MockRuleRepository mockRuleRepository;

    public BoardDto createBoard() {
        var boardId = IdGenerator.generateBoardId();
        var ownerToken = StringUtils.generate(BOARD_OWNER_TOKEN_LENGTH);

        var board = Board.builder()
                .id(boardId)
                .ownerToken(ownerToken)
                .timestamp(Instant.now())
                .build();
        var persisted = boardRepository.save(board);

        var boardDto = modelMapper.map(persisted, BoardDto.class);

        log.info("Created board: {}", persisted.getId());
        return boardDto;
    }

    public BoardDto getBoardDto(String boardId) {
        var boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isEmpty()) {
            throw new NotFoundException("Board not found by id: " + boardId);
        }

        return modelMapper.map(boardOpt.get(), BoardDto.class);
    }

    public void deleteBoard(BoardDto boardDto) {
        log.info("Delete board: {}", boardDto.getId());
        mockRuleRepository.deleteByBoardId(boardDto.getId());
        boardRepository.deleteById(boardDto.getId());
    }
}
