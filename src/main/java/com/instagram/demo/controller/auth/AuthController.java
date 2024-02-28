package com.instagram.demo.controller.auth;

import com.google.gson.Gson;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.schema.Person;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
}
