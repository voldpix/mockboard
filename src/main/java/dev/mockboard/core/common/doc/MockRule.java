package dev.mockboard.core.common.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockRule implements Serializable {

    private String id;

    private String path;

    private Map<String, String> headers;

    private String body;

    private int statusCode;

    private LocalDateTime createdAt;

    // inner state
    private boolean shouldApplyFaker;
}
