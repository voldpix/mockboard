package dev.mockboard.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board implements Serializable {

    private Long id;

    private String boardId;

    private String apiKey;

    private String ownerToken;

    private Instant timestamp;
}
