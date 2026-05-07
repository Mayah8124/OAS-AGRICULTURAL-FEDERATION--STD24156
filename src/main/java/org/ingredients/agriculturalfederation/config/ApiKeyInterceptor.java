package org.ingredients.agriculturalfederation.config;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyInterceptor implements HandlerInterceptor {

    private final String expectedApiKey;

    public ApiKeyInterceptor(String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String provided = request.getHeader("x-api-key");
        if (provided == null || provided.isBlank()) {
            throw new SecurityException("Missing API key");
        }
        if (!provided.equals(expectedApiKey)) {
            throw new SecurityException("Invalid API key");
        }
        return true;
    }
}
