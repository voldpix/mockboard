package dev.mockboard.common.mapper;

import dev.mockboard.repository.model.Board;
import dev.mockboard.common.domain.dto.BoardDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardMapper {

    private final ModelMapper modelMapper;

    public BoardDto mapBoardDocToBoardDto(Board doc) {
        return modelMapper.map(doc, BoardDto.class);
    }
}
