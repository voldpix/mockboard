package dev.mockboard.web;

import dev.mockboard.common.domain.ExceptionResponse;
import dev.mockboard.common.exception.BadRequestException;
import dev.mockboard.common.exception.NotFoundException;
import dev.mockboard.common.exception.UnauthorizedException;
import io.javalin.config.RoutesConfig;
import io.javalin.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public final class ErrorHandlers {

    private ErrorHandlers() {
    }

    public static void register(RoutesConfig routes) {
        routes.exception(NotFoundException.class, (ex, ctx) -> error(ctx, HttpStatus.NOT_FOUND, ex.getMessage()));
        routes.exception(BadRequestException.class, (ex, ctx) -> error(ctx, HttpStatus.BAD_REQUEST, ex.getMessage()));
        routes.exception(UnauthorizedException.class, (ex, ctx) -> error(ctx, HttpStatus.UNAUTHORIZED, ex.getMessage()));
        routes.exception(IllegalArgumentException.class, (ex, ctx) -> error(ctx, HttpStatus.BAD_REQUEST, ex.getMessage()));
        routes.exception(Exception.class, (ex, ctx) -> {
            log.error("Unhandled request failure", ex);
            error(ctx, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        });
    }

    private static void error(io.javalin.http.Context ctx, HttpStatus status, String message) {
        ctx.status(status).json(new ExceptionResponse(message, LocalDateTime.now()));
    }
}
