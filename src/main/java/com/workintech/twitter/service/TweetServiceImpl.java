package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.TweetPatchRequestDto;
import com.workintech.twitter.dto.request.TweetRequestDto;
import com.workintech.twitter.dto.response.TweetResponseDto;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.TweetNotFoundException;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.util.mapper.TweetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;

    @Override
    public TweetResponseDto save(TweetRequestDto tweetRequestDto, String username) {

        //kullanıcıyı username ile buluyorum
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TwitterException("user not found", HttpStatus.NOT_FOUND));


        Tweet tweet = tweetMapper.toEntity(tweetRequestDto);//dto entity dönüşümüü
        tweet.setUser(user);

        //tweeti kaydediyorum
        Tweet savedTweet = tweetRepository.save(tweet);

        return convertToResponseDto(savedTweet);
    }

    @Override
    public List<TweetResponseDto> findAllByUserId(Long userId) {

        //kullanıcıya ait tweetleri tarihe göre getiriyorum
        return tweetRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TweetResponseDto findById(Long id) {

        //tweeti id ile buluyorum
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("tweet not found with id: " + id));

        return convertToResponseDto(tweet);
    }

    @Override
    public TweetResponseDto replace(Long id, TweetRequestDto tweetRequestDto, String username) {

        //tweeti buluyorum
        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("tweet not found"));


        if (!existingTweet.getUser().getUsername().equals(username)) {//sadece sahibi güncelleyebilir
            throw new TwitterException("unauthorized", HttpStatus.FORBIDDEN);
        }

        //komple güncelleme
        existingTweet.setContent(tweetRequestDto.content());

        Tweet updatedTweet = tweetRepository.save(existingTweet);

        return convertToResponseDto(updatedTweet);
    }

    @Override
    public TweetResponseDto update(Long id, TweetPatchRequestDto tweetPatchRequestDto, String username) {


        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("tweet not found"));

        //sadece sahibi update yapabilir
        if (!existingTweet.getUser().getUsername().equals(username)) {
            throw new TwitterException("unauthorized", HttpStatus.FORBIDDEN);
        }


        tweetMapper.updateEntity(existingTweet, tweetPatchRequestDto);//sadece dolu alanları güncelliyorum

        Tweet updatedTweet = tweetRepository.save(existingTweet);

        return convertToResponseDto(updatedTweet);
    }

    @Override
    public void delete(Long id, String username) {


        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("tweet not found"));

        //sadece sahibi silebilir
        if (!tweet.getUser().getUsername().equals(username)) {
            throw new TwitterException("unauthorized", HttpStatus.FORBIDDEN);
        }

        tweetRepository.delete(tweet);
    }

    @Override
    @Transactional
    public TweetResponseDto retweet(Long originalTweetId, String username) {

        //kullanıcıyı buluyorum
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TwitterException("user not found", HttpStatus.NOT_FOUND));

        //orijinal tweeti buluyorum
        Tweet originalTweet = tweetRepository.findById(originalTweetId)
                .orElseThrow(() -> new TweetNotFoundException("tweet not found"));


        Tweet tweetToRetweet = originalTweet.getParentTweet() != null//retweet zinciri varsa en üst tweeti alıyorum
                ? originalTweet.getParentTweet()
                : originalTweet;

        //aynı tweeti tekrar retweet etmiş mi kontrol ediyorum
        tweetRepository.findByUserIdAndParentTweetId(user.getId(), tweetToRetweet.getId())
                .ifPresent(existing -> {
                    throw new TwitterException("already retweeted", HttpStatus.CONFLICT);
                });

        //yeni retweet oluşturuyorum
        Tweet retweet = new Tweet();
        retweet.setContent(null);
        retweet.setUser(user);
        retweet.setParentTweet(tweetToRetweet);

        Tweet savedRetweet = tweetRepository.save(retweet);

        return convertToResponseDto(savedRetweet);
    }

    @Override
    public void deleteRetweet(Long id, String username) {

        //retweeti buluyorum
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new TweetNotFoundException("retweet not found"));

        //sadece sahibi silebilir
        if (!tweet.getUser().getUsername().equals(username)) {
            throw new TwitterException("unauthorized", HttpStatus.FORBIDDEN);
        }

        //retweet mi kontrolü
        if (tweet.getParentTweet() == null) {
            throw new TwitterException("not a retweet", HttpStatus.BAD_REQUEST);
        }

        tweetRepository.delete(tweet);
    }

    private TweetResponseDto convertToResponseDto(Tweet tweet) {
        return tweetMapper.toResponseDto(tweet);
    }
}