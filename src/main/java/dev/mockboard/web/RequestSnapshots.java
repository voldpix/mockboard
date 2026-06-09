package dev.mockboard.web;

import dev.mockboard.common.domain.RequestSnapshot;
import io.javalin.http.Context;

public final class RequestSnapshots {

    private RequestSnapshots() {
    }

    public static RequestSnapshot from(Context ctx) {
        return new RequestSnapshot(
                ctx.method().name(),
                ctx.path(),
                ctx.fullUrl(),
                ctx.queryString(),
                ctx.headerMap(),
                ctx.body(),
                ctx.contentType(),
                ctx.contentLength()
        );
    }
}
