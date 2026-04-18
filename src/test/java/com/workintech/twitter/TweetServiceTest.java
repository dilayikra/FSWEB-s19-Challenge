package com.workintech.twitter;

import com.workintech.twitter.dto.request.TweetRequestDto;
import com.workintech.twitter.dto.response.TweetResponseDto;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TweetNotFoundException;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.service.TweetServiceImpl;
import com.workintech.twitter.util.mapper.TweetMapper;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)// mockları açıyorum
class TweetServiceTest {

    @Mock //tweet repo sahte
    private TweetRepository tweetRepository;

    @Mock //user repo sahte
    private UserRepository userRepository;

    @Mock //mapper sahte
    private TweetMapper tweetMapper;

    @InjectMocks // ervice içine mockları basıyorum
    private TweetServiceImpl tweetService;

    @Test
    @DisplayName("tweet başarıyla kaydedilmeli")
    void shouldSaveTweetSuccessfully() { //tweet atma testi
        User user = new User(); //user oluşturuyorum
        user.setUsername("dilayikra");

        TweetRequestDto requestDto = new TweetRequestDto("final projesi testi! 🚀"); //gelen veri

        TweetResponseDto responseDto = new TweetResponseDto(
                1L,
                "final projesi testi! 🚀",
                LocalDateTime.now(),
                "dilayikra",
                null,
                false,
                0,
                0,
                0
        );

        when(userRepository.findByUsername("dilayikra")).thenReturn(Optional.of(user)); //user var
        when(tweetMapper.toEntity(any(TweetRequestDto.class))).thenReturn(new Tweet()); //entity çevir
        when(tweetRepository.save(any(Tweet.class))).thenAnswer(i -> i.getArgument(0)); //kaydet
        when(tweetMapper.toResponseDto(any(Tweet.class))).thenReturn(responseDto); //response hazırla

        TweetResponseDto result = tweetService.save(requestDto, "dilayikra"); //çalıştır

        assertNotNull(result); // boş mu değil mi bak
        assertEquals("dilayikra", result.userName()); // user doğru mu
        assertEquals("final projesi testi! 🚀", result.content());//içerik doğru mu

        verify(tweetRepository, times(1)).save(any(Tweet.class)); //save olmuş mu
    }

    @Test
    @DisplayName("kullanıcı yoksa hata fırlatmalı")
    void shouldThrowExceptionWhenUserNotFound() { //user yok testi
        TweetRequestDto requestDto = new TweetRequestDto("bu tweet atılamayacak");

        when(userRepository.findByUsername("olmayan_user")).thenReturn(Optional.empty()); //user yok

        TwitterException exception = assertThrows(TwitterException.class, () -> {
            tweetService.save(requestDto, "olmayan_user");
        });

        // Servis katmanına göre "user not found" olarak eşitledik
        assertEquals("user not found", exception.getMessage()); //mesaj kontrol
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(tweetRepository, never()).save(any(Tweet.class)); //save olmamalı
    }

    @Test
    @DisplayName("tweet silme başarılı olmalı")
    void shouldDeleteTweetSuccessfully() { //tweet silme testi
        Long tweetId = 1L;
        String username = "dilayikra";

        Tweet tweet = new Tweet();
        tweet.setId(tweetId);

        User user = new User();
        user.setUsername(username);
        tweet.setUser(user);

        when(tweetRepository.findById(tweetId)).thenReturn(Optional.of(tweet));//tweet bulundu

        tweetService.delete(tweetId, username); // sil

        verify(tweetRepository, times(1)).delete(tweet);//delete çağrıldı mı
    }

    @Test
    @DisplayName("yetkisiz kullanıcı silememeli")
    void shouldThrowExceptionWhenUnauthorizedUserTriesToDeleteTweet() {//yetkisiz silme
        Long tweetId = 1L;
        String username = "yetkisiz_kullanici";

        Tweet tweet = new Tweet();
        tweet.setId(tweetId);

        User owner = new User();
        owner.setUsername("gercek_sahip");
        tweet.setUser(owner);

        when(tweetRepository.findById(tweetId)).thenReturn(Optional.of(tweet));//tweet var

        TwitterException exception = assertThrows(TwitterException.class, () -> {
            tweetService.delete(tweetId, username); // silmeye çalışıyo
        });


        assertEquals("unauthorized", exception.getMessage()); //hata mesajı
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(tweetRepository, never()).delete(any(Tweet.class));//silme yok
    }

    @Test
    @DisplayName("tweet yoksa silerken hata vermeli")
    void shouldThrowExceptionWhenTweetNotFoundOnDelete() {//tweet yok testi
        Long tweetId = 99L;
        String username = "dilayikra";

        when(tweetRepository.findById(tweetId)).thenReturn(Optional.empty());//tweet yok

        TweetNotFoundException exception = assertThrows(TweetNotFoundException.class, () -> {
            tweetService.delete(tweetId, username);
        });


        assertEquals("tweet not found", exception.getMessage());//hata kontrol
        verify(tweetRepository, never()).delete(any(Tweet.class));//delete yok
    }
}