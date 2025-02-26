package com.hairpower.back.user.service;

import com.hairpower.back.s3.service.S3Service;
import com.hairpower.back.user.model.User;
import com.hairpower.back.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final S3Service s3Service;

    public User createUser(String gender, MultipartFile image) throws IOException {
        User user = new User(gender);

        // S3에 이미지 업로드 후 URL 저장
        String imageUrl = s3Service.uploadFile(image);
        user.setImageUrl(imageUrl);

        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }
}
