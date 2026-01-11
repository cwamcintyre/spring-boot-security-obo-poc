package com.example.web;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.net.MalformedURLException;

@FunctionalInterface
public interface OboTokenAcquirer {
    String acquireOboToken(OidcUser user) throws MalformedURLException;
}

