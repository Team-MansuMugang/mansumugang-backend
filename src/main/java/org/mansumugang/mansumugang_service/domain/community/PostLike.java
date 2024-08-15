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
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 다른 테이블과의 관계

    // 1. 게시물
    @ManyToOne
    private Post post;

    // 2. 유저
    @ManyToOne
    private Protector protector;

    public static PostLike of(Post post, Protector protector){

        return PostLike.builder()
                .post(post)
                .protector(protector)
                .build();
    }
}
