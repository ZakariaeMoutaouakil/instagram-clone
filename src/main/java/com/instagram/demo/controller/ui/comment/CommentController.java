package com.instagram.demo.controller.ui.comment;

import com.google.gson.Gson;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Comment;
import com.instagram.demo.data.schema.Person;
import com.instagram.demo.data.schema.Post;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    @PostMapping(path = "{postId}")
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

    /**
     * Updates the content of a comment if the authenticated user is the author of the comment.
     *
     * @param commentId      The ID of the comment to be updated.
     * @param updatedContent The updated content for the comment.
     * @param authentication An object representing the authentication details of the user making the request.
     * @return ResponseEntity containing a message indicating the outcome of the update operation.
     * - If the comment is successfully updated, returns ResponseEntity with HTTP status OK (200).
     * - If the comment is not found, returns ResponseEntity with HTTP status NOT_FOUND (404).
     * - If the authenticated user is not authorized to update the comment, returns ResponseEntity
     * with HTTP status FORBIDDEN (403) along with a message indicating the lack of authorization.
     * - If an unexpected error occurs during the update process, returns ResponseEntity with
     * HTTP status INTERNAL_SERVER_ERROR (500) along with a generic error message.
     */
    @Transactional
    @PutMapping("{commentId}")
    public ResponseEntity<String> updateCommentContent(@PathVariable Long commentId,
                                                       @RequestBody String updatedContent,
                                                       Authentication authentication) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

            // Check if the authenticated user is the author of the comment
            if (!comment.getAuthor().getUsername().equals(authentication.getName())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new Gson().toJson("You are not authorized to update this comment"));
            }

            // Update the comment's content
            comment.setComment(updatedContent);
            commentRepository.save(comment);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Gson().toJson("Comment content successfully updated"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Gson().toJson("Comment not found"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Gson().toJson("An error occurred"));
        }
    }

    /**
     * Deletes a comment with the specified ID if the authenticated user is the author of the comment.
     *
     * @param commentId      The ID of the comment to be deleted.
     * @param authentication An object representing the authentication details of the user making the request.
     * @return ResponseEntity containing a message indicating the outcome of the deletion operation.
     * - If the comment is successfully deleted, returns ResponseEntity with HTTP status OK (200).
     * - If the comment is not found, returns ResponseEntity with HTTP status NOT_FOUND (404).
     * - If the authenticated user is not authorized to delete the comment, returns ResponseEntity
     * with HTTP status FORBIDDEN (403) along with a message indicating the lack of authorization.
     * - If an unexpected error occurs during the deletion process, returns ResponseEntity with
     * HTTP status INTERNAL_SERVER_ERROR (500) along with a generic error message.
     */
    @DeleteMapping("{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId,
                                                Authentication authentication) {
        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

            // Check if the authenticated user is the author of the comment
            if (!comment.getAuthor().getUsername().equals(authentication.getName())) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new Gson().toJson("You are not authorized to delete this comment"));
            }

            commentRepository.delete(comment);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new Gson().toJson("Comment successfully deleted"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Gson().toJson("Comment not found"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Gson().toJson("An error occurred"));
        }
    }
}
