package com.instagram.demo.controller.auth;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling authentication-related endpoints.
 * Provides endpoints for user registration and login.
 */
@RestController
@RequestMapping(path = "/", produces = "application/json")
@AllArgsConstructor
public class AuthController {
    private final Gson gson;

    /**
     * Endpoint for user login.
     * Retrieves the username of the authenticated user and responds with it.
     *
     * @param authentication Authentication object containing details of the authenticated user.
     * @return ResponseEntity containing the username of the authenticated user.
     */
    @GetMapping("login")
    ResponseEntity<String> login(Authentication authentication) {
        return new ResponseEntity<>(gson.toJson(authentication.getName()), HttpStatus.OK);
    }

    @GetMapping("test")
    ResponseEntity<String> testCookie(Authentication authentication) {
        return new ResponseEntity<>(gson.toJson(authentication.getName()), HttpStatus.OK);
    }
}
