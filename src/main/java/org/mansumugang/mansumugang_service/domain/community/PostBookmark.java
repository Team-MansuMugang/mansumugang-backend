package org.mansumugang.mansumugang_service.domain.community;


import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PostBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 다른 테이블과의 관계

    // 1. 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    // 2. 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private Protector protector;

    public static PostBookmark of(Post post, Protector protector){
        return PostBookmark.builder()
                .post(post)
                .protector(protector)
                .build();
    }
}
