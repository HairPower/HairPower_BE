package com.hairpower.back.chat.service;

import com.hairpower.back.ai.service.AiService;
import com.hairpower.back.chat.dto.ChatRequestDto;
import com.hairpower.back.chat.dto.ChatResponseDto;
import com.hairpower.back.chat.model.Chat;
import com.hairpower.back.chat.repository.ChatRepository;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final AiService aiService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public ChatResponseDto chatWithAI(ChatRequestDto requestDto) {
        Long userId;
        try {
            userId = Long.parseLong(requestDto.getUserId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 userId 형식입니다.");
        }

        String message = requestDto.getMessage();

        // 사용자 정보 조회
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = userOptional.get();

        // AI와 통신하여 응답 받기
        String aiResponse = aiService.chatbotRespond(userId , message);

        // 채팅 로그 저장
        Chat chat = new Chat();
        chat.setUser(user);
        chatRepository.save(chat);

        return new ChatResponseDto(userId.toString(), aiResponse);
    }
}
