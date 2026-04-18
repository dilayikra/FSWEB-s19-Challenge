package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RetweetRequestDto(
        @NotNull(message = "Retweet yapılacak Tweet ID boş olamaz")
        @Positive(message = "Geçersiz Tweet ID")
        Long originalTweetId
) {
}