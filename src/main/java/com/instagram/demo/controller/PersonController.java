package com.instagram.demo.controller;

import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;



@RestController
@RequestMapping(path = "/persons/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {
    private final PostRepository postRepository;
    private final PersonRepository personRepository;

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
}
