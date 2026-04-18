package com.workintech.twitter.util.mapper;

import com.workintech.twitter.dto.request.TweetPatchRequestDto;
import com.workintech.twitter.dto.request.TweetRequestDto;
import com.workintech.twitter.dto.response.TweetResponseDto;
import com.workintech.twitter.entity.Tweet;
import org.springframework.stereotype.Component;

@Component
public class TweetMapper {
    public TweetResponseDto toResponseDto(Tweet tweet) {//entity yi dto ya çeviriyorum (frontende tweeti düzgün formatta göndermek için)
        boolean isRetweet = tweet.getParentTweet() != null;
        String originalUsername = isRetweet ? tweet.getParentTweet().getUser().getUsername() : null;

        return new TweetResponseDto(
                tweet.getId(),
                tweet.getContent(),
                tweet.getCreatedAt(),
                tweet.getUser().getUsername(),
                originalUsername,
                isRetweet,
                tweet.getLikes() != null ? tweet.getLikes().size() : 0,    // likeCount
                tweet.getComments() != null ? tweet.getComments().size() : 0, // commentCount
                tweet.getRetweets() != null ? tweet.getRetweets().size() : 0  // retweetCount
        );
    }

    public Tweet toEntity(TweetRequestDto tweetRequestDto) {//dto yu entity e çeviriyorum (yeni tweet oluştururken)
        Tweet tweet = new Tweet();
        tweet.setContent(tweetRequestDto.content());
        return tweet;
    }

    public void updateEntity(Tweet tweetToUpdate, TweetPatchRequestDto tweetPatchRequestDto) {//patch update yaparken sadece dolu alanları güncelliyorum
        if (tweetPatchRequestDto.content() != null) {
            tweetToUpdate.setContent(tweetPatchRequestDto.content());
        }
    }
}
