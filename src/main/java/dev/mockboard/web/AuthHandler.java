package dev.mockboard.web;

import dev.mockboard.Constants;
import dev.mockboard.service.AppSecurityService;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthHandler {

    private final AppSecurityService appSecurityService;

    public void requireAppToken(Context ctx) {
        if (ctx.path().endsWith("/stream")) {
            appSecurityService.validateAnyToken(ctx.header(Constants.APP_TOKEN_HEADER_KEY), ctx.queryParam("token"));
            return;
        }

        appSecurityService.validateToken(ctx.header(Constants.APP_TOKEN_HEADER_KEY));
    }
}
