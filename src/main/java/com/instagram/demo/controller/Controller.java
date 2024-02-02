package com.instagram.demo.controller;

import com.instagram.demo.repository.query.PersonProjection;
import com.instagram.demo.repository.query.PersonRepository;
import com.instagram.demo.repository.query.PostRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/", produces="application/json")
@AllArgsConstructor
@CrossOrigin(origins="http://localhost:8080")
@Transactional
public class Controller {
    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    @GetMapping("/")
//    @ResponseBody
    int all() {
        return 1;//postRepository.findAll();
    }
    @GetMapping("/persons/{username}")
    Optional<PersonProjection> person(@PathVariable String username) {
        return personRepository.findByUsername(username);
    }
    @GetMapping("/persons/")
    Optional<PersonProjection> post(@RequestParam String username) {
        return personRepository.findByUsername(username);
    }
}
