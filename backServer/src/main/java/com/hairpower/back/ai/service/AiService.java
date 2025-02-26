package com.hairpower.back.ai.service;

import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AiService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private static final String AI_SERVER_BASE_URL = "http://ai-server-url"; // AI 서버 주소

    // AI에 이미지 업로드 요청
    public String uploadPhotoToAI(User user) {
        String url = AI_SERVER_BASE_URL + "/upload-photo";

        Map<String, String> requestBody = Map.of(
                "user_id", user.getUserId(),
                "gender", user.getGender(),
                "image_string", user.getImageUrl()
        );

        Map<String, String> response = restTemplate.postForObject(url, requestBody, Map.class);
        return response != null ? response.get("message") : "Upload failed";
    }

    // AI에서 사용자 얼굴형 분석 결과 가져오기
    public List<String> fetchUserFeaturesFromAI(String userId) {
        String url = AI_SERVER_BASE_URL + "/select-story-image/" + userId;
        Map<String, List<String>> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("user_features")) {
            List<String> userFeatures = response.get("user_features");

            Optional<User> userOptional = userRepository.findById(userId);
            userOptional.ifPresent(user -> {
                user.setUserFeatures(userFeatures);
                userRepository.save(user);
            });

            return userFeatures;
        }
        return null;
    }

    // AI 분석 결과 가져오기 (DB 저장 없이 반환)
    public String getStoryResult(String userId) {
        String url = AI_SERVER_BASE_URL + "/get-story-result/" + userId;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("content")) {
            return "AI 분석 결과를 가져올 수 없습니다.";
        }

        Map<String, String> content = (Map<String, String>) response.get("content");
        return content.get("text");
    }

    // AI 챗봇 응답 요청
    public String chatbotRespond(String userId, String message) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "사용자를 찾을 수 없습니다.";
        }

        User user = userOptional.get();

        String url = AI_SERVER_BASE_URL + "/chatbot/respond";

        Map<String, Object> requestBody = Map.of(
                "user_id", userId,
                "message", message,
                "user_features", user.getUserFeatures()
        );

        Map<String, String> response = restTemplate.postForObject(url, requestBody, Map.class);
        return response != null ? response.get("response") : "AI 응답을 가져올 수 없습니다.";
    }
}
