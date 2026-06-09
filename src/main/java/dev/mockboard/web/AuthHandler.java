package dev.mockboard.web;

import dev.mockboard.Constants;
import dev.mockboard.service.AppSecurityService;
import io.javalin.http.Context;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthHandler {

    private final AppSecurityService appSecurityService;

    public void requireAppToken(Context ctx) {
        appSecurityService.validateToken(ctx.header(Constants.APP_TOKEN_HEADER_KEY));
    }
}
