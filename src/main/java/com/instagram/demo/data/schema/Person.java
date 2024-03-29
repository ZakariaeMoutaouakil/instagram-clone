package com.instagram.demo.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
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
    @NotEmpty
    @NotBlank
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

    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> uploads;

    @ManyToMany(fetch = EAGER)
    private Set<Post> likedPosts;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(id, person.id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", validated=" + validated +
                ", photo='" + photo + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
