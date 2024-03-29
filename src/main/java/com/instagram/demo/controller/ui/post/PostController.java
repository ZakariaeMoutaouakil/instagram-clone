package com.instagram.demo.controller.ui.post;

import com.google.gson.Gson;
import com.instagram.demo.data.projection.comment.CommentProjection;
import com.instagram.demo.data.projection.person.PersonFeed;
import com.instagram.demo.data.projection.post.PostFeedProjection;
import com.instagram.demo.data.repository.CommentRepository;
import com.instagram.demo.data.repository.PersonRepository;
import com.instagram.demo.data.repository.PostRepository;
import com.instagram.demo.data.schema.Person;
import com.instagram.demo.data.schema.Post;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a preview of a post.
 * This record contains essential information about a post that is suitable for displaying in a preview format.
 *
 * @param id            The ID of the post.
 * @param image         The URL or path to the image associated with the post.
 * @param likesCount    The number of likes received by the post.
 * @param commentCounts The number of comments made on the post.
 */
record PostPreview(Long id, String image, Long likesCount, Long commentCounts) {
}

/**
 * Represents a detailed response for a post.
 * This record contains comprehensive information about a post, including its image, description, hashtags, comments, and likes.
 *
 * @param photo        The URL or path to the uploader's photo.
 * @param hashtags     The set of hashtags associated with the post.
 * @param image        The URL or path to the image associated with the post.
 * @param timeUntilNow The time elapsed since the post was made.
 * @param description  The description or caption of the post.
 * @param comments     The page containing comments associated with the post.
 * @param likesCount   The total number of likes received by the post.
 */
record PostResponse(String photo,
                    Set<String> hashtags,
                    String image,
                    Long timeUntilNow,
                    String description,
                    Page<CommentProjection> comments,
                    Long likesCount,
                    Boolean like) {
}

/**
 * Represents a post for feed.
 * This record contains information about a post suitable for displaying in a feed.
 *
 * @param postFeedProjection The projection of the post.
 * @param likesCount         The number of likes received by the post.
 * @param commentCounts      The number of comments made on the post.
 */
record PostFeed(PostFeedProjection postFeedProjection,
                Long likesCount,
                Long commentCounts,
                Boolean like) {
}

/**
 * Controller class for managing operations related to posts.
 * This controller provides endpoints for retrieving, creating, updating, and deleting posts,
 * as well as handling operations such as liking/unliking posts and fetching post previews.
 */
@RestController
@RequestMapping(path = "/posts/", produces = "application/json")
@AllArgsConstructor
public class PostController {
    /**
     * Logger instance for logging messages related to PostController class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    /**
     * Repository for performing CRUD operations on posts.
     */
    private final PostRepository postRepository;

    /**
     * Repository for performing CRUD operations on comments.
     */
    private final CommentRepository commentRepository;

    /**
     * Repository for performing CRUD operations on persons (users).
     */
    private final PersonRepository personRepository;

    /**
     * Gson instance used for JSON serialization and deserialization.
     */
    private final Gson gson;

    /**
     * Retrieves detailed information about a specific post by its ID.
     * This endpoint returns an optional {@link PostResponse} object representing the post with the specified ID.
     * If the post with the given ID exists, the method returns a populated {@link Optional} containing the post details;
     * otherwise, it returns an empty {@link Optional}.
     *
     * @param postId     The ID of the post to retrieve.
     * @param pageNumber The page number for retrieving comments associated with the post. Page numbering starts from 0.
     * @return An {@link Optional} containing a {@link PostResponse} object representing the detailed information about the post
     * if the post with the specified ID exists; otherwise, an empty {@link Optional}.
     */
    @GetMapping("{postId}")
    Optional<PostResponse> post(@PathVariable Long postId,
                                @RequestParam(defaultValue = "0", required = false) Integer pageNumber,
                                Authentication authentication) {
        Pageable pageRequest = PageRequest.of(pageNumber, 2);
        return postRepository
                .findPostById(postId)
                .map(postProjection ->
                        new PostResponse(
                                postProjection.getUploaderPhoto(),
                                postRepository.findHashtagsByPostId(postId),
                                postProjection.getImage(),
                                postProjection.getTimeUntilNow(),
                                postProjection.getDescription(),
                                commentRepository.findByPostIdOrderByDateDesc(postId, pageRequest),
                                personRepository.countLikersByPostId(postId),
                                postRepository.existsLikedPostByUser(postId, authentication.getName())
                        ));
    }


