package com.instagram.demo.controller;

import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.schema.Person;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("register")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "Your account was successfully created.")
    Person register(@RequestBody UserCredentials userCredentials) {
        return personRepository.save(new Person(
                userCredentials.username(),
                userCredentials.email(),
                passwordEncoder.encode(userCredentials.password()),
                userCredentials.firstname(),
                userCredentials.lastname()
        ));
    }
    @RequestMapping("login/{username}")
    public Optional<Person> getUserDetailsAfterLogin1(@PathVariable String username) {
        return personRepository.findFirstByUsername(username);
    }

    @RequestMapping("login")
    public Optional<Person> getUserDetailsAfterLogin(Authentication authentication) {
        return personRepository.findFirstByUsername(authentication.getName());
    }
}
