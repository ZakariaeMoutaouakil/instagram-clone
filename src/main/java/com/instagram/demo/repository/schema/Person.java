package com.instagram.demo.repository.schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String username;

    private String firstname;

    private String lastname;

    private String bio;

    @OneToMany(mappedBy = "uploader")
    private Set<Post> uploads;

    @ManyToOne(fetch = LAZY)
    private Post likedPost;

    @OneToMany(mappedBy = "author")
    private Set<Comment> comments;
//    private Integer numberOfPosts;
//
//    private Long numberOfFollowers;
//
//    private Integer numberOfFollowees;
}
