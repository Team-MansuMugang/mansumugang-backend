package org.mansumugang.mansumugang_service.dto.community.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostImage;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class PostInquiry {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostListResponse{

        private PostUtil.PostListMetaData metaData;
        private List<PostUtil.PostSummaryData> posts;

        public static PostListResponse fromPage(Page<Post> postPage){
            return PostListResponse.builder()
                    .metaData(PostUtil.PostListMetaData.of(postPage.getTotalPages(), postPage.getNumber()))
                    .posts(postPage.map(PostUtil.PostSummaryData::fromEntity).toList())
                    .build();
        }

    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostDetailResponse{

        private Long id; // 게시물 고유번호
        private String title; // 게시물 제목
        private String nickname; // 게시물 작성자 닉네임
        private String categoryCode; //게시물 카테고리 분류
        private String content; // 게시물 내용
        private LocalDateTime createdAt; // 게시물 작성 시간
        private LocalDateTime updatedAt; // 게시물 업데이트 시간
        private List<PostUtil.PostImageSummaryData> images; // 게시물 이미지들

        public static PostDetailResponse fromEntity(Post foundPost, List<PostImage> foundImages){
            return PostDetailResponse.builder()
                    .id(foundPost.getId())
                    .title(foundPost.getTitle())
                    .nickname(foundPost.getProtector().getNickname())
                    .categoryCode(foundPost.getPostCategory().getCategoryCode())
                    .content(foundPost.getContent())
                    .createdAt(foundPost.getCreatedAt())
                    .updatedAt(foundPost.getUpdatedAt())
                    .images(foundImages.stream().map(PostUtil.PostImageSummaryData::fromEntity).toList())
                    .build();
        }
    }
}
