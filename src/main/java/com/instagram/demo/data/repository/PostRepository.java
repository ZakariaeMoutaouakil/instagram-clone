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

import java.util.Optional;
import java.util.Set;

/**
 * This interface provides CRUD operations for managing posts in the database.
 */
public interface PostRepository extends CrudRepository<Post, Long> {
    /**
     * Counts the number of posts uploaded by a specific user given its username.
     *
     * @param username The username of the uploader.
     * @return The number of posts uploaded by the user.
     */
    long countByUploaderUsername(String username);


    /**
     * Finds a page of post preview projections uploaded by a specific user ordered by date descending.
     *
     * @param username The username of the uploader.
     * @param pageable The pagination information.
     * @return A page of post preview projections ordered by date descending.
     */
    Page<PostPreviewProjection> findPostsByUploaderUsernameOrderByDateDesc(String username, Pageable pageable);

    /**
     * Finds a post projection by its ID.
     *
     * @param id The ID of the post.
     * @return An {@link Optional} containing the post projection, or empty if not found.
     */
    Optional<PostProjection> findPostById(Long id);

    /**
     * Finds the hashtags associated with a post by its ID.
     *
     * @param id The ID of the post.
     * @return A set of hashtags associated with the post.
     */
    @Query(value = "SELECT hashtags FROM post_hashtags WHERE post_id = ?1", nativeQuery = true)
    Set<String> findHashtagsByPostId(Long id);

    /**
     * Finds a page of post feed projections by uploader IDs ordered by date descending.
     *
     * @param ids      The set of uploader IDs.
     * @param pageable The pagination information.
     * @return A page of post feed projections ordered by date descending.
     */
    Page<PostFeedProjection> findByUploaderIdInOrderByDateDesc(Set<Long> ids, Pageable pageable);

    /**
     * Checks if a user has liked a post.
     *
     * @param postId   The ID of the post.
     * @param username The username of the user.
     * @return {@code true} if the user has liked the post, {@code false} otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Post p JOIN p.likers liker WHERE p.id = :postId AND liker.username = :username")
    boolean existsLikedPostByUser(@Param("postId") Long postId, @Param("username") String username);

}
