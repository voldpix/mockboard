package dev.mockboard.web;

import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public final class HttpResponses {

    private HttpResponses() {
    }

    public static void applyHeaders(Context ctx, Map<String, List<String>> headers) {
        headers.forEach((name, values) -> values.forEach(value -> ctx.header(name, value)));
    }
}
