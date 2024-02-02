package com.instagram.demo.repository.query;

import com.instagram.demo.repository.schema.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByUploaderUsername(String username);
}
