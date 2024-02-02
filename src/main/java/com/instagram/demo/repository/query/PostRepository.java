package com.instagram.demo.repository.query;

import com.instagram.demo.repository.schema.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    Post findById(long id);
    List<Post> findAll();
}
