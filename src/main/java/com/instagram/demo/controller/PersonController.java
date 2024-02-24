package com.instagram.demo.controller;

import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.projection.person.PersonSuggestion;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/persons/", produces = "application/json")
@AllArgsConstructor
public class PersonController {
    /**
     * Logger instance for logging messages related to PersonController class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    /**
     * Repository for performing CRUD operations on posts.
     */
    private final PostRepository postRepository;

    /**
     * Repository for performing CRUD operations on persons (users).
     */
    private final PersonRepository personRepository;

    @GetMapping("suggestions")
    List<PersonSuggestion> personSuggestions(Authentication authentication) {
        return personRepository.findThreeRandomUsersNotFollowedByUser(authentication.getName());
    }

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
