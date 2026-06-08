package dev.mockboard.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PersistableEntity<ID> implements Serializable {

    @Builder.Default
    private boolean isNew = true;

    public void markNotNew() {
        this.isNew = false;
    }
}
