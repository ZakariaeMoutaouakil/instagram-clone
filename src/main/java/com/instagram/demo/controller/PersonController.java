package com.instagram.demo.controller;

import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Person;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

record UserCredentials(String username, String email, String password, String firstname,
                       String lastname) {
}

@RestController
@RequestMapping(path = "/persons/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {
    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("info/{username}")
    Optional<PersonProjection> personBio(@PathVariable String username) {
        return personRepository.findByUsername(username);
    }

    @GetMapping("stats/{username}")
    HashMap<String, Long> personStats(@PathVariable String username) {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("followers", personRepository.countFollowersByUsername(username));
        hashMap.put("followings", personRepository.countByFollowersUsername(username));
        hashMap.put("posts", postRepository.countByUploaderUsername(username));
        return hashMap;
    }

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
}
