package com.hairpower.back.face.model;

import com.hairpower.back.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FaceAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faceId;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    private String faceShape;
}
