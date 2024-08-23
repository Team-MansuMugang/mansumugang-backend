package org.mansumugang.mansumugang_service.domain.community;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.dto.community.post.PostSave;
import org.mansumugang.mansumugang_service.dto.community.post.PostUpdate;
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
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시물 고유번호

    @Column(nullable = false)
    private String title; // 제목

    @Lob // 가변길이를 갖는 큰 데이터를 처리할 때 사용함.
    @Column(nullable = false)
    private String content; // 내용

    @CreatedDate
    private LocalDateTime createdAt; // 작성 시간

    @LastModifiedDate
    private LocalDateTime updatedAt; // 업데이트 시간

    // 다른테이블과의 연관 관계

    // 0. 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Protector protector;

    // 1. 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    private PostCategory postCategory;


    public static Post of(PostSave.Request request, PostCategory category ,Protector protector){

        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postCategory(category)
                .protector(protector)
                .build();
    }

    public void update(PostUpdate.Request request, PostCategory postCategory){
        this.title = request.getTitle();
        this.content = request.getContent();
        this.updatedAt = LocalDateTime.now();
        this.postCategory = postCategory;
    }


}
