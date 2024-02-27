package com.instagram.demo.controller.ui.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestPostBody(
        @NotNull
        @Size(max = 140)
        String description,

        @Size(max = 5)
        String[] hashtags,

        @NotEmpty
        String image
) {
}
