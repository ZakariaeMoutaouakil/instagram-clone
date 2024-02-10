package com.instagram.demo.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;
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
    private boolean validated;

    @NotNull
    private String photo;

    @NotNull
    @Email
    @Column(unique = true)
    @JsonIgnore
    private String email;

    @NotNull
    @Size(min = 1, max = 20)
    @JsonIgnore
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

    @ManyToMany
    private Set<Person> followers;

    @ManyToMany(mappedBy = "followers")
    private Set<Person> followees;

    public Person(String username, String email, String password, String firstname, String lastname) {
        this.username = username;
        this.validated = false;
        this.photo = "";
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.bio = "";
    }
}
