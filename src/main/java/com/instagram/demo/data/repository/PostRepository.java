package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.PostProjection;
import com.instagram.demo.data.schema.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    // Counts number of posts uploaded by a specific user given its username
    long countByUploaderUsername(String username);

    List<PostProjection> findPostsByUploaderUsername(String username);
}
