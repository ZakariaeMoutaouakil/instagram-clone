package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.PostProjection;
import com.instagram.demo.data.schema.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    // Counts number of posts uploaded by a specific user given its username
    long countByUploaderUsername(String username);

//    List<PostProjection> findPostsByUploaderUsername(String username);

//    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likers LEFT JOIN FETCH p.comments WHERE p.uploader.username = :username")
    List<PostProjection> findPostsByUploaderUsername(@Param("username") String username);
    List<Post> findAllByUploaderUsername(String username);
}
