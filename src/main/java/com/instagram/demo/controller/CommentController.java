package com.instagram.demo.controller;

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

@RestController
@RequestMapping(path = "/comments/", produces = "application/json")
@AllArgsConstructor
public class CommentController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

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

            return ResponseEntity.status(HttpStatus.CREATED).body("Comment successfully created");
        } catch (EntityNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
