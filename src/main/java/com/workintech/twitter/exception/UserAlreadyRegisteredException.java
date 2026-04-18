package com.workintech.twitter.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends TwitterException {
    public UserAlreadyRegisteredException(String message) {
        super(message, HttpStatus.CONFLICT); //409 conflict hatası döndürür
    }
}