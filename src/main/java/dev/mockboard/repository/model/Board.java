package dev.mockboard.repository.model;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Board implements Serializable {

    private static final long serialVersionUID = 7751465435236789856L;

    private String id;
    private String name;
    private Instant timestamp;
    @Builder.Default
    private List<MockRule> mockRules = new LinkedList<>();
}
