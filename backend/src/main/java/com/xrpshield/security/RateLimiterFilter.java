package com.xrpshield.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterFilter.class);
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStartTimes = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
                                    @org.springframework.lang.NonNull HttpServletResponse response,
                                    @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestUri = request.getRequestURI();

        // Enforce rate limiting on sensitive authentication endpoints
        if (requestUri.startsWith("/api/v1/auth/login") || requestUri.startsWith("/api/v1/wallet/verify")) {
            String clientIp = getClientIp(request);
            long currentTime = System.currentTimeMillis();

            windowStartTimes.putIfAbsent(clientIp, currentTime);
            long windowStart = windowStartTimes.get(clientIp);

            if (currentTime - windowStart > 60000) {
                windowStartTimes.put(clientIp, currentTime);
                requestCounts.put(clientIp, new AtomicInteger(1));
            } else {
                int count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0)).incrementAndGet();
                if (count > MAX_REQUESTS_PER_MINUTE) {
                    logger.warn("RATE_LIMIT_EXCEEDED | Client IP: {} | Request URI: {}", clientIp, requestUri);
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\":false,\"message\":\"Rate limit exceeded. Please try again later.\",\"timestamp\":\"" + java.time.Instant.now() + "\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
