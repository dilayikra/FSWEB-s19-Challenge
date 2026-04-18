package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    //kullanıcı id ve tweet idye göre daha önce like atılmış mı kontrol ediyorum
    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId);
}
