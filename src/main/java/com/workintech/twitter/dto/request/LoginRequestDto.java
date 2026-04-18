package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Username boş olamaz")
        String username,

        @NotBlank(message = "Password boş olamaz")
        String password
) {
}
