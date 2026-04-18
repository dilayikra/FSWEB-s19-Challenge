package com.workintech.twitter.service;

import com.workintech.twitter.dto.request.RegisterRequestDto;
import com.workintech.twitter.dto.response.RegisterResponseDto;
import com.workintech.twitter.entity.Role;
import com.workintech.twitter.entity.User;
import com.workintech.twitter.exception.UserAlreadyRegisteredException;
import com.workintech.twitter.exception.TwitterException;
import com.workintech.twitter.repository.RoleRepository;
import com.workintech.twitter.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SECRET_KEY = "twitter_clone_projesi_cok_gizli_anahtar_burada_durmali";

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        //kullanıcı daha önce kayıtlı mı diye kontrol ediyorum!!
        Optional<User> existingUser = userRepository.findByEmail(registerRequestDto.email());
        if (existingUser.isPresent()) {
            throw new UserAlreadyRegisteredException("Email already registered!");
        }

        if (userRepository.findByUsername(registerRequestDto.username()).isPresent()) {
            throw new UserAlreadyRegisteredException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequestDto.username());
        user.setEmail(registerRequestDto.email());
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));//şifreyi düz saklamıyorum, hashliyorum daha güvenli

        Role userRole = roleRepository.findByAuthority("ROLE_USER")
                .orElseThrow(() -> new TwitterException("Role not found", HttpStatus.INTERNAL_SERVER_ERROR));

        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);

        return new RegisterResponseDto(user.getUsername(), "Kullanıcı başarıyla kaydolmuştur.");
    }


    public String login(String username, String password) { //login işlemi kullanıcı var mı şifre doğru mu kontrolü!!
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new TwitterException("Geçersiz kullanıcı adı veya şifre", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(password, user.getPassword())) {//şifreyi karşılaştır
            throw new TwitterException("Geçersiz kullanıcı adı veya şifre", HttpStatus.UNAUTHORIZED);
        }

        return generateToken(username);
    }


    @Override //jwt token üretme
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override//token geçerli mi
    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}