package com.instagram.demo.data.projection.post;

public interface PostFeedProjection extends PostProjection{
    String getUploaderUsername();
    Long getId();
    Boolean getUploaderValidated();
}
