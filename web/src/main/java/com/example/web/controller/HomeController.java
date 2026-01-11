package com.example.web.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.web.service.ApiService;

import java.net.MalformedURLException;

@Controller
public class HomeController {

    @Autowired
    private ApiService apiService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OidcUser oidcUser, Authentication authentication) throws MalformedURLException {
        model.addAttribute("message", "Hello from Web App");
        if (oidcUser != null) {
            model.addAttribute("userName", oidcUser.getFullName());
            model.addAttribute("userEmail", oidcUser.getEmail());

            String apiGreeting = apiService.getGreeting(oidcUser);

            model.addAttribute("apiGreeting", apiGreeting);
        }
        return "index";
    }

    @PostMapping("/claims")
    public String showClaims(Model model, @AuthenticationPrincipal OidcUser oidcUser, Authentication authentication) throws MalformedURLException {
        model.addAttribute("message", "Hello from Web App");
        if (oidcUser != null) {
            model.addAttribute("userName", oidcUser.getFullName());
            model.addAttribute("userEmail", oidcUser.getEmail());
            String apiGreeting = apiService.getGreeting(oidcUser);
            model.addAttribute("apiGreeting", apiGreeting);
            // Call API to get claims
            String claims = apiService.getRoles(oidcUser);
            model.addAttribute("claims", claims);
        }
        return "index";
    }

    @PostMapping("/onlyWriters")
    public String showWriterResult(Model model, @AuthenticationPrincipal OidcUser oidcUser, Authentication authentication) throws MalformedURLException {
        model.addAttribute("message", "Hello from Web App");
        if (oidcUser != null) {
            model.addAttribute("userName", oidcUser.getFullName());
            model.addAttribute("userEmail", oidcUser.getEmail());
            String apiGreeting = apiService.getGreeting(oidcUser);
            model.addAttribute("apiGreeting", apiGreeting);
            String writerResult = apiService.getOnlyWriters(oidcUser);
            model.addAttribute("writerResult", writerResult);
        }
        return "index";
    }

    @PostMapping("/onlySuperusers")
    public String showSuperuserResult(Model model, @AuthenticationPrincipal OidcUser oidcUser, Authentication authentication) throws MalformedURLException {
        model.addAttribute("message", "Hello from Web App");
        if (oidcUser != null) {
            model.addAttribute("userName", oidcUser.getFullName());
            model.addAttribute("userEmail", oidcUser.getEmail());
            String apiGreeting = apiService.getGreeting(oidcUser);
            model.addAttribute("apiGreeting", apiGreeting);
            String superuserResult = apiService.getOnlySuperusers(oidcUser);
            model.addAttribute("superuserResult", superuserResult);
        }
        return "index";
    }
}
