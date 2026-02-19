package com.example.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

import java.util.Set;

@Configuration
@Profile("dev-tokens")
public class DevBearerTokenResolverConfig {

    private static final Set<String> DEV_TOKENS = Set.of(
            "swagger-writer-token",
            "swagger-reader-token"
    );

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> {

            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.substring(7);

            // If it is a dev token, do NOT let JWT filter see it
            if (DEV_TOKENS.contains(token)) {
                return null;
            }

            // Otherwise return it normally (real JWT)
            return token;
        };
    }
}

