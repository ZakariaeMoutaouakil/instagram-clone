package com.instagram.demo.repository.schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;
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

    @NotNull
    @Size(min = 1, max = 20)
    @Column(unique = true)
    private String username;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 1, max = 20)
    private String password;

    @NotNull
    @Size(min = 1, max = 20)
    private String firstname;

    @NotNull
    @Size(min = 1, max = 20)
    private String lastname;

    @NotNull
    @Size(max = 140)
    private String bio;

    @OneToMany(mappedBy = "uploader")
    private Set<Post> uploads;

    @ManyToOne(fetch = EAGER)
    private Post likedPost;

    @OneToMany(mappedBy = "author")
    private Set<Comment> comments;
//    private Integer numberOfPosts;
//
//    private Long numberOfFollowers;
//
//    private Integer numberOfFollowees;
}
