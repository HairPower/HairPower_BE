package com.hairpower.back.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.github.cdimascio.dotenv.Dotenv;  // 추가된 라이브러리
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);
    private final Dotenv dotenv;

    public S3Config() {
        // ✅ .env 파일 로드
        dotenv = Dotenv.configure().ignoreIfMissing().load();
        logger.info("🔍 직접 확인 - Dotenv AWS_ACCESS_KEY: {}", dotenv.get("AKIAUQ4L22I5OQSDABHG"));
        logger.info("🔍 직접 확인 - Dotenv AWS_REGION: {}", dotenv.get("ap-northeast-2"));
    }

    @Bean
    public AmazonS3 amazonS3() {
        // ✅ 환경 변수 로드
        String accessKey = dotenv.get("AKIAWIJIUYTXLIFU5CVG");
        String secretKey = dotenv.get("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");
        String region = dotenv.get("ap-northeast-2");

        if (accessKey == null || secretKey == null || region == null) {
            logger.error("❌ AWS 환경 변수가 설정되지 않았습니다! 서버 실행 전에 환경 변수를 설정하세요.");
            throw new IllegalStateException("❌ AWS 환경 변수가 설정되지 않았습니다! 서버 실행 전에 환경 변수를 설정하세요.");
        }

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.fromName(region))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        logger.info("✅ AmazonS3 Bean 생성 완료!");
        return s3Client;
    }
}
