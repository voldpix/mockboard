package dev.mockboard.repository.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = {"id", "boardId", "timestamp"})
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
