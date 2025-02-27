package com.hairpower.back.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private final String bucketName = "hairpower12"; // ✅ 버킷명 수정

    public String uploadFile(MultipartFile file) throws IOException {
        File convertedFile = convertMultiPartToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // ✅ ACL 제거
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));

        // ✅ 파일 URL 반환
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}
