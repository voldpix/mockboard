package dev.mockboard.service;

import dev.mockboard.Constants;
import dev.mockboard.common.exception.UnauthorizedException;
import dev.mockboard.common.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppSecurityService {

    @Getter
    private final String appToken = StringUtils.generate(Constants.APP_TOKEN_LENGTH);

    public AppSecurityService() {
        log.info("Generated local app token for this runtime");
    }

    public void validateToken(String requestToken) {
        if (!appToken.equals(requestToken)) {
            throw new UnauthorizedException("Invalid app token");
        }
    }

    public void validateAnyToken(String... requestTokens) {
        for (var requestToken : requestTokens) {
            if (requestToken == null || requestToken.isBlank()) {
                continue;
            }

            if (appToken.equals(requestToken)) {
                return;
            }
        }

        throw new UnauthorizedException("Invalid app token");
    }
}
