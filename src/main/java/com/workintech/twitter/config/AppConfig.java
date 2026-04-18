package com.workintech.twitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;//şifreyi hashlemek için bunu kullanıyorum
import org.springframework.security.crypto.password.PasswordEncoder; //şifreleme işlemleri için kullanılan interface!!

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}