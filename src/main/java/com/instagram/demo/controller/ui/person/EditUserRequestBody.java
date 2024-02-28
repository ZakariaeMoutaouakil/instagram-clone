package com.instagram.demo.controller.ui.person;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EditUserRequestBody(
        @NotNull
        @Size(min = 1, max = 20)
        String username,
        @NotNull
        String photo,
        @NotNull
        @Email
        String email,
        @NotNull
        @Size(min = 1, max = 20)
        String firstname,
        @NotNull
        @Size(min = 1, max = 20)
        String lastname,
        @NotNull
        @Size(max = 140)
        String bio
) {
}
