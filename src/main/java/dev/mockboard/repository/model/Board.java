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
@EqualsAndHashCode(callSuper = false, of = {"id", "ownerToken"})
public class Board extends PersistableEntity<String> implements Serializable {

    private String id;
    private String ownerToken;
    private Instant timestamp;
    private boolean deleted;
}
