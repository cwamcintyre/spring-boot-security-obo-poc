package com.example.web;

import com.microsoft.aad.msal4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization/azure")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
            );
        return http.build();
    }

    @Bean
    public OboTokenAcquirer oboTokenAcquirer(
            @Value("${api.msTenantId}") String tenantId,
            @Value("${spring.security.oauth2.client.registration.azure.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.azure.client-secret}") String clientSecret,
            @Value("${api.msAuthority}") String authority,
            @Value("${api.oboApiScope}") String apiScope,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        Logger logger = LoggerFactory.getLogger("OboTokenAcquirer");
        return user -> {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("azure", user.getName());

            if (client == null || client.getAccessToken() == null) {
                logger.warn("No access token available for user");
                return null;
            }

            try {
                ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                        clientId,
                        ClientCredentialFactory.createFromSecret(clientSecret))
                        .authority(authority)
                        .build();
                OnBehalfOfParameters oboParameters = OnBehalfOfParameters.builder(
                        Collections.singleton(apiScope),
                        new UserAssertion(client.getAccessToken().getTokenValue()))
                        .build();
                IAuthenticationResult oboResult = app.acquireToken(oboParameters).join();
                logger.info("Obtained OBO access token: " + oboResult.accessToken());
                return oboResult.accessToken();
            } catch (Exception e) {
                logger.error("Failed to acquire OBO token", e);
                return null;
            }
        };
    }
}
