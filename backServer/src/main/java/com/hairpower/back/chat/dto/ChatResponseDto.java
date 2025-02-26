package com.hairpower.back.chat.dto;

import com.hairpower.back.chat.model.Chat;
import lombok.Getter;

@Getter
public class ChatResponseDto {
    private Long chatId;
    private Long userId;

    public ChatResponseDto(Chat chat) {
        this.chatId = chat.getChatId();
        this.userId = chat.getUser().getUserId(); // user 객체에서 userId 가져오기
    }
}
