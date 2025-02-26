package com.hairpower.back.chat.service;

import com.hairpower.back.chat.model.Chat;
import com.hairpower.back.chat.repository.ChatRepository;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public Chat createChat(Long userId, String message, String response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Chat chat = new Chat();
        chat.setUser(user);
//        chat.setMessage(message);
//        chat.setResponse(response);

        return chatRepository.save(chat);
    }

    public List<Chat> getChatsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return chatRepository.findByUser(user);
    }
}
