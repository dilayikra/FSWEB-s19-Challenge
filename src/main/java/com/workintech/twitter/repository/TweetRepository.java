package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TweetRepository extends JpaRepository<Tweet, Long> {

    //kullanıcı idsine göre tweetleri getiriyorum en yeni tweet en üstte olucak şekilde
    List<Tweet> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    //bi kullanıcının belirli bir tweeti retweet edip etmediğini kontrol ediyorum
    Optional<Tweet> findByUserIdAndParentTweetId(Long userId, Long parentTweetId);
}
