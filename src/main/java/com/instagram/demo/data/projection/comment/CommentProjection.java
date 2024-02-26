package com.instagram.demo.data.projection.comment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface CommentProjection {
    Long getId();

    LocalDateTime getDate();

    default long getTimeUntilNow() {
        return getDate().until(LocalDateTime.now(), ChronoUnit.DAYS);
    }

    String getComment();

    String getAuthorUsername();

    String getAuthorPhoto();
}
