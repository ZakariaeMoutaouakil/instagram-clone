package com.instagram.demo.controller;

import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

record PostPreview(String image, Long likesCount, Long commentCounts) {
}

@RestController
@RequestMapping(path = "/posts/", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostContoller {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PersonRepository personRepository;

    @GetMapping("preview/{username}")
    ArrayList<PostPreview> postPreview(@PathVariable String username) {
        ArrayList<PostPreview> arrayList = new ArrayList<>();
        for (var image : postRepository.findPostsByUploaderUsername(username)) {
            String imageUrl = image.getImage();
            arrayList.add(new PostPreview(imageUrl,
                    personRepository.countByLikedPostImage(imageUrl),
                    commentRepository.countByPostImage(imageUrl)
            ));
        }
        return arrayList;
    }
    @GetMapping("preview2/{username}")
    ArrayList<PostPreview> postPreview2(@PathVariable String username) {
        ArrayList<PostPreview> arrayList = new ArrayList<>();
        for (var image : postRepository.findPostsByUploaderUsername(username)) {
            String imageUrl = image.getImage();
            arrayList.add(new PostPreview(imageUrl,
                    personRepository.countByLikedPostImage(imageUrl),
                    commentRepository.countByPostImage(imageUrl)
            ));
        }
        return arrayList;
    }
}
