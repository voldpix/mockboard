package dev.mockboard.common.utils;

import de.huxhorn.sulky.ulid.ULID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdGenerator {

    private static final ULID ULID = new ULID();

    public static String generateId() {
        return ULID.nextULID().toLowerCase();
    }
}
