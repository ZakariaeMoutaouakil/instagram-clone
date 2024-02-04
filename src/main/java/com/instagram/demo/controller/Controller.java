package com.instagram.demo.controller;

import com.instagram.demo.data.projection.PersonProjection;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping(path = "/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class Controller {
    private final PostRepository postRepository;
    private final PersonRepository personRepository;

    @GetMapping("/")
    int all() {
        return 1;//postRepository.findAll();
    }

    @GetMapping("/persons/info/{username}")
    Optional<PersonProjection> personBio(@PathVariable String username) {
        return personRepository.findByUsername(username);
    }

    @GetMapping("/persons/stats/{username}")
    HashMap<String, Long> personStats(@PathVariable String username) {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("followers", personRepository.countFollowersByUsername(username));
        hashMap.put("followings", personRepository.countByFollowersUsername(username));
        hashMap.put("posts", postRepository.countByUploaderUsername(username));
        return hashMap;
    }
}
