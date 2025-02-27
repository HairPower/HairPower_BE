package com.hairpower.back.user.service;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.s3.service.S3Service;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service; // ✅ S3 업로드 서비스 추가
    private final AiService aiService; // ✅ AI 서비스 추가

    // ✅ 사용자의 ID로 조회하는 메서드
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));
    }

    // ✅ 유저 생성 (S3 업로드 후 저장)
    public User createUser(String gender, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image); // ✅ S3 업로드

        User user = new User();
        user.setGender(gender);
        user.setImageUrl(imageUrl);
        user = userRepository.save(user);
        log.info("✅ 사용자 저장 완료: userId={}", user.getUserId());

        // ✅ AI 서버에 자동으로 요청 보내기 (이제 AI 서버에서 user_features 저장)
        sendUserInfoToAI(user);

        return user;
    }

    // ✅ AI 서버에 사용자 정보 전송 (유저 생성 이후)
    public void sendUserInfoToAI(User user) {
        log.info("📡 AI 서버 전송 시작... userId={}", user.getUserId());

        try {
            aiService.uploadPhotoToAI(
                    user.getUserId().toString(),
                    user.getGender(),
                    user.getImageUrl()
            );

            log.info("📡 AI 서버 요청 완료. (user_features 업데이트는 AiService에서 수행)");
        } catch (Exception e) {
            log.error("❌ AI 서버 요청 실패: {}", e.getMessage(), e);
        }
    }

    /// ✅ 사용자 특징 조회 (List<String>을 Map<String, String>으로 변환)
    public Map<String, String> getUserFeatures(Long userId) {
        log.info("📡 사용자 특징 조회 요청: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        List<String> featureList = user.getUserFeatures();

        // ✅ List<String> → Map<String, String> 변환
        Map<String, String> featureMap = featureList.stream()
                .map(entry -> entry.split(": ")) // "chin: 짧은 턱" → ["chin", "짧은 턱"]
                .filter(arr -> arr.length == 2) // key-value가 정상적으로 분리된 것만 처리
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));

        log.info("✅ userId={} 의 특징 조회 완료: {}", userId, featureMap);

        return featureMap;
    }


}
