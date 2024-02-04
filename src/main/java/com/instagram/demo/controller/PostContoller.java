package com.instagram.demo.controller;

import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Post;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @GetMapping("all/{username}")
    List<Post> findPost(@PathVariable String username){
        return postRepository.findAllByUploaderUsername(username);
    }

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
    Set<PostPreview> postPreview2(@PathVariable String username) {
        return postRepository
                .findPostsByUploaderUsername(username)
                .stream()
                .map(
                        postProjection -> new PostPreview(
                                postProjection.getImage(),
                                (long) postProjection.getLikers().size(),
                                (long) postProjection.getComments().size()
                        )
                )
                .collect(Collectors.toSet());
    }
}
