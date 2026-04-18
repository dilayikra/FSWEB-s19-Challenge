package com.workintech.twitter.security;

import com.workintech.twitter.service.AuthService;
import com.workintech.twitter.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor//constructorı otomatik oluşturuyorum
public class JwtAuthenticationFilter extends OncePerRequestFilter {//her requestte bir kere çalışsın diye

    private final AuthService authService;//token işlemleri için kullanıyorum
    private final UserDetailsServiceImpl userDetailsService;//kullanıcıyı çekmek için

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);//requestten token çekiyorum

        try {
            if (token != null) { // token varsa devam
                String username = authService.extractUsername(token);//token içinden username alıyorum

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    //eğer daha önce login edilmemişse

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);//kullanıcıyı getiriyorum

                    if (authService.validateToken(token, userDetails.getUsername())) {
                        //token doğru mu kontrol ediyorum

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );//authentication objesi oluşturuyorum

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        //kullanıcıyı sisteme login olmuş gibi kaydediyorum
                    }
                }
            }
        } catch (Exception ignored) {

        }

        filterChain.doFilter(request, response);//requesti devam ettiriyorum
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");//headerdan authorization alıyorum
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);//bearer kısmını atıp tokenı alıyorum
        }
        return null;//yoksa null dönücek
    }
}
