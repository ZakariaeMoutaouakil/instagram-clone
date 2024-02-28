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

/**
 * REST controller for managing person-related endpoints.
 * Handles HTTP requests related to persons and produces JSON responses.
 */
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

    /**
     * Gson instance for JSON serialization and deserialization.
     */
    private final Gson gson;

    /**
     * PasswordEncoder instance for encoding and verifying passwords.
     */
    private final PasswordEncoder passwordEncoder;

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

    /**
     * Retrieves statistics of a person including the number of followers, followings, and posts.
     *
     * @param username The username of the person to retrieve statistics for.
     * @return A HashMap containing the statistics with keys "followers", "followings", and "posts".
     */
    @GetMapping("/stats/{username}")
    HashMap<String, Long> personStats(@PathVariable String username) {
        HashMap<String, Long> hashMap = new HashMap<>();
        hashMap.put("followers", personRepository.countFollowersByUsername(username));
        hashMap.put("followings", personRepository.countByFollowersUsername(username));
        hashMap.put("posts", postRepository.countByUploaderUsername(username));
        return hashMap;
    }

    /**
     * Follows or unfollows a person based on the provided username.
     *
     * @param username       The username of the person to follow or unfollow.
     * @param authentication The authentication object containing details about the currently logged-in user.
     * @return A ResponseEntity indicating the success or failure of the follow action.
     */
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

                return ResponseEntity.ok(gson.toJson("Followship removed successfully"));
            } else {
                // If not following, then follow
                loggedInUser.getFollowees().add(targetUser);
                targetUser.getFollowers().add(loggedInUser);

                personRepository.save(loggedInUser);
                personRepository.save(targetUser);

                return new ResponseEntity<>(
                        gson.toJson("Followship created successfully"),
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
    @PostMapping(path = "", consumes = "application/json")
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
                    .body(gson.toJson("An error occurred while registering the user"));
        }
    }

    /**
     * Updates the details of the authenticated user.
     *
     * @param editUserRequestBody The request body containing the updated user information.
     * @param authentication      The authentication object containing details about the current user.
     * @return A ResponseEntity containing a success message if the update is successful,
     * or an error message if the update fails or the user is not found.
     */
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<String> updateUser(@RequestBody EditUserRequestBody editUserRequestBody,
                                             Authentication authentication) {
        try {
            // Obtain authenticated user's information
            String authenticatedUsername = authentication.getName();

            // Find the user in the database by username
            Person authenticatedUser = personRepository
                    .findFirstByUsername(authenticatedUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            logger.debug(authenticatedUser.toString());
            Optional<Person> unavailableUser = personRepository
                    .findFirstByUsername(editUserRequestBody.username());

            // Check if the authenticated user is the same as the user being edited
            if (unavailableUser.isPresent()
                    && !unavailableUser.get().getUsername().equals(authenticatedUsername)) {
                logger.debug(unavailableUser.toString());
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(gson.toJson("The username is already taken by another user"));
            }

            // Update the existing user with the new details
            authenticatedUser.setUsername(editUserRequestBody.username());
            authenticatedUser.setPhoto(editUserRequestBody.photo());
            authenticatedUser.setEmail(editUserRequestBody.email());
            authenticatedUser.setFirstname(editUserRequestBody.firstname());
            authenticatedUser.setLastname(editUserRequestBody.lastname());
            authenticatedUser.setBio(editUserRequestBody.bio());

            logger.debug(authenticatedUser.toString());
            // Save the updated user
            personRepository.save(authenticatedUser);

            // Return success response
            return ResponseEntity.ok(gson.toJson(gson.toJson("User updated successfully")));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred while updating the user"));
        }
    }

    /**
     * Deletes a person from the system along with their related data, including followers, followees, and liked posts.
     * This operation requires authentication, and only the authenticated user can delete their own account.
     *
     * @param authentication The authentication object containing details about the current user.
     * @return A ResponseEntity containing a success message if the deletion is successful,
     * or an error message if the deletion fails or the user is not found.
     */
    @Transactional
    @DeleteMapping("")
    public ResponseEntity<String> deletePerson(Authentication authentication) {
        try {
            // Retrieve authenticated username
            String authenticatedUsername = authentication.getName();

            // Find the person by username
            Person person = personRepository
                    .findFirstByUsername(authenticatedUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Delete the person
            personRepository.deleteFolloweesAndFollowersById(person.getId());
            personRepository.deleteLikesById(person.getId());
            personRepository.delete(person);

            // Return success response
            return ResponseEntity.ok(gson.toJson("Person deleted successfully"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            // Return error response if deletion fails
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred while deleting the person"));
        }
    }
}
