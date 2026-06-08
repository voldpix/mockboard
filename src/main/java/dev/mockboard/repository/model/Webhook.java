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
public class Webhook implements Serializable {

    private String id;
    private String boardId;
    private String method;
    private String path;
    private String fullUrl;
    private String queryParams;
    private String headers;
    private String body;
    private String contentType;
    private Integer statusCode;

    // metadata
    private boolean matched;
    private Instant timestamp;
    private long processingTimeMs;
}
