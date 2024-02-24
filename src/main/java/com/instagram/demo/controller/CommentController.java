package com.instagram.demo.controller;

import com.google.gson.Gson;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Comment;
import com.instagram.demo.data.schema.Person;
import com.instagram.demo.data.schema.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing operations related to comments.
 * This controller provides endpoints for creating comments on posts.
 */
@RestController
@RequestMapping(path = "/comments/", produces = "application/json")
@AllArgsConstructor
public class CommentController {
    /**
     * Repository for performing CRUD operations on posts.
     */
    private final PostRepository postRepository;

    /**
     * Repository for performing CRUD operations on comments.
     */
    private final CommentRepository commentRepository;

    /**
     * Repository for performing CRUD operations on persons (users).
     */
    private final PersonRepository personRepository;

    /**
     * Creates a new comment on a post.
     *
     * @param comment        The content of the comment.
     * @param postId         The ID of the post on which the comment is being made.
     * @param authentication The authentication object representing the currently authenticated user.
     * @return A {@link org.springframework.http.ResponseEntity} containing a JSON-formatted string representing the result of the comment creation operation.
     * If the comment is created successfully, the response status is {@link org.springframework.http.HttpStatus#CREATED}, and the message in the response body indicates success.
     * If the post or user is not found, the response status is {@link org.springframework.http.HttpStatus#NOT_FOUND}, and the message in the response body indicates the reason for failure.
     * If an unexpected error occurs during the operation, the response status is {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}, and the message in the response body indicates the error.
     */
    @PostMapping(path = "create/{postId}")
    ResponseEntity<String> createComment(@RequestBody String comment,
                                         @PathVariable Long postId,
                                         Authentication authentication) {
        try {
            Person person = personRepository
                    .findFirstByUsername(authentication.getName())
                    .orElseThrow(
                            () -> new UsernameNotFoundException("User not found")
                    );

            Post post = postRepository
                    .findById(postId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Post not found")
                    );

            commentRepository.save(new Comment(person, comment, post));

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new Gson().toJson("Comment successfully created"));
        } catch (EntityNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Gson().toJson(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Gson().toJson("An error occurred"));
        }
    }
}
