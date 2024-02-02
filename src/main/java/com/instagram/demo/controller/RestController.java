package com.instagram.demo.controller;

import com.instagram.demo.repository.query.PostRepository;
import com.instagram.demo.repository.schema.Post;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping
@AllArgsConstructor
public class RestController {
    private final PostRepository postRepository;

    @GetMapping("/")
    List<Post> all() {
        return postRepository.findAll();
    }
}
