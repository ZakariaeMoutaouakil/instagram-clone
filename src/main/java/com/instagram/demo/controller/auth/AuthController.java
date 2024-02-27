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
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint for registering a new user.
     * Saves the user details provided in the request body.
     *
     * @param registerUserCredentials User credentials provided in the request body.
     * @return ResponseEntity containing the newly registered user if successful,
     * or ResponseEntity with an error message if an exception occurs.
     */
    @PostMapping(path = "register")
    public ResponseEntity<?> register(@RequestBody RegisterUserCredentials registerUserCredentials) {
        try {
            Person savedPerson = personRepository.save(new Person(
                    registerUserCredentials.username(),
                    registerUserCredentials.email(),
                    passwordEncoder.encode(registerUserCredentials.password()),
                    registerUserCredentials.firstname(),
                    registerUserCredentials.lastname()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
        } catch (Exception e) {
            // Log the exception
            logger.error("Error occurred while registering user: " + e.getMessage());
            // Return ResponseEntity with error message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while registering the user.");
        }
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
