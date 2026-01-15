package dev.mockboard.filter;

import dev.mockboard.Constants;
import dev.mockboard.cache.RateLimiterCache;
import dev.mockboard.common.utils.RequestUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimiterCache rateLimiterCache;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        var ip = RequestUtils.getClientIp(request);
        var path = request.getRequestURI();
        var method = request.getMethod();

        var allowed = true;
        if (path.startsWith("/api/boards") && "POST".equals(method)) {
            allowed = rateLimiterCache.allowBoardCreation(ip);
        } else if (path.startsWith("/m")) {
            allowed = rateLimiterCache.allowMockExecution(ip);
        }

        if (allowed) {
            allowed = rateLimiterCache.allowOtherRequests(ip);
        }

        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(Constants.DEFAULT_RATE_LIMIT_ERROR_RESPONSE);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
