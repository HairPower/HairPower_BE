package com.hairpower.back;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // ✅ 모든 엔드포인트 CORS 허용
                .allowedOriginPatterns(
                        "http://localhost:5173", // ✅ 개발 환경 (로컬)
                        "http://3.39.22.236:3000" // ✅ 배포된 프론트엔드
                ) // ✅ 와일드카드 대신 특정 Origin 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // ✅ 인증 정보 포함 허용 (JWT, 세션)
    }
}
