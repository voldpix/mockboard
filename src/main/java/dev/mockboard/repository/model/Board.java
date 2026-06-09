package dev.mockboard.repository.model;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board implements Serializable {

    private String id;
    private Instant timestamp;
}
