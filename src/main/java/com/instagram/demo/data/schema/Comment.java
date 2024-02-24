package com.instagram.demo.data.schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = EAGER, optional = false)
    private Person author;

    @NotNull
    @CreationTimestamp
    @Past
    private LocalDateTime date;

    @NotNull
    @Size(min = 1, max = 50)
    private String comment;

    @ManyToOne(fetch = EAGER, optional = false)
    private Post post;

    public Comment(Person author, String comment, Post post) {
        this.author = author;
        this.comment = comment;
        this.post = post;
        this.date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", author=" + author.getUsername() +
                ", date=" + date +
                ", comment='" + comment + '\'' +
                ", post=" + post.getId() +
                '}';
    }
}
