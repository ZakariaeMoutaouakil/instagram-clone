package com.instagram.demo.repository.schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 140)
    private String description;

//    private Long numberOfLikes;

    @ElementCollection
    private Set<String> hashtags;

    @NotNull
    @NotEmpty
    private String image;

    @ManyToOne(fetch = EAGER, optional = false)
    private Person uploader;

    @OneToMany(mappedBy = "likedPost")
    private Set<Person> likers;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;
}
