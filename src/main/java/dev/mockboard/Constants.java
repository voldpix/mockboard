package dev.mockboard;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    // events
    public static final int MAX_EVENT_CONSUMER_DRAIN_ELEMS = 200;

    // scheduler
    public static final int CREATED_EVENTS_PROCESS_DELAY = 5_000;
    public static final int UPDATED_EVENTS_PROCESS_DELAY = 10_000;
    public static final int DELETED_EVENTS_PROCESS_DELAY = 30_000;

    // cache
    public static final int DEFAULT_CACHE_MAX_ENTRIES = 10_000;
    public static final int DEFAULT_CACHE_EXP_AFTER_ACCESS_MIN = 15;

    // headers
    public static final String OWNER_TOKEN_HEADER_KEY = "X-Owner-Token";

}
