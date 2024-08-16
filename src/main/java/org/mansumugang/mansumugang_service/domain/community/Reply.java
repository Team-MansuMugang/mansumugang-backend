package org.mansumugang.mansumugang_service.domain.community;

import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplySave;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // 다른 테이블과의 관계

    // 1. 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    Comment comment;

    // 2. 유저
    @ManyToOne(fetch = FetchType.LAZY)
    Protector protector;

    public static Reply of(ReplySave.Request request, Comment foundComment, Protector validProtector){
        return Reply.builder()
                .content(request.getContent())
                .comment(foundComment)
                .protector(validProtector)
                .build();
    }

    public void update(String content){
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete(){
        this.protector = null;
        this.content = "삭제된 대댓글 입니다.";
        this.deletedAt = LocalDateTime.now();
    }
}
