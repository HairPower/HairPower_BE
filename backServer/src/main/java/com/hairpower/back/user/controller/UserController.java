package com.hairpower.back.user.controller;

import com.hairpower.back.user.model.User;
import com.hairpower.back.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // ✅ 사용자 이미지 업로드 (S3 저장 후 URL 반환)
    @PostMapping("/upload-photo")
    public ResponseEntity<User> uploadPhoto(
            @RequestParam("gender") String gender,
            @RequestParam("image") MultipartFile image) throws IOException {

        User user = userService.createUser(gender, image);
        return ResponseEntity.ok(user);
    }


    // ✅ 사용자 특징 조회 API
    @GetMapping("/face/{userId}")
    public ResponseEntity<List<String>> getUserFeatures(@PathVariable Long userId) {
        log.info("📡 GET /face/{} 요청 수신", userId);

        List<String> userFeatures = userService.getUserFeatures(userId);

        return ResponseEntity.ok(userFeatures);
    }



//    // ✅ 유저 생성 후 AI 서버에 정보 보내기
//    @PostMapping("/send-ai/{userId}")
//    public ResponseEntity<String> sendUserInfoToAI(@PathVariable Long userId) {
//        String aiResponse = userService.sendUserInfoToAI(userId);
//        return ResponseEntity.ok(aiResponse);
//    }
}
