package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikeRequestDto(
        @NotNull(message = "Tweet ID boş olamaz")
        @Positive(message = "Geçersiz Tweet ID")
        Long tweetId
) {
}
