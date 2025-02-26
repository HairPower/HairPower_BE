package com.hairpower.back.chat.dto;

import lombok.Getter;

@Getter
public class ChatRequestDto {
    private Long userId;
    private String message;
}
