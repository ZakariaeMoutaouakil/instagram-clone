package com.instagram.demo.data.repository;

import com.instagram.demo.data.projection.person.PersonFeed;
import com.instagram.demo.data.projection.person.PersonProjection;
import com.instagram.demo.data.schema.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findFirstByUsername(String username);

    Optional<PersonProjection> findByUsername(String username);

    // Counts number of people the given user follows
    long countByFollowersUsername(String username);

    // Counts number of people that follow the given user
    long countFollowersByUsername(String username);

    // Counts number of people who like a given post given the url of the image in the post
    @Query("SELECT COUNT(l) FROM Post p JOIN p.likers l WHERE p.id = :postId")
    long countLikersByPostId(@Param("postId") Long postId);

    // Ids of users that the username follows
    Set<PersonFeed> findByFollowersUsername(String username);
}
