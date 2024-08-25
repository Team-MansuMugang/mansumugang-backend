package org.mansumugang.mansumugang_service.dto.community.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostImage;

import java.time.LocalDateTime;
import java.util.List;

public class PostUtil {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostListMetaData{
        private int totalPage;
        private int currentPage;

        public static PostListMetaData of(int totalPage, int currentPage){
            return PostListMetaData.builder()
                    .totalPage(totalPage)
                    .currentPage(currentPage + 1)
                    .build();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostSummaryData{

        private Long id; // 게시물 고유번호
        private String title; // 게시물 제목
        private String nickname; // 게시물 작성자 닉네임
        private String content; // 게시물 내용 -> 프론트에서 잘라서 쓰면댐
        private String categoryCode; // 게시물 카테고리
        private LocalDateTime createdAt; // 게시물 작성 시간
        private LocalDateTime updatedAt; // 게시물 작성 시간
//        private Long likeCount; // 게시물 좋아요 수
//        private Long bookmarkCount; // 게시물 스크랩 수



        public static  PostSummaryData fromEntity(Post post){
            return  PostSummaryData.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .nickname(post.getProtector().getNickname())
                    .content(post.getContent())
                    .categoryCode(post.getPostCategory().getCategoryCode())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }
    }
    @Getter
    @AllArgsConstructor
    @Builder
    public static class PostImagesSummaryData{
        private String postImageApiUrlPrefix;
        private List<PostImageSummaryData> images;

        public static PostImagesSummaryData fromEntity(List<PostImage> foundPostImages, String postImageApiUrlPrefix){
            // PostImage 리스트를 PostImageSummaryData 리스트로 변환
            List<PostImageSummaryData> summaryDataList = foundPostImages.stream()
                    .map(PostImageSummaryData::fromEntity)
                    .toList();

            // PostImagesSummaryData 객체 생성
            return PostImagesSummaryData.builder()
                    .postImageApiUrlPrefix(postImageApiUrlPrefix)
                    .images(summaryDataList)
                    .build();
        }


    }


    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostImageSummaryData{

        private Long id;
        private String postImageName;
        private LocalDateTime createdAt;

        public static PostImageSummaryData fromEntity(PostImage foundPostImage){
            return PostImageSummaryData.builder()
                    .id(foundPostImage.getId())
                    .postImageName(foundPostImage.getImageName())
                    .createdAt(foundPostImage.getCreatedAt())
                    .build();
        }
    }

}
