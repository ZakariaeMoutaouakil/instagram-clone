package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.person.PersonFeed;
import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.projection.person.PersonSuggestion;
import com.instagram.demo.data.schema.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This interface provides methods to interact with the database table storing person entities.
 */
public interface PersonRepository extends CrudRepository<Person, Long> {
    /**
     * Finds the first person by the given username.
     *
     * @param username The username of the person to find.
     * @return An {@link Optional} containing the found person, or empty if not found.
     */
    Optional<Person> findFirstByUsername(String username);

    /**
     * Finds a person projection by the given username.
     *
     * @param username The username of the person to find.
     * @return An {@link Optional} containing the found person projection, or empty if not found.
     */
    Optional<PersonProjection> findByUsername(String username);

    /**
     * Counts the number of people followed by the given user.
     *
     * @param username The username of the user whose followed count is to be counted.
     * @return The number of people followed by the user.
     */
    long countByFollowersUsername(String username);

    /**
     * Counts the number of people following the given user.
     *
     * @param username The username of the user whose followers count is to be counted.
     * @return The number of people following the user.
     */
    @Query("SELECT COUNT(u) FROM Person u JOIN u.followees f WHERE f.username = :username")
    long countFollowersByUsername(@Param("username") String username);


    /**
     * Counts the number of people who like a given post identified by its ID.
     *
     * @param postId The ID of the post to count likes for.
     * @return The number of people who like the post.
     */
    @Query("SELECT COUNT(l) FROM Post p JOIN p.likers l WHERE p.id = :postId")
    long countLikersByPostId(@Param("postId") Long postId);

    /**
     * Finds the IDs of users that the given username follows.
     *
     * @param username The username of the user whose followed users are to be found.
     * @return A set of {@link PersonFeed} containing the IDs of the followed users.
     */
    Set<PersonFeed> findByFollowersUsername(String username);

    /**
     * Finds three random users not followed by the given user.
     *
     * @param username The username of the user for whom random users are to be found.
     * @return A list of {@link PersonSuggestion} representing three random users not followed by the given user.
     */
    @Query(value = "SELECT * " +
            "FROM person " +
            "WHERE username <> ?1 " +  // Exclude the given user
            "AND username NOT IN ( " +
            "    SELECT p.username " +
            "    FROM person_followers pf " +
            "    JOIN person p ON pf.followees_id = p.id " +
            "    JOIN person f ON pf.followers_id = f.id " +
            "    WHERE f.username = ?1 " +  // Check if the user is not followed
            ") " +
            "ORDER BY RANDOM() " +  // Random ordering
            "LIMIT 3",
            nativeQuery = true)
    List<PersonSuggestion> findThreeRandomUsersNotFollowedByUser(String username);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Person f " +
            "JOIN f.followers u " +
            "WHERE u.username = :followerUsername AND f.username = :followeeUsername")
    boolean isFollowing(@Param("followerUsername") String followerUsername, @Param("followeeUsername") String followeeUsername);
}
