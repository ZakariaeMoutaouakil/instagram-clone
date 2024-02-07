package com.instagram.demo.controller;

import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Comment;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

record NewComment(String comment, String username) {
}

@RestController
@RequestMapping(path = "/comments/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

    @PostMapping(path = "{postId}")
    @ResponseStatus(code = HttpStatus.CREATED, reason = "Your comment was added.")
    Optional<Comment> createComment(@RequestBody NewComment newComment, @PathVariable Long postId) {
        return postRepository
                .findById(postId)
                .map(post -> new Comment(
                        personRepository.findFirstByUsername(newComment.username()).get(),
                        newComment.comment(),
                        post
                ))
                .map(commentRepository::save);
    }

}
