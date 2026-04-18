package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.CommentPatchRequestDto;
import com.workintech.twitter.dto.request.CommentRequestDto;
import com.workintech.twitter.dto.response.CommentResponseDto;
import com.workintech.twitter.entity.Comment;
import com.workintech.twitter.entity.Tweet;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.CommentNotFoundException;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.CommentRepository;
import com.workintech.twitter.repository.TweetRepository;
import com.workintech.twitter.repository.UserRepository;
import com.workintech.twitter.util.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto save(CommentRequestDto commentRequestDto, String username) {


        User user = userRepository.findByUsername(username)//kullanıcıyı username ile buluyorum
                .orElseThrow(() -> new TwitterException("user not found", HttpStatus.NOT_FOUND));

        //comment hangi tweete atılacak onu buluyorum
        Tweet tweet = tweetRepository.findById(commentRequestDto.tweetId())
                .orElseThrow(() -> new TwitterException("tweet not found", HttpStatus.NOT_FOUND));


        Comment comment = commentMapper.toEntity(commentRequestDto);//dto entity dönüşümü burda

        comment.setUser(user);
        comment.setTweet(tweet);

        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto replace(Long id, CommentRequestDto commentRequestDto, String username) {


        Comment comment = commentRepository.findById(id)//commenti idyle buluyorum
                .orElseThrow(() -> new CommentNotFoundException("comment not found with id: " + id));

        //sadece kendi commentini güncelleyebilir kontrolü
        if (!comment.getUser().getUsername().equals(username)) {
            throw new TwitterException("sadece kendi yorumunu güncelleyebilirsin", HttpStatus.FORBIDDEN);
        }


        comment.setContent(commentRequestDto.content());//tüm alanları güncelliyorum

        //tweet değişmiş mi kontrol ediyorum
        if (!comment.getTweet().getId().equals(commentRequestDto.tweetId())) {

            Tweet tweet = tweetRepository.findById(commentRequestDto.tweetId())
                    .orElseThrow(() -> new TwitterException("tweet not found", HttpStatus.NOT_FOUND));

            comment.setTweet(tweet);
        }

        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    public CommentResponseDto update(Long id, CommentPatchRequestDto patchDto, String username) {


        Comment comment = commentRepository.findById(id)//commenti buluyorum
                .orElseThrow(() -> new CommentNotFoundException("comment not found with id: " + id));

        //sadece sahibi update yapabilir
        if (!comment.getUser().getUsername().equals(username)) {
            throw new TwitterException("sadece kendi yorumunu güncelleyebilirsin", HttpStatus.FORBIDDEN);
        }

        //sadece gelen alanları değiştiriyorum
        if (patchDto.content() != null) {
            comment.setContent(patchDto.content());
        }

        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long id, String username) {


        Comment comment = commentRepository.findById(id)//commenti buluyorum
                .orElseThrow(() -> new CommentNotFoundException("comment not found with id: " + id));


        boolean isCommentOwner = comment.getUser().getUsername().equals(username);//ya comment sahibi ya da tweet sahibi silebilir
        boolean isTweetOwner = comment.getTweet().getUser().getUsername().equals(username);

        if (isCommentOwner || isTweetOwner) {
            commentRepository.delete(comment);
        } else {
            throw new TwitterException("bu yorumu silmeye yetkin yok", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public List<CommentResponseDto> findByTweetId(Long tweetId) {

        //tweet idye göre commentleri getiriyorum
        return commentRepository.findAllByTweetId(tweetId)
                .stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}