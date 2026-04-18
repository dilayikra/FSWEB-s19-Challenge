package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "Username cannot be blank")
        @Size(max = 50)
        String username,

        @Email(message = "Please provide a valid email")
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
