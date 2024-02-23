package com.instagram.demo.controller;

import com.instagram.demo.data.projection.comment.CommentProjection;
import com.instagram.demo.data.projection.person.PersonFeed;
import com.instagram.demo.data.projection.post.PostFeedProjection;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Person;
import com.instagram.demo.data.schema.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

record PostPreview(Long id, String image, Long likesCount, Long commentCounts) {
}

record PostResponse(String photo, Set<String> hashtags,
                    String image, Long timeUntilNow, String description, Page<CommentProjection> comments,
                    Long likesCount) {
}

record PostFeed(PostFeedProjection postFeedProjection, Long likesCount, Long commentCounts) {
}

@RestController
@RequestMapping(path = "/posts/", produces = "application/json")
@AllArgsConstructor
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

    @GetMapping("{id}")
    Optional<PostResponse> post(@PathVariable Long id, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 2);
        return postRepository
                .findPostById(id)
                .map(postProjection ->
                        new PostResponse(
                                postProjection.getUploaderPhoto(),
                                postRepository.findHashtagsByPostId(id),
                                postProjection.getImage(),
                                postProjection.getTimeUntilNow(),
                                postProjection.getDescription(),
                                commentRepository.findByPostIdOrderByDateDesc(id, pageRequest),
                                personRepository.countLikersByPostId(id)
                        ));
    }

    @GetMapping("preview/{username}")
    Page<PostPreview> postPreview(@PathVariable String username, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 3);
        return postRepository
                .findPostsByUploaderUsernameOrderByDateDesc(username, pageRequest)
                .map(postPreviewProjection ->
                        new PostPreview(postPreviewProjection.getId(),
                                postPreviewProjection.getImage(),
                                personRepository.countLikersByPostId(postPreviewProjection.getId()),
                                commentRepository.countByPostId(postPreviewProjection.getId())
                        ));
    }

    @GetMapping("feed")
    Page<PostFeed> feed(Authentication authentication, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 1);
        return postRepository.findByUploaderIdInOrderByDateDesc(
                        personRepository.findByFollowersUsername(authentication.getName())
                                .stream()
                                .map(PersonFeed::getId)
                                .collect(Collectors.toSet()),
                        pageRequest)
                .map(postFeedProjection -> new PostFeed(
                                postFeedProjection,
                                personRepository.countLikersByPostId(postFeedProjection.getId()),
                                commentRepository.countByPostId(postFeedProjection.getId())
                        )
                );
    }

    @PostMapping("like/{postId}")
    ResponseEntity<String> likePost(@PathVariable Long postId, Authentication authentication) {
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

            // Initialize likers set if null
            if (post.getLikers() == null) {
                post.setLikers(new HashSet<>());
            }

            // Initialize likedPost set if null
            if (person.getLikedPosts() == null) {
                person.setLikedPosts(new HashSet<>());
            }

            // Toggle post like status for the user
            if (post.getLikers().contains(person)) {
                // If the user has already liked the post, remove the like
                post.getLikers().remove(person);
                person.getLikedPosts().remove(post);
            } else {
                // If the user has not liked the post before, like the post
                post.getLikers().add(person);
                person.getLikedPosts().add(post);
            }

            // Save both the Person and Post objects to persist the changes in the database
            personRepository.save(person);
            postRepository.save(post);

            // Return appropriate response based on like toggling
            if (post.getLikers().contains(person)) {
                return new ResponseEntity<>("Post liked successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Post unliked successfully", HttpStatus.OK);
            }
        } catch (EntityNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
