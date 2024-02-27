package com.instagram.demo.controller.ui.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Represents the request body for creating or editing a post.
 *
 * @param description The description of the post, limited to a maximum of 140 characters.
 * @param hashtags    An optional array of up to 5 hashtags associated with the post.
 * @param image       The image URL or path associated with the post, which must not be null or empty.
 */
public record RequestPostBody(
        @NotNull
        @Size(max = 140)
        String description,

        @Size(max = 5)
        String[] hashtags,

        @NotNull
        @NotEmpty
        String image
) {
}
