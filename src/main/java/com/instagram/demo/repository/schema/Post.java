package com.instagram.demo.repository.schema;

import jakarta.persistence.ElementCollection;
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
public class Post {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String description;

//    private Long numberOfLikes;

    @ElementCollection
    private Set<String> hashtags;

    private String image;

    @ManyToOne(fetch = LAZY)
    private Person uploader;

    @OneToMany(mappedBy = "likedPost")
    private Set<Person> likers;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;
}
