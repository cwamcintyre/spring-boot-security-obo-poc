package com.example.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("dev-tokens")
public class DevTokenConfig {

    private static final Map<String, List<String>> DEV_TOKENS = Map.of(
            "swagger-writer-token",
            List.of("ROLE_API.Writer"),

            "swagger-reader-token",
            List.of("ROLE_API.Reader")
    );

    private boolean devTokensAllowed() {

        boolean runningInK8s =
                System.getenv("KUBERNETES_SERVICE_HOST") != null;

        if (!runningInK8s) {
            // Local / CI / integration tests
            return true;
        }

        // Running inside Kubernetes → enforce namespace check
        String namespace = System.getenv("K8S_NAMESPACE");

        // this should check a secret..
        return "dev".equals(namespace);
    }

    @Bean
    public OncePerRequestFilter devTokenFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {

                if (!devTokensAllowed()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 2️⃣ Extract bearer token
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String token = authHeader.substring(7);

                // 3️⃣ Only allow explicitly configured tokens
                if (!DEV_TOKENS.containsKey(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4️⃣ Inject role-based Authentication
                List<SimpleGrantedAuthority> authorities =
                        DEV_TOKENS.get(token).stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                "dev-user",
                                null,
                                authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
            }
        };
    }
}
