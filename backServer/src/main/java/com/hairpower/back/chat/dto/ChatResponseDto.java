package com.hairpower.back.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatResponseDto {
    private String userId;
    private String response;
}
