package dev.mockboard.core.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private String id;
    private String apiKey;
    private String ownerToken;
    private LocalDateTime createdAt;
}
