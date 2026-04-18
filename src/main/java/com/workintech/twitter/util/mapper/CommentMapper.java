package com.workintech.twitter.util.mapper;

import com.workintech.twitter.dto.request.CommentRequestDto;
import com.workintech.twitter.dto.response.CommentResponseDto;
import com.workintech.twitter.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    //entity yi dto çeviriyorum (frontende gidicek format)
    public CommentResponseDto toResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getUsername(),
                comment.getTweet().getId()
        );
    }

    // dtoyu entity ye çeviriyorum (veritabanına kaydedeceğim format)
    public Comment toEntity(CommentRequestDto commentRequestDto) {

        Comment comment = new Comment();

        //sadece content bilgisini alıp entity ye koyuyorum
        comment.setContent(commentRequestDto.content());

        return comment;
    }
}
