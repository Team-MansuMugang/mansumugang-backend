package org.mansumugang.mansumugang_service.domain.community;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageName;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    // 다른 테이블과의 관계
    // 1. 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public static PostImage of(String imageName, Post post){
        return PostImage.builder()
                .imageName(imageName)
                .post(post)
                .build();
    }
}
