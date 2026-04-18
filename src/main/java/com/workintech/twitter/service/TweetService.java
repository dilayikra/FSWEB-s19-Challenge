package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.TweetPatchRequestDto;
import com.workintech.twitter.dto.request.TweetRequestDto;
import com.workintech.twitter.dto.response.TweetResponseDto;

import java.util.List;

public interface TweetService {
    TweetResponseDto save(TweetRequestDto tweetRequestDto, String username);
    List<TweetResponseDto> findAllByUserId(Long userId);
    TweetResponseDto findById(Long id);
    TweetResponseDto update(Long id, TweetPatchRequestDto tweetPatchRequestDto, String username);
    TweetResponseDto replace(Long id, TweetRequestDto tweetRequestDto, String username);
    void delete(Long id, String username);

    TweetResponseDto retweet(Long originalTweetId, String username);
    void deleteRetweet(Long id, String username);
}