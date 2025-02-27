package com.hairpower.back;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())  // ✅ WebConfig 설정 유지
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (Postman 요청 가능하게)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chat/**").permitAll() // ✅ 채팅 엔드포인트 CORS 문제 해결
                        .anyRequest().permitAll() // 모든 요청 허용
                );

        return http.build();
    }
}
