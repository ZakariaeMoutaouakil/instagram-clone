package com.instagram.demo.controller;

import com.instagram.demo.data.projection.PostProjection;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Post;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

record PostPreview(String image, Long likesCount, Long commentCounts) {
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

    @GetMapping("preview/{username}")
    Page<PostPreview> postPreview(@PathVariable String username, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 3, Sort.by("date").descending());
        return postRepository
                .findPostsByUploaderUsername(username, pageRequest)
                .map(PostProjection::getImage)
                .map(image ->
                        new PostPreview(image,
                                personRepository.countByLikedPostImage(image),
                                commentRepository.countByPostImage(image)
                        ));
    }
}