    /**
     * Updates an existing post with the provided details.
     *
     * @param postId          The ID of the post to be edited.
     * @param requestPostBody The request body containing the updated post details.
     * @param authentication  The authentication object representing the current user.
     * @return ResponseEntity with a success message if the post is successfully edited,
     * or an error message if an exception occurs.
     */
    @Transactional
    @PutMapping(path = "{postId}", consumes = "application/json")
    public ResponseEntity<String> editPost(@PathVariable Long postId,
                                           @RequestBody RequestPostBody requestPostBody,
                                           Authentication authentication) {
        try {
            // Obtain authenticated user's information
            String authenticatedUsername = authentication.getName();

            // Retrieve the post from the database using its ID
            Post post = postRepository
                    .findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("Post not found"));

            // Check if the authenticated user is the uploader of the post
            if (!post.getUploader().getUsername().equals(authenticatedUsername)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(gson.toJson("You are not authorized to edit this post"));
            }

            // Update the post with the new details
            post.setDescription(requestPostBody.description());
            post.setImage(requestPostBody.image());
            logger.debug(post.toString());

            postRepository.deleteHashtagsByPostId(postId);
            for (String hashtag : requestPostBody.hashtags()) {
                logger.debug("hashtag=" + hashtag);
                postRepository.insertHashtagsByPostId(postId, hashtag);
            }

            // Save the updated post
            postRepository.save(post);

            // Return success response
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson("Post edited successfully"));
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred while editing the post"));
        }
    }

    /**
     * Retrieves a page of post previews for a specific user.
     * This endpoint returns a paginated list of post previews belonging to the specified user.
     * The posts are sorted by date in descending order.
     *
     * @param username   The username of the user whose post previews are to be retrieved.
     * @param pageNumber The page number to retrieve. Page numbering starts from 0.
     * @return A {@link org.springframework.data.domain.Page} containing {@link PostPreview} objects representing post previews for the specified user.
     * Each post preview includes essential information such as the post ID, image, number of likers, and number of comments.
     */
    @GetMapping("preview/{username}")
    Page<PostPreview> postPreview(@PathVariable String username, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 3);
        return postRepository
                .findPostsByUploaderUsernameOrderByDateDesc(username, pageRequest)
                .map(postPreviewProjection ->
                        new PostPreview(postPreviewProjection.getId(),
                                postPreviewProjection.getImage(),
                                personRepository.countLikersByPostId(postPreviewProjection.getId()),
                                commentRepository.countByPostId(postPreviewProjection.getId())
                        ));
    }

    /**
     * Retrieves a page of posts for the authenticated user's feed.
     * This endpoint returns a paginated list of posts from users whom the authenticated user is following.
     * The posts are sorted by date in descending order.
     *
     * @param authentication The authentication object representing the currently authenticated user.
     * @param pageNumber     The page number to retrieve. Page numbering starts from 0.
     * @return A {@link org.springframework.data.domain.Page} containing {@link PostFeed} objects representing posts in the feed.
     * Each post is augmented with additional information such as the number of likers and comments.
     * @throws org.springframework.security.core.AuthenticationException if the user is not authenticated.
     */
    @GetMapping("feed")
    Page<PostFeed> feed(Authentication authentication, @RequestParam Integer pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, 1);
        return postRepository.findByUploaderIdInOrderByDateDesc(
                        personRepository.findByFollowersUsername(authentication.getName())
                                .stream()
                                .map(PersonFeed::getId)
                                .collect(Collectors.toSet()),
                        pageRequest)
                .map(postFeedProjection -> new PostFeed(
                                postFeedProjection,
                                personRepository.countLikersByPostId(postFeedProjection.getId()),
                                commentRepository.countByPostId(postFeedProjection.getId()),
                                postRepository.existsLikedPostByUser(
                                        postFeedProjection.getId(),
                                        authentication.getName()
                                )
                        )
                );
    }

    /**
     * Handles liking/unliking a post by a user.
     * This endpoint allows a user to like or unlike a post with the specified ID.
     * If the user has already liked the post, the like will be removed.
     * If the user has not liked the post before, the post will be liked.
     *
     * @param postId         The ID of the post to be liked/unliked.
     * @param authentication The authentication object representing the currently authenticated user.
     * @return A {@link org.springframework.http.ResponseEntity} containing a message indicating the success or failure
     * of the like/unlike operation. If the operation is successful, the response status is {@link org.springframework.http.HttpStatus#CREATED}
     * for liking a post and {@link org.springframework.http.HttpStatus#OK} for unliking a post.
     * If the post or user is not found, the response status is {@link org.springframework.http.HttpStatus#NOT_FOUND}.
     * If an unexpected error occurs during the operation, the response status is {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}.
     */
    @Transactional
    @PostMapping("like/{postId}")
    ResponseEntity<String> likePost(@PathVariable Long postId, Authentication authentication) {
        try {
            Person person = personRepository
                    .findFirstByUsername(authentication.getName())
                    .orElseThrow(
                            () -> new UsernameNotFoundException("User not found")
                    );

            Post post = postRepository
                    .findById(postId)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Post not found")
                    );

            // Initialize likers set if null
            if (post.getLikers() == null) {
                post.setLikers(new HashSet<>());
            }

            // Initialize LikedPosts set if null
            if (person.getLikedPosts() == null) {
                person.setLikedPosts(new HashSet<>());
            }

            // Toggle post like status for the user
            if (post.getLikers().contains(person)) {
                // If the user has already liked the post, remove the like
                post.getLikers().remove(person);
                person.getLikedPosts().remove(post);
            } else {
                // If the user has not liked the post before, like the post
                post.getLikers().add(person);
                person.getLikedPosts().add(post);
            }

            // Save both the Person and Post objects to persist the changes in the database
            personRepository.save(person);
            postRepository.save(post);

            // Return appropriate response based on like toggling
            if (post.getLikers().contains(person)) {
                return new ResponseEntity<>(
                        gson.toJson("Post liked successfully"),
                        HttpStatus.CREATED
                );
            } else {
                return new ResponseEntity<>(
                        gson.toJson("Post unliked successfully"),
                        HttpStatus.OK
                );
            }
        } catch (EntityNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred"));
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> createPost(@RequestBody RequestPostBody requestPostBody,
                                        Authentication authentication) {
        try {
            // Retrieve the authenticated user
            Person uploader = personRepository.findFirstByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Assign the authenticated user as the uploader of the post
            Post post = new Post(requestPostBody, uploader);
            logger.debug(post.toString());

            // Save the post
            Post savedPost = postRepository.save(post);
            logger.debug(savedPost.toString());

            // Create a PostPreview object from the saved post
            PostPreview postPreview = new PostPreview(
                    savedPost.getId(),
                    savedPost.getImage(),
                    0L,
                    0L
            );

            // Return ResponseEntity with the created PostPreview object
            return ResponseEntity.status(201).body(postPreview);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Log the exception
            logger.error("Error occurred while creating a post: " + e.getMessage());
            // Return ResponseEntity with an error message
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred while creating the post."));
        }
    }

    /**
     * Deletes a post with the specified ID if the authenticated user is the uploader.
     * If the post is successfully deleted, returns a success response.
     * If the post is not found, returns a 404 Not Found response.
     * If an error occurs during the deletion process, returns a 500 Internal Server Error response.
     *
     * @param postId         The ID of the post to delete.
     * @param authentication The authentication object containing the details of the authenticated user.
     * @return A ResponseEntity containing the result of the deletion operation.
     */
    @Transactional
    @DeleteMapping("{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId, Authentication authentication) {
        try {
            // Obtain authenticated user's information
            String authenticatedUsername = authentication.getName();

            // Retrieve the post from the database using its ID
            Post post = postRepository
                    .findById(postId)
                    .orElseThrow(() -> new PostNotFoundException("Post not found"));
            logger.debug(post.toString());

            // Check if the authenticated user is the uploader of the post
            if (!post.getUploader().getUsername().equals(authenticatedUsername)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(gson.toJson("You are not authorized to delete this post"));
            }
            logger.debug(post.toString());

            // If the authenticated user is the uploader, delete the post
            postRepository.deleteLikesByPostId(postId);
            postRepository.delete(post);

            // Return success response
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson("Post deleted successfully"));
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(gson.toJson("An error occurred while deleting the post"));
        }
    }

    /**
     * Exception thrown when a post is not found.
     */
    static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(String message) {
            super(message);
        }
    }
}
