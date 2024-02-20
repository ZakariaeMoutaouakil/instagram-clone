package com.instagram.demo.controller;

import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.schema.Person;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

record RegisterUserCredentials(String username, String email, String password, String firstname, String lastname) {
}

@RestController
@RequestMapping(path = "/", produces = "application/json")
@AllArgsConstructor
public class AuthController {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

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

    @GetMapping("login")
    public PersonProjection login(Authentication authentication) {
        return personRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with specified credentials is not found.")
                );
    }

    @GetMapping("test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok().build();
    }
}
