package com.hairpower.back.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String gender;
    private String imageUrl; // S3에 업로드된 이미지 URL 저장

    @ElementCollection
    private List<String> userFeatures; // AI 분석 결과 저장

    public User(String gender) {
        //this.userId = UUID.randomUUID().toString(); // 랜덤 UUID 생성
        this.gender = gender;
    }
}

//package com.hairpower.back.user.model;

//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.List;
//import java.util.UUID;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class User {
//    @Id
//    private String userId; // 랜덤 ID
//
//    private String gender;
//    private String imageUrl; // S3 이미지 URL
//
//    @ElementCollection
//    private List<String> userFeatures; // AI 분석된 사용자 특징
//
//    public User(String gender) {
//        this.userId = UUID.randomUUID().toString(); // 랜덤 UUID 생성
//        this.gender = gender;
//    }
//}
//
