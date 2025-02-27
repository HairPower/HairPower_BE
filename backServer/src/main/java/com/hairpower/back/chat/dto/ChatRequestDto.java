package com.hairpower.back.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRequestDto {
    private String user_id;
    private String message;  // ❗️ message 필드 추가

    public ChatRequestDto(String user_id, String message) {
        this.user_id = user_id;
        this.message = message;
    }
}
