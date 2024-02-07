package com.instagram.demo.data.projection.post;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface PostProjection {
    LocalDateTime getDate();

    default long getTimeUntilNow() {
        return getDate().until(LocalDateTime.now(), ChronoUnit.DAYS);
    }

    String getImage();

    String getDescription();

    String getUploaderPhoto();

}
