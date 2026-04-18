package com.workintech.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        String content,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("tweet_id")
        Long tweetId
) {
}
