package com.example.web.service;

import com.example.web.OboTokenAcquirer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ApiService {
    private final WebClient webClient;

    private final String apiBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final OboTokenAcquirer oboTokenAcquirer;

    public ApiService(WebClient.Builder webClientBuilder, @Value("${api.base-url}") String apiBaseUrl, OboTokenAcquirer oboTokenAcquirer) {
        this.webClient = webClientBuilder.build();
        this.apiBaseUrl = apiBaseUrl;
        this.oboTokenAcquirer = oboTokenAcquirer;
    }

    public String getGreeting(OidcUser user) throws MalformedURLException {
        String oboToken = oboTokenAcquirer.acquireOboToken(user);
        return webClient.get()
                .uri(apiBaseUrl + "/api/hello")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oboToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getRoles(OidcUser user) throws MalformedURLException {
        String oboToken = oboTokenAcquirer.acquireOboToken(user);
        return webClient.get()
                .uri(apiBaseUrl + "/api/roles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oboToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getOnlyWriters(OidcUser user) throws MalformedURLException {
        String oboToken = oboTokenAcquirer.acquireOboToken(user);
        return webClient.get()
                .uri(apiBaseUrl + "/api/onlyWriters")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oboToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getOnlySuperusers(OidcUser user) throws MalformedURLException {
        String oboToken = oboTokenAcquirer.acquireOboToken(user);
        return webClient.get()
                .uri(apiBaseUrl + "/api/onlySuperusers")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oboToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getClaims(OidcUser user) throws MalformedURLException {
        String oboToken = oboTokenAcquirer.acquireOboToken(user);
        return webClient.get()
                .uri(apiBaseUrl + "/api/claims")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + oboToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}