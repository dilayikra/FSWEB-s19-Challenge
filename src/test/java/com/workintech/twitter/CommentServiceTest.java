package com.workintech.twitter;

import com.workintech.twitter.dto.request.CommentRequestDto;
import com.workintech.twitter.dto.response.CommentResponseDto;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.service.CommentServiceImpl;
import com.workintech.twitter.util.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)//mockları aktif ediyorum
class CommentServiceTest {//comment servici test ediyorum

    @Mock//repositoryyi sahte yapıyorum
    private CommentRepository commentRepository;

    @Mock//tweet repo mock
    private TweetRepository tweetRepository;

    @Mock //user repo mock
    private UserRepository userRepository;

    @Mock//mapper mock
    private CommentMapper commentMapper;

    @InjectMocks//servicin içine mockları enjekte ediyorum
    private CommentServiceImpl commentService;

    @Test
    void shouldSaveCommentSuccessfully() {//yorum düzgün kaydediliyo mu test
        User user = new User(); //kullanıcı oluştur
        user.setUsername("dilayikra");

        Tweet tweet = new Tweet();//tweet oluştur
        tweet.setId(1L);

        CommentRequestDto requestDto = new CommentRequestDto("proje tamamlandı", 1L);//gelen request
        CommentResponseDto responseDto = new CommentResponseDto(
                1L,
                "proje tamamlandı",
                LocalDateTime.now(),
                "dilayikra",
                1L
        );

        when(userRepository.findByUsername("dilayikra")).thenReturn(Optional.of(user));//user bulundu
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(tweet)); // tweet bulundu
        when(commentMapper.toEntity(any(CommentRequestDto.class))).thenReturn(new Comment());//entityye çevir
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));//kaydet
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(responseDto);//response hazırla

        CommentResponseDto result = commentService.save(requestDto, "dilayikra");//servisi çalıştır

        assertNotNull(result); // null değil mi bakıyorum
        assertEquals("proje tamamlandı", result.content());//content doğru mu
        assertEquals("dilayikra", result.userName());//user doğru mu

        verify(userRepository).findByUsername("dilayikra");//user çağrıldı mı
        verify(tweetRepository).findById(1L);//tweet çağrıldı mı
        verify(commentRepository).save(any(Comment.class));//save oldu mu
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {//user yoksa hata veriyor mu
        CommentRequestDto requestDto = new CommentRequestDto("bu yorum gitmicek", 1L);

        when(userRepository.findByUsername("olmayan_kullanici")).thenReturn(Optional.empty());//user yok

        TwitterException exception = assertThrows(TwitterException.class, () -> {
            commentService.save(requestDto, "olmayan_kullanici");
        });


        assertEquals("user not found", exception.getMessage());//mesaj doğru mu
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());//status doğru mu
        verify(commentRepository, never()).save(any(Comment.class));//save olmamalı
    }

    @Test
    void shouldThrowExceptionWhenTweetNotFound() {//tweet yoksa hata veriyo mu
        User user = new User();
        user.setUsername("dilayikra");

        CommentRequestDto requestDto = new CommentRequestDto("Yorum", 99L);

        when(userRepository.findByUsername("dilayikra")).thenReturn(Optional.of(user));//user var
        when(tweetRepository.findById(99L)).thenReturn(Optional.empty());//tweet yok

        TwitterException exception = assertThrows(TwitterException.class, () -> {
            commentService.save(requestDto, "dilayikra");
        });


        assertEquals("tweet not found", exception.getMessage());//hata mesajı
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(commentRepository, never()).save(any(Comment.class));//save yok
    }

    @Test
    void shouldDeleteCommentSuccessfully() {//yorum silme başarılı mı
        Long commentId = 1L;
        String username = "dilayikra";

        Tweet tweet = new Tweet();
        tweet.setId(100L);

        User tweetOwner = new User();
        tweetOwner.setUsername("tweet_sahibi");
        tweet.setUser(tweetOwner);

        Comment comment = new Comment();
        comment.setId(commentId);

        User commentUser = new User();
        commentUser.setUsername(username);
        comment.setUser(commentUser);
        comment.setTweet(tweet);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));//comment bulundu

        commentService.delete(commentId, username);//silme işlemi

        verify(commentRepository, times(1)).delete(comment);//delete çağrıldı mı
    }

    @Test
    void shouldDeleteCommentWhenUserIsTweetOwner() {//tweet sahibi de silebiliyor mu
        Long commentId = 1L;
        String username = "tweet_sahibi";

        Tweet tweet = new Tweet();
        User tweetOwner = new User();
        tweetOwner.setUsername("tweet_sahibi");
        tweet.setUser(tweetOwner);

        Comment comment = new Comment();
        comment.setId(commentId);

        User commentOwner = new User();
        commentOwner.setUsername("yorum_sahibi");
        comment.setUser(commentOwner);
        comment.setTweet(tweet);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.delete(commentId, username);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void shouldThrowExceptionWhenUnauthorizedUserTriesToDeleteComment() {//yetkisiz silme denemesi
        Long commentId = 1L;
        String username = "yetkisiz_kullanici";

        Tweet tweet = new Tweet();
        User tweetOwner = new User();
        tweetOwner.setUsername("tweet_sahibi");
        tweet.setUser(tweetOwner);

        Comment comment = new Comment();
        comment.setId(commentId);

        User commentOwner = new User();
        commentOwner.setUsername("yorum_sahibi");
        comment.setUser(commentOwner);
        comment.setTweet(tweet);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));//comment var

        TwitterException exception = assertThrows(TwitterException.class, () -> {
            commentService.delete(commentId, username); //silmeye çalışıyo
        });


        assertEquals("bu yorumu silmeye yetkin yok", exception.getMessage());//hata mesajı
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(commentRepository, never()).delete(any(Comment.class));//silme olmamalı
    }
}