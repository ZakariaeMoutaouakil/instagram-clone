package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.post.PostFeedProjection;
import com.instagram.demo.data.projection.post.PostPreviewProjection;
import com.instagram.demo.data.projection.post.PostProjection;
import com.instagram.demo.data.schema.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Long> {
    // Counts number of posts uploaded by a specific user given its username
    long countByUploaderUsername(String username);

//    List<PostPreviewProjection> findPostsByUploaderUsername(String username);

    //    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likers LEFT JOIN FETCH p.comments WHERE p.uploader.username = :username")
    Page<PostPreviewProjection> findPostsByUploaderUsernameOrderByDateDesc(String username, Pageable pageable);

    List<Post> findAllByUploaderUsername(String username);

    Optional<PostProjection> findPostById(Long id);

    @Query(value = "SELECT hashtags FROM post_hashtags WHERE post_id = ?1", nativeQuery = true)
//    @Query(value = "SELECT p.hashtags FROM Post p WHERE p.id = :postId")
    Set<String> findHashtagsByPostId(Long id);

    Page<PostFeedProjection> findByUploaderIdInOrderByDateDesc(Set<Long> ids, Pageable pageable);

}
