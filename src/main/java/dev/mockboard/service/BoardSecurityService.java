package dev.mockboard.service;

import dev.mockboard.common.domain.dto.BoardDto;
import dev.mockboard.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardSecurityService {

    private final BoardService boardService;

    public BoardDto validateOwnershipAndGet(String boardId, String requestOwnerToken) {
        var boardDto = boardService.getBoardDto(boardId);
        if (!boardDto.getOwnerToken().equals(requestOwnerToken)) {
            throw new UnauthorizedException("Invalid owner token");
        }

        return boardDto;
    }
}
