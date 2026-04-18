package com.workintech.twitter.controller;

import com.workintech.twitter.dto.request.LoginRequestDto;
import com.workintech.twitter.dto.request.RegisterRequestDto;
import com.workintech.twitter.dto.response.RegisterResponseDto;
import com.workintech.twitter.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;//login/register işlemleri

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponseDto register(@Validated @RequestBody RegisterRequestDto registerRequestDto) {
        return authService.register(registerRequestDto);//kullanıcıyı kaydettiriyorum
    }

    @PostMapping("/login")
    public Map<String, String> login(@Validated @RequestBody LoginRequestDto loginRequestDto) {
        String token = authService.login(loginRequestDto.username(), loginRequestDto.password());//token üret
        return Map.of(
                "token", token,
                "message", "Giriş başarılı!"
        );
    }
}
