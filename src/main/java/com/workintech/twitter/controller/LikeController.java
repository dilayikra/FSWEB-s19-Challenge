package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.LikeRequestDto;
import com.workintech.twitter.dto.response.LikeResponseDto;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;//like işlemlerini service katmanına yönlendiriyorum

    @PostMapping("/like")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponseDto like(@Validated @RequestBody LikeRequestDto likeRequestDto,
                                @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //like atma işlemini service katmanına gönderiyorum
        return likeService.like(likeRequestDto, user.getUsername());
    }

    @PostMapping("/dislike/{tweetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dislike(@PathVariable Long tweetId,
                        @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //dislike yani like geri alma işlemi
        likeService.dislike(tweetId, user.getUsername());
    }
}