package com.example.api.controller;

import com.example.api.security.ApiSuperuserOnly;
import com.example.api.security.ApiWriterOnly;
import com.example.common.dto.GreetingDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloApiController {

    @GetMapping("/api/hello")
    public GreetingDto hello() {
        return new GreetingDto("Hello from API");
    }

    @GetMapping("/api/onlyWriters")
    @ApiWriterOnly
    public @ResponseBody String onlyWriters() {
        return "Hello Writers!";
    }

    @GetMapping("/api/onlySuperusers")
    @ApiSuperuserOnly
    public @ResponseBody String onlySuperusers() {
        return "Hello Superusers!";
    }

    @GetMapping("/api/roles")
    @ResponseBody
    public Map<String, Object> claims(@AuthenticationPrincipal Jwt jwt) {
        // Only return the 'roles' claim if present
        Object roles = jwt.getClaims().get("roles");
        return Map.of("roles", roles);
    }
}
