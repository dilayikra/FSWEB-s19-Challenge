package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LikeRequestDto;
import com.workintech.twitter.dto.response.LikeResponseDto;

public interface LikeService {
    LikeResponseDto like(LikeRequestDto likeRequestDto, String username);
    void dislike(Long tweetId, String username);
}
