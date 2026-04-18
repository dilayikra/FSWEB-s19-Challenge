package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.CommentPatchRequestDto;
import com.workintech.twitter.dto.request.CommentRequestDto;
import com.workintech.twitter.dto.response.CommentResponseDto;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;//commentle ilgili bütün işlemleri service katmanına gönderiyorum

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto save(@Validated @RequestBody CommentRequestDto commentRequestDto,
                                   @AuthenticationPrincipal User user) {

        //login olan kullanıcıyı spring securityden alıyorum
        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //comment oluşturma işlemini service e bırakıyorum, username i de gönderiyorum
        return commentService.save(commentRequestDto, user.getUsername());
    }

    @PutMapping("/{id}")
    public CommentResponseDto replace(@PathVariable Long id,
                                      @Validated @RequestBody CommentRequestDto commentRequestDto,
                                      @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //commenti tamamen güncelleme işlemi
        return commentService.replace(id, commentRequestDto, user.getUsername());
    }

    @PatchMapping("/{id}")
    public CommentResponseDto update(@PathVariable Long id,
                                     @Validated @RequestBody CommentPatchRequestDto patchDto,
                                     @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //commentin sadece belirli alanlarını güncelleme işlemi
        return commentService.update(id, patchDto, user.getUsername());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal User user) {

        System.out.println("Login olan kullanıcının id'si: " + user.getId());
        //comment silme işlemi
        commentService.delete(id, user.getUsername());
    }

    @GetMapping("/tweet/{tweetId}")
    public List<CommentResponseDto> findByTweetId(@PathVariable Long tweetId) {
        //belirli bir tweete ait commentleri getiriyorum
        return commentService.findByTweetId(tweetId);
    }
}
