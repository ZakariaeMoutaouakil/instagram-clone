package com.instagram.demo.controller;

import com.google.gson.Gson;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.schema.Person;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Represents the credentials for registering a new user.
 * Includes fields for username, email, password, first name, and last name.
 */
record RegisterUserCredentials(String username, String email, String password, String firstname, String lastname) {
}

/**
 * Controller handling authentication-related endpoints.
 * Provides endpoints for user registration and login.
 */
@RestController
@RequestMapping(path = "/", produces = "application/json")
@AllArgsConstructor
public class AuthController {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint for registering a new user.
     * Saves the user details provided in the request body.
     * Responds with HTTP status 201 (Created) upon successful registration.
     *
     * @param registerUserCredentials User credentials provided in the request body.
     * @return The newly registered user.
     */
    @PostMapping(path = "register")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "Your account was successfully created.")
    Person register(@RequestBody RegisterUserCredentials registerUserCredentials) {
        return personRepository.save(new Person(
                registerUserCredentials.username(),
                registerUserCredentials.email(),
                passwordEncoder.encode(registerUserCredentials.password()),
                registerUserCredentials.firstname(),
                registerUserCredentials.lastname()
        ));
    }

    /**
     * Endpoint for user login.
     * Retrieves the username of the authenticated user and responds with it.
     *
     * @param authentication Authentication object containing details of the authenticated user.
     * @return ResponseEntity containing the username of the authenticated user.
     */
    @GetMapping("login")
    ResponseEntity<String> login(Authentication authentication) {
        return new ResponseEntity<>(new Gson().toJson(authentication.getName()), HttpStatus.OK);
    }
}
