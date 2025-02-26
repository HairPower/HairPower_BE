package com.hairpower.back.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final String BUCKET_NAME = "your-s3-bucket-name";

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(BUCKET_NAME, fileName, file.getInputStream(), metadata);
        return amazonS3.getUrl(BUCKET_NAME, fileName).toString();
    }
}
