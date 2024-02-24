package com.instagram.demo.data.schema;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.FetchType.EAGER;
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
    @Size(max = 5)
    private Set<String> hashtags;

    @NotNull
    @NotEmpty
//    @Column(unique = true)
    private String image;

    @NotNull
    @CreationTimestamp
    @Past
    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne(fetch = EAGER, optional = false)
    private Person uploader;

    @ManyToMany(mappedBy = "likedPosts")
    private Set<Person> likers;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Post post = (Post) obj;
        return Objects.equals(id, post.id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", hashtags=" + hashtags +
                ", image='" + image + '\'' +
                ", date=" + date +
                ", uploader=" + uploader.getUsername() +
                ", likers=" + likers.size() +
                ", comments=" + comments.size() +
                '}';
    }
}
