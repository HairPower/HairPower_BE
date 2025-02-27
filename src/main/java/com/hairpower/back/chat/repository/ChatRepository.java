package com.hairpower.back.chat.repository;

import com.hairpower.back.chat.model.Chat;
import com.hairpower.back.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUser(User user); // userId 대신 User 객체 사용
}
