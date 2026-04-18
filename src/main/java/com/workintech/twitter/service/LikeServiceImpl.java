package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.LikeRequestDto;
import com.workintech.twitter.dto.response.LikeResponseDto;
import com.workintech.twitter.entity.Like;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.LikeNotFoundException;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.LikeRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.util.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final LikeMapper likeMapper;

    @Override
    public LikeResponseDto like(LikeRequestDto likeRequestDto, String username) {

        User user = userRepository.findByUsername(username)//kullanıcıyı username ile buluyorum
                .orElseThrow(() -> new TwitterException("User not found: " + username, HttpStatus.NOT_FOUND));

        //aynı tweeti daha önce like etmiş mi kontrol ediyorum yani double like engeli
        return likeRepository.findByUserIdAndTweetId(user.getId(), likeRequestDto.tweetId())
                .map(likeMapper::toResponseDto)
                .orElseGet(() -> {
                    //tweet var mı kontrol ediyorum
                    Tweet tweet = tweetRepository.findById(likeRequestDto.tweetId())
                            .orElseThrow(() -> new TwitterException("Tweet not found with id: " + likeRequestDto.tweetId(), HttpStatus.NOT_FOUND));

                    //yeni like oluşturuyorum
                    Like newLike = new Like();
                    newLike.setUser(user);
                    newLike.setTweet(tweet);

                    Like savedLike = likeRepository.save(newLike);
                    return likeMapper.toResponseDto(savedLike);
                });
    }

    @Override
    public void dislike(Long tweetId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TwitterException("User not found", HttpStatus.NOT_FOUND));

        //ike var mı kontrolü yapıyorum, yoksa LikeNotFoundException fırlatıcam
        Like like = likeRepository.findByUserIdAndTweetId(user.getId(), tweetId)
                .orElseThrow(() -> new LikeNotFoundException("Like record not found for tweet id: " + tweetId));

        likeRepository.delete(like);
    }
}
