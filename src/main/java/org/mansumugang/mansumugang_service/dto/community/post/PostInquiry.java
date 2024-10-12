package org.mansumugang.mansumugang_service.dto.community.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostImage;
import org.mansumugang.mansumugang_service.domain.community.PostLike;
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

        private Long id;
        private String title;
        private String imageApiUrl;
        private String profileImageName;
        private String nickname;
        private String categoryCode;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<PostUtil.PostImagesSummaryData> image; // 게시물 이미지들
        private boolean isLiked;
        private Long likeCount;
        private Long bookmarkCount;
        private Long commentCount;

        // 프로필 이미지 경로 및 이름 추가.
        public static PostDetailResponse fromEntity(Post foundPost, List<PostImage> foundImages, String postImageApiUrlPrefix, String imageApiUrl ,boolean isLiked ,Long likeCount, Long bookmarkCount, Long commentCount){
            return PostDetailResponse.builder()
                    .id(foundPost.getId())
                    .title(foundPost.getTitle())
                    .imageApiUrl((imageApiUrl))
                    .profileImageName(foundPost.getProtector().getProfileImageName() != null ? foundPost.getProtector().getProfileImageName() : null)
                    .nickname(foundPost.getProtector().getNickname())
                    .categoryCode(foundPost.getPostCategory().getCategoryCode())
                    .content(foundPost.getContent())
                    .createdAt(foundPost.getCreatedAt())
                    .updatedAt(foundPost.getUpdatedAt())
                    .image(List.of(PostUtil.PostImagesSummaryData.fromEntity(foundImages, postImageApiUrlPrefix)))
                    .isLiked(isLiked)
                    .likeCount(likeCount)
                    .bookmarkCount(bookmarkCount)
                    .commentCount(commentCount)
                    .build();
        }
    }
}
