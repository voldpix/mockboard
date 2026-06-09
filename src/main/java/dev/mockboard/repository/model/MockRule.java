package dev.mockboard.repository.model;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "boardId", "timestamp"})
public class MockRule implements Serializable {

    private String id;
    private String boardId;
    private String method;
    private String path;
    private String headers;
    private String body;
    private int statusCode;
    private int delay; //ms
    private Instant timestamp;
}
