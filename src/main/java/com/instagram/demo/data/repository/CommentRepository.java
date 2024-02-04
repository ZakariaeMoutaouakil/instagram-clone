package com.instagram.demo.data.repository;

import com.instagram.demo.data.schema.Comment;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    // Counts number of comments under a given post given its image url
    long countByPostImage(String image);
}
