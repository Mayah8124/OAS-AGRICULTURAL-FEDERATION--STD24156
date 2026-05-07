package org.ingredients.agriculturalfederation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiKeySecurityConfig implements WebMvcConfigurer {

    private final String expectedApiKey;

    public ApiKeySecurityConfig(@Value("${API_KEY:agri-secure-key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiKeyInterceptor(expectedApiKey)).addPathPatterns("/**");
    }
}
