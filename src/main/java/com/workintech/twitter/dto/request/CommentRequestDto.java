package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "Yorum içeriği boş olamaz")
        @Size(max = 200)
        String content,

        @NotNull(message = "Hangi tweete yorum yaptığınızı belirtmelisiniz")
        Long tweetId
) {
}
