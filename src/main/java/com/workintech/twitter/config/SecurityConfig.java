package com.workintech.twitter.config;

import com.workintech.twitter.security.JwtAuthenticationFilter; //JWT kontrolü yapan custom filter bu
import lombok.RequiredArgsConstructor; // final field'lar için constructor üretir (DI için)
import org.springframework.context.annotation.Bean; // Bean tanımlamak için
import org.springframework.context.annotation.Configuration; // Config class olduğunu belirtir
import org.springframework.http.HttpMethod; // HTTP methodlarını (GET, POST vs.) kullanmak için
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Security ayarlarını yapmak için
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Spring Security'yi aktif eder
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // csrf disable için
import org.springframework.security.config.http.SessionCreationPolicy; // session yönetimi için
import org.springframework.security.web.SecurityFilterChain; // Security filter zinciri
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // default login filter
import org.springframework.web.cors.CorsConfiguration; // CORS ayarları için
import org.springframework.web.cors.CorsConfigurationSource; // CORS kaynağı
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // URL bazlı CORS config

import java.util.Arrays; // Liste oluşturmak için
import java.util.List; // Liste tipi

@Configuration
@EnableWebSecurity//Spring Security aktif ediliyo
@RequiredArgsConstructor//final değişkenler için constructor oluşturur (dependency injection)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //JWT filter'ı Spring otomatik inject eder (dependancy injection oluyo)

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {//"No 'Access-Control-Allow-Origin' header is present" hatasını engellemeliyim
        CorsConfiguration configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(List.of("http://localhost:3200"));


        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));


        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));


        configuration.setExposedHeaders(List.of("x-auth-token"));


        configuration.setAllowCredentials(true);
        //cookie ve authorization bilgisi gönderimine izin

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //bu ayarları url lere bağlayan yapı

        source.registerCorsConfiguration("/**", configuration);
        //tüm endpointlere bu cors ayarını uygulattırıyorum

        return source; //bean olarak döndür
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CORS ayarını aktif et

                .csrf(AbstractHttpConfigurer::disable)
                //csrf:Cross-Site Request Forgery,bir kullanıcının bilgisi olmadan başka bir site üzerinden yetkili işlem yaptırılması demek

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Session kullanılmaz (JWT kullandığımız için)

                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**").permitAll()
                                // auth endpoint'leri herkese açık (login/register)

                                .requestMatchers(HttpMethod.GET, "/tweet/findByUserId/**").permitAll()
                                // Bu GET endpoint herkese açık

                                .requestMatchers(HttpMethod.GET, "/tweet/findById/**").permitAll()
                                // Bu GET endpoint herkese açık

                                .requestMatchers(HttpMethod.GET, "/comment/tweet/**").permitAll()
                                // Bu GET endpoint herkese açık

                                .anyRequest().authenticated()
                        // Diğer tüm istekler login gerektirir
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //JWT filter default login filterdan önce çalışıyo! token kontrolü!

                .build();
    }
}