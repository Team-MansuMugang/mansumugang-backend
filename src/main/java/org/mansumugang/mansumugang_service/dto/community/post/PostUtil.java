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

        private Long id;
        private String title;
        private String nickname;
        private String content;
        private String categoryCode;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;


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

            List<PostImageSummaryData> summaryDataList = foundPostImages.stream()
                    .map(PostImageSummaryData::fromEntity)
                    .toList();

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
