package com.hairpower.back.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(
                "AKIAWIJIUYTXLIFU5CVG", // ✅ Access Key
                "SE3IQAAp9yl3kDJoEcoJk0rV5tn5LIWw5lGc5zyl" // ✅ Secret Key
        );

        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2) // ✅ 한국(서울) 리전으로 변경
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
