package com.instagram.demo.controller.ui.person;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping(path = "/persons", produces = "application/json")
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

    private final Gson gson;

    private final PasswordEncoder passwordEncoder;

    private final PersonMapper personMapper;


    @GetMapping("/suggestions")
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
    @GetMapping("/info/{username}")
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

    @GetMapping("/stats/{username}")
    HashMap<String, Long> personStats(@PathVariable String username) {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("followers", personRepository.countFollowersByUsername(username));
        hashMap.put("followings", personRepository.countByFollowersUsername(username));
        hashMap.put("posts", postRepository.countByUploaderUsername(username));
        return hashMap;
    }

    @Transactional
    @PostMapping("/follow/{username}")
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
                        .body(gson.toJson("You cannot follow yourself."));
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
                        gson.toJson("Followship created succesfully"),
                        HttpStatus.CREATED
                );
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for registering a new user.
     * Saves the user details provided in the request body.
     *
     * @param registerUserCredentials User credentials provided in the request body.
     * @return ResponseEntity containing the newly registered user if successful,
     * or ResponseEntity with an error message if an exception occurs.
     */
    @PostMapping(path = "")
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

    @PutMapping("")
    public ResponseEntity<String> updateUser(@RequestBody EditUserRequestBody request,
                                             Authentication authentication) {
        try {
            // Obtain authenticated user's information
            String authenticatedUsername = authentication.getName();

            // Find the user in the database by username
            Person existingUser = personRepository
                    .findFirstByUsername(authenticatedUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Check if the authenticated user is the same as the user being edited
            if (!existingUser.getUsername().equals(request.username())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("You are not authorized to edit this user");
            }

            // Map the request body to the Person entity
            Person updatedUser = personMapper.mapToEntity(request);

            // Update the existing user with the new details
            existingUser.setPhoto(updatedUser.getPhoto());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstname(updatedUser.getFirstname());
            existingUser.setLastname(updatedUser.getLastname());
            existingUser.setBio(updatedUser.getBio());

            // Save the updated user
            personRepository.save(existingUser);

            // Return success response
            return ResponseEntity.ok("User updated successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the user");
        }
    }
}
