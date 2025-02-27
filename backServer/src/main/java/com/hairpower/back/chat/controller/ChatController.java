package com.hairpower.back.chat.controller;

import com.hairpower.back.chat.dto.ChatRequestDto;
import com.hairpower.back.ai.service.AiService; // ✅ AiService 추가
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final AiService aiService; // ✅ UserService → AiService로 변경

    // ✅ AI 분석 시작 (기본값 적용)
    @PostMapping("/start-analysis/{userId}")
    public ResponseEntity<String> startAiAnalysis(@PathVariable String userId) {
        aiService.fetchUserFeaturesFromAI(userId); // ✅ AiService에서 직접 호출
        return ResponseEntity.ok("AI 분석 완료 및 userFeatures 저장됨.");
    }

    // ✅ AI 챗봇 응답 받기
    @PostMapping("/ask-ai")
    public ResponseEntity<String> askAi(@RequestBody ChatRequestDto requestDto) {
        return ResponseEntity.ok("AI 응답 예제");
    }
}
