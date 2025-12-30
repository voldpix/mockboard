package dev.mockboard.core.common.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCacheObj implements Serializable {

    private String id;
    private String boardId;
    private String apiKey;
    private String ownerToken;
    private LocalDateTime createdAt;
}
