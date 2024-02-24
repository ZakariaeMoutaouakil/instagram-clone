package com.instagram.demo.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.instagram.demo.data.projection.person.PersonSuggestion;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Person;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

//    private final ModelMapper modelMapper;

    private final Gson gson;

    @GetMapping("suggestions")
    List<PersonSuggestion> personSuggestions(Authentication authentication) {
        return personRepository.findThreeRandomUsersNotFollowedByUser(authentication.getName());
    }

    /**
     * Retrieves information about a person by their username.
     * Constructs a JSON object with the person's attributes and adds a boolean attribute.
     *
     * @param username The username of the person to retrieve information for.
     * @return A JSON object containing information about the person.
     */
    @GetMapping("info/{username}")
    Optional<String> personBio(@PathVariable String username, Authentication authentication) {
        return personRepository.findByUsername(username)
                .map(projection -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("username", projection.getUsername());
                    jsonObject.addProperty("bio", projection.getBio());
                    jsonObject.addProperty("firstname", projection.getFirstname());
                    jsonObject.addProperty("lastname", projection.getLastname());
                    jsonObject.addProperty("validated", projection.getValidated());
                    jsonObject.addProperty("photo", projection.getPhoto());
                    jsonObject.addProperty(
                            "follow",
                            personRepository.isFollowing(authentication.getName(), username)
                    );
                    return gson.toJson(jsonObject);
                });
    }

    @GetMapping("stats/{username}")
    HashMap<String, Long> personStats(@PathVariable String username) {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("followers", personRepository.countFollowersByUsername(username));
        hashMap.put("followings", personRepository.countByFollowersUsername(username));
        hashMap.put("posts", postRepository.countByUploaderUsername(username));
        return hashMap;
    }

    @Transactional
    @PostMapping("follow/{username}")
    public ResponseEntity<String> followPerson(@PathVariable String username, Authentication authentication) {
        Optional<Person> loggedInUserOptional = personRepository
                .findFirstByUsername(authentication.getName());
        Optional<Person> targetUserOptional = personRepository
                .findFirstByUsername(username);

        if (loggedInUserOptional.isPresent() && targetUserOptional.isPresent()) {
            Person loggedInUser = loggedInUserOptional.get();
            Person targetUser = targetUserOptional.get();

            if (loggedInUser.equals(targetUser)) {
                return ResponseEntity
                        .badRequest()
                        .body(new Gson().toJson("You cannot follow yourself."));
            }

            if (loggedInUser.getFollowees().contains(targetUser)) {
                // If already following, then unfollow
                loggedInUser.getFollowees().remove(targetUser);
                targetUser.getFollowers().remove(loggedInUser);

                personRepository.save(loggedInUser);
                personRepository.save(targetUser);

                return ResponseEntity.ok(new Gson().toJson("Followship removed succesfully"));
            } else {
                // If not following, then follow
                loggedInUser.getFollowees().add(targetUser);
                targetUser.getFollowers().add(loggedInUser);

                personRepository.save(loggedInUser);
                personRepository.save(targetUser);

                return new ResponseEntity<>(
                        new Gson().toJson("Followship created succesfully"),
                        HttpStatus.CREATED
                );
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
