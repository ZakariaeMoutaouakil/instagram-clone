package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.comment.CommentProjection;
import com.instagram.demo.data.schema.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


public interface CommentRepository extends CrudRepository<Comment, Long> {
    // Counts number of comments under a given post given its image url
    long countByPostId(Long id);
    Page<CommentProjection> findByPostIdOrderByDateDesc(Long postId, Pageable pageable);
}
