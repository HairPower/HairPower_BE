package com.hairpower.back.ai.service;

import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;
    private final UserRepository userRepository; // ✅ 유저 DB 업데이트를 위한 Repository 추가

    private static final String AI_SERVER_URL = "https://14d7-34-90-160-86.ngrok-free.app/";

    // ✅ WebClient 요청 & 응답 로깅 필터 추가
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("📡 [AI 요청] {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("📡 [Header] {}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("📡 [AI 응답] HTTP Status={}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

    // ✅ AI 서버에 유저 정보 전송 (유저 생성 후 자동 실행)
    public void uploadPhotoToAI(String userId, String gender, String imageUrl) {
        Map<String, String> requestBody = Map.of(
                "user_id", userId,
                "gender", gender,
                "image_url", imageUrl
        );

        log.info("📡 AI 서버 요청 JSON: {}", requestBody);

        try {
            // AI 서버에 요청
            String response = webClient.post()
                    .uri(AI_SERVER_URL + "/upload-photo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 응답 로그
            log.info("📡 AI 서버 응답: {}", response);

            // AI 응답이 success일 경우, 후속 요청 진행
            if (response.contains("success")) {
                fetchUserFeaturesFromAI(userId);  // ✅ 성공 시 즉시 user_features 가져오기
            } else {
                log.warn("⚠️ AI 응답에서 success 메시지가 없음. 분석 중 오류 가능성 있음.");
                saveDefaultUserFeatures(userId); // ✅ 오류 발생 시 기본값 저장
            }

        } catch (Exception e) {
            log.error("❌ AI 서버 요청 중 오류 발생: {}", e.getMessage(), e);
            saveDefaultUserFeatures(userId); // ✅ 오류 발생 시 기본값 저장
        }
    }

    // ✅ AI 서버에서 사용자 특징 가져오기 (수정됨)
    public void fetchUserFeaturesFromAI(String userId) {
        String url = AI_SERVER_URL + "/select-story-image/" + userId;
        log.info("📡 AI 서버에서 사용자 특징 요청: URL={}", url);

        try {
            Map<String, List<String>> response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("📡 AI 서버 응답: {}", response);

            // 응답에서 user_features 추출
            List<String> userFeatures = response.getOrDefault("user_features", List.of());

            if (userFeatures.isEmpty()) {
                log.warn("⚠️ AI 서버 응답이 비어 있음. 기본값 저장.");
                saveDefaultUserFeatures(userId);
            } else {
                updateUserFeatures(userId, userFeatures);
            }

        } catch (Exception e) {
            log.error("❌ AI 서버에서 사용자 특징 요청 중 오류 발생: {}", e.getMessage(), e);
            saveDefaultUserFeatures(userId); // ✅ 오류 발생 시 기본값 저장
        }
    }

    // ✅ DB에 사용자 특징 업데이트
    private void updateUserFeatures(String userId, List<String> userFeatures) {
        log.info("📡 DB에 사용자 특징 업데이트: userId={}, userFeatures={}", userId, userFeatures);

        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        user.setUserFeatures(userFeatures);
        userRepository.save(user);

        log.info("✅ userFeatures가 성공적으로 업데이트되었습니다. userId={}", userId);
    }

    // ✅ AI 서버 오류 발생 시 기본값 저장
    private void saveDefaultUserFeatures(String userId) {
        List<String> defaultFeatures = Arrays.asList("세모형", "짧은 코", "긴 턱", "짧은 얼굴");

        log.info("⚠️ AI 서버 오류로 기본 userFeatures 저장. 기본값: {}", defaultFeatures);
        updateUserFeatures(userId, defaultFeatures);
    }

    // ✅ AI 챗봇 응답 받기 (수정됨)
    public String chatbotRespond(Long userId, String message) {
        String url = AI_SERVER_URL + "/chatbot/respond";

        Map<String, String> requestBody = Map.of(
                "user_id", String.valueOf(userId),
                "message", message
        );

        try {
            Map<String, String> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.info("📡 AI 챗봇 응답: {}", response);

            return response.getOrDefault("response", "응답 없음");
        } catch (Exception e) {
            log.error("❌ AI 챗봇 요청 중 오류 발생: {}", e.getMessage(), e);
            return "AI 응답을 가져오는 데 실패했습니다.";
        }

    }
}
