package com.hairpower.back.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDto {
    private String userId;
    private String message;  // ❗️ message 필드 추가

    public ChatRequestDto(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }
}
