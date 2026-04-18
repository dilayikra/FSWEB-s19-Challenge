package com.workintech.twitter.dto.request;

import jakarta.validation.constraints.Size;

public record CommentPatchRequestDto(
        @Size(max = 200, message = "Yorum içeriği en fazla 200 karakter olabilir")
        String content
) {
}
