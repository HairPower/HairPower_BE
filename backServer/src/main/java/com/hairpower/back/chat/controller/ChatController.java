package com.hairpower.back.chat.controller;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.chat.dto.ChatRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AiService aiService;

    // AI 분석 시작
    @PostMapping("/start-analysis/{userId}")
    public ResponseEntity<String> startAiAnalysis(@PathVariable Long userId) {
        String response = aiService.uploadPhotoToAI(userId);
        return ResponseEntity.ok(response);
    }

    // AI로부터 사용자 특징 받아서 저장
    @GetMapping("/fetch-user-features/{userId}")
    public ResponseEntity<List<String>> fetchUserFeatures(@PathVariable Long userId) {
        List<String> features = aiService.fetchUserFeaturesFromAI(userId);
        return ResponseEntity.ok(features);
    }

    // AI 분석 결과 확인 (DB 저장 없이 프론트에 바로 반환)
    @GetMapping("/get-ai-result/{userId}")
    public ResponseEntity<String> getAiResult(@PathVariable Long userId) {
        String response = aiService.getStoryResult(userId);
        return ResponseEntity.ok(response);
    }

    // 사용자 질문 → AI 질문 → 응답 반환
    @PostMapping("/ask-ai")
    public ResponseEntity<String> askAi(@RequestBody ChatRequestDto requestDto) {
        String response = aiService.chatbotRespond(requestDto.getUserId(), requestDto.getMessage());
        return ResponseEntity.ok(response);
    }
}
