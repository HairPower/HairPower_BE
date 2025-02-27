package com.hairpower.back.ai.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    private final WebClient webClient;
    private final UserRepository userRepository; // ✅ 유저 DB 업데이트를 위한 Repository 추가
    private final ObjectMapper objectMapper = new ObjectMapper(); // ✅ Jackson ObjectMapper 추가


    private static final String AI_SERVER_URL = "https://07c7-35-240-236-97.ngrok-free.app";

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
                    .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {log.error("❌ AI 서버 오류: {}", errorBody);
                            return Mono.error(new RuntimeException("AI 서버 오류: " + errorBody));
                            })
                    )
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

    // ✅ AI 서버에서 사용자 특징 가져오기
    // ✅ AI 서버에서 사용자 특징 가져오기 (문제 해결)
    public void fetchUserFeaturesFromAI(String userId) {
        String url = AI_SERVER_URL + "/select-story-image/" + userId;
        log.info("📡 AI 서버에서 사용자 특징 요청: URL={}", url);

        try {
            // 1️⃣ **AI 서버 응답을 먼저 `String`으로 받음**
            String responseStr = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("📡 AI 서버 응답 (원본): {}", responseStr);

            // 2️⃣ **Jackson ObjectMapper를 사용하여 JSON 변환**
            Map<String, Object> response = objectMapper.readValue(responseStr, LinkedHashMap.class);

            log.info("📡 AI 서버 응답 (파싱된 JSON): {}", response);

            // 3️⃣ **유효성 검사 및 `user_features` 추출**
            if (response == null || !response.containsKey("user_features")) {
                log.warn("⚠️ AI 서버 응답이 비어 있음. 기본값 저장.");
                saveDefaultUserFeatures(userId);
                return;
            }

            // 4️⃣ **user_features를 `LinkedHashMap<String, String>`으로 변환 (순서 유지)**
            LinkedHashMap<String, String> userFeatures = objectMapper.convertValue(response.get("user_features"), LinkedHashMap.class);

            // 5️⃣ **DB 업데이트**
            updateUserFeatures(userId, userFeatures);

        } catch (Exception e) {
            log.error("❌ AI 서버에서 사용자 특징 요청 중 오류 발생: {}", e.getMessage(), e);
            saveDefaultUserFeatures(userId);
        }
    }

    // ✅ DB에 사용자 특징 업데이트 (JSON 대신 리스트로 저장)
    private void updateUserFeatures(String userId, Map<String, String> userFeatures) {
        log.info("📡 DB에 사용자 특징 업데이트: userId={}, userFeatures={}", userId, userFeatures);

        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // ✅ Map을 List<String>으로 변환 (키=값 형태로 리스트에 저장)
        List<String> featureList = userFeatures.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList();

        user.setUserFeatures(featureList);
        userRepository.save(user);

        log.info("✅ userFeatures가 리스트 형태로 성공적으로 업데이트되었습니다. userId={}", userId);
    }




    // ✅ AI 서버 오류 발생 시 기본값 저장 (순서 유지)
    private void saveDefaultUserFeatures(String userId) {
        LinkedHashMap<String, String> defaultFeatures = new LinkedHashMap<>(); // ✅ HashMap -> LinkedHashMap으로 변경
        defaultFeatures.put("forehead", "긴 이마");
        defaultFeatures.put("nose", "평범한 코");
        defaultFeatures.put("chin", "짧은 턱");
        defaultFeatures.put("eye_mid", "평범한 미간");
        defaultFeatures.put("vertical", "짧은 얼굴");
        defaultFeatures.put("shape", "각진형");

        log.info("⚠️ AI 서버 오류로 기본 userFeatures 저장. 기본값: {}", defaultFeatures);
        updateUserFeatures(userId, defaultFeatures); // ✅ 타입이 일치하도록 변경
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
    // ✅ AI 서버에서 헤어 스타일 추천 결과 가져오기
    public ResponseEntity<AiStoryResponse> getStoryResult(Long userId) {
        String url = AI_SERVER_URL + "/get-story-result/" + userId;
        log.info("📡 AI 서버에서 스타일 추천 결과 요청: URL={}", url);

        try {
            AiStoryResponse response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(AiStoryResponse.class)
                    .block();

            log.info("📡 AI 서버 응답: {}", response);

            // 응답이 null이거나 필수 필드가 없으면 204 No Content 반환
            if (response == null || response.getContent() == null || response.getContent().getText() == null) {
                log.warn("⚠️ AI 서버 응답이 유효하지 않음. 기본 메시지 반환.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(response); // JSON 형식으로 그대로 반환

        } catch (Exception e) {
            log.error("❌ AI 서버에서 스타일 추천 요청 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Getter
    public static class AiStoryResponse {
        @JsonProperty("description")
        private String description;

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("user_features")
        private String userFeatures;

        @JsonProperty("question")
        private String question;

        @JsonProperty("content")
        private AiStoryContent content;
    }

    @Getter
    private static class AiStoryContent {
        @JsonProperty("type")
        private String type;

        @JsonProperty("text")
        private String text;
    }
}
