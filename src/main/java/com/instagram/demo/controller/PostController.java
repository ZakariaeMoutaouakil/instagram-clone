package com.instagram.demo.controller;

import com.instagram.demo.data.projection.comment.CommentProjection;
import com.instagram.demo.data.projection.person.PersonFeed;
import com.instagram.demo.data.projection.post.PostFeedProjection;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Post;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

    @GetMapping("all/{username}")
    List<Post> findPost(@PathVariable String username) {
        return postRepository.findAllByUploaderUsername(username);
    }

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
                                personRepository.countByLikedPostId(id)
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
                                personRepository.countByLikedPostId(postPreviewProjection.getId()),
                                commentRepository.countByPostId(postPreviewProjection.getId())
                        ));
    }

    @GetMapping("feed/{username}")
    Page<PostFeed> feed(@PathVariable String username, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 1);
        return postRepository.findByUploaderIdInOrderByDateDesc(
                        personRepository.findByFollowersUsername(username)
                                .stream()
                                .map(PersonFeed::getId)
                                .collect(Collectors.toSet()),
                        pageRequest)
                .map(postFeedProjection -> new PostFeed(
                                postFeedProjection,
                                personRepository.countByLikedPostId(postFeedProjection.getId()),
                                commentRepository.countByPostId(postFeedProjection.getId())
                        )
                );
    }
}
