package com.workintech.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LikeResponseDto(
        Long id,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("tweet_id")
        Long tweetId
) {
}