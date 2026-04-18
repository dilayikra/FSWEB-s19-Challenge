package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.RegisterRequestDto;
import com.workintech.twitter.dto.response.RegisterResponseDto;

public interface AuthService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);


    String login(String username, String password);//login olup token alma işlemi oluyo

    String generateToken(String username);
    boolean validateToken(String token, String username);
    String extractUsername(String token);//token içinden username çekiyorum
}
