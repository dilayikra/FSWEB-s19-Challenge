package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.TweetPatchRequestDto;
import com.workintech.twitter.dto.request.TweetRequestDto;
import com.workintech.twitter.dto.response.TweetResponseDto;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;//tweet ile ilgili bütün işlemleri service katmanına gönderiyorum


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto save(@Validated @RequestBody TweetRequestDto tweetRequestDto,
                                 @AuthenticationPrincipal User user) {

        //login olan kullanıcıyı spring securityden alıyorum
        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //tweet oluşturma işlemini service katmanına bırakıyorum
        return tweetService.save(tweetRequestDto, user.getUsername());
    }

    @PostMapping("/retweet/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto retweet(@PathVariable Long id,
                                    @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //retweet işlemini service katmanına gönderiyorum
        return tweetService.retweet(id, user.getUsername());
    }

    @DeleteMapping("/retweet/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRetweet(@PathVariable Long id,
                              @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //retweet silme işlemini service katmanına gönderiyorum
        tweetService.deleteRetweet(id, user.getUsername());
    }

    @GetMapping("/findByUserId/{userId}")
    public List<TweetResponseDto> findByUserId(@PathVariable Long userId) {
        //belirli bir kullanıcıya ait tweetleri getiriyorum
        return tweetService.findAllByUserId(userId);
    }

    @GetMapping("/findById/{id}")
    public TweetResponseDto findById(@PathVariable Long id) {

        //idye göre tek bi tweet getiriyorum
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    public TweetResponseDto replace(@PathVariable Long id,
                                    @Validated @RequestBody TweetRequestDto tweetRequestDto,
                                    @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        return tweetService.replace(id, tweetRequestDto, user.getUsername());
    }

    @PatchMapping("/{id}")
    public TweetResponseDto update(@PathVariable Long id,
                                   @Validated @RequestBody TweetPatchRequestDto tweetPatchRequestDto,
                                   @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        return tweetService.update(id, tweetPatchRequestDto, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //tweet silme işlemini service katmanına gönderiyorum
        tweetService.delete(id, user.getUsername());
    }
}