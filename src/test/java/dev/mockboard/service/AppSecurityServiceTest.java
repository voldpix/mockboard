package dev.mockboard.service;

import dev.mockboard.common.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AppSecurityServiceTest {

    @Test
    void validateAnyTokenAcceptsHeaderToken() {
        var service = new AppSecurityService();

        assertThatCode(() -> service.validateAnyToken(service.getAppToken(), "ignored"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAnyTokenAcceptsQueryFallbackToken() {
        var service = new AppSecurityService();

        assertThatCode(() -> service.validateAnyToken(null, service.getAppToken()))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAnyTokenRejectsMissingToken() {
        var service = new AppSecurityService();

        assertThatThrownBy(() -> service.validateAnyToken(null, " "))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid app token");
    }

    @Test
    void validateAnyTokenAcceptsAnyValidToken() {
        var service = new AppSecurityService();

        assertThatCode(() -> service.validateAnyToken("invalid", service.getAppToken()))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAnyTokenRejectsInvalidToken() {
        var service = new AppSecurityService();

        assertThatThrownBy(() -> service.validateAnyToken("invalid"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid app token");
    }
}
