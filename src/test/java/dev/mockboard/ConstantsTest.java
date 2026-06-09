package dev.mockboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantsTest {

    @Test
    void localFirstLimitsAreRelaxed() {
        assertThat(Constants.MAX_MOCK_RULES).isEqualTo(1_000);
        assertThat(Constants.MAX_WEBHOOKS).isEqualTo(100);
        assertThat(Constants.MAX_BOARD_NAME_LENGTH).isEqualTo(80);
        assertThat(Constants.MAX_PATH_LENGTH).isEqualTo(1_000);
        assertThat(Constants.MAX_BODY_LENGTH).isEqualTo(100_000);
        assertThat(Constants.MAX_HEADERS_SIZE).isEqualTo(50);
        assertThat(Constants.MAX_WILDCARDS).isEqualTo(10);
    }
}
