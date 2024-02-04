package com.instagram.demo.data.projection;

import com.instagram.demo.data.schema.Comment;
import com.instagram.demo.data.schema.Person;

import java.util.Set;

public interface PostProjection {
    String getImage();
    Set<Person> getLikers();
    Set<Comment> getComments();
}
