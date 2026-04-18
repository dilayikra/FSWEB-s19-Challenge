package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.CommentPatchRequestDto;
import com.workintech.twitter.dto.request.CommentRequestDto;
import com.workintech.twitter.dto.response.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto save(CommentRequestDto commentRequestDto, String username);
    CommentResponseDto update(Long id, CommentPatchRequestDto commentPatchRequestDto, String username);
    CommentResponseDto replace(Long id, CommentRequestDto commentRequestDto, String username);
    void delete(Long id, String username);
    List<CommentResponseDto> findByTweetId(Long tweetId);
}
