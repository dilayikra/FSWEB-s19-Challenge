package com.workintech.twitter.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record TweetResponseDto(
        Long id,
        String content,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("original_user_name")
        String originalUserName,
        @JsonProperty("is_retweet")
        boolean isRetweet,
        // Yeni eklenen dinamik alanlar:
        @JsonProperty("likeCount")
        int likeCount,
        @JsonProperty("commentCount")
        int commentCount,
        @JsonProperty("retweetCount")
        int retweetCount
) {
}