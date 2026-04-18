package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.Size;

public record TweetPatchRequestDto(
        @Size(max = 280)
        String content
) {
}
