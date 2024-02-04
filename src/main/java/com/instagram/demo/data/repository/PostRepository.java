package com.instagram.demo.data.repository;

import com.instagram.demo.data.schema.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByUploaderUsername(String username);

    long countByUploaderUsername(String username);
}
