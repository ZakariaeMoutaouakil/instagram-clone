package com.instagram.demo.data.projection;

import com.instagram.demo.data.schema.Comment;
import com.instagram.demo.data.schema.Person;

import java.time.LocalDateTime;
import java.util.Set;

public interface PostProjection {
    String getImage();
    LocalDateTime getDate();
}
