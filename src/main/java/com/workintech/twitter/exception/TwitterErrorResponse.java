package com.workintech.twitter.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TwitterErrorResponse {
    private String message;
    private int status;
    private long timestamp;
    private LocalDateTime localDateTime;
}
