package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TweetRequestDto(
        @NotBlank(message = "Tweet içeriği boş olamaz")
        @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir")
        String content
) {
}
