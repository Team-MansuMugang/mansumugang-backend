package org.mansumugang.mansumugang_service.dto.community.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostImage;

import java.time.LocalDateTime;
import java.util.List;

public class PostUpdate {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @NotNull(message = "값이 null이면 안됩니다.")
        private Long postId; // 수정할 게시물 아이디

        @NotBlank
        @Size(min = 2, message = "제목은 2글자 이상이어야 합니다!")
        private String title; // 수정할 게시물 제목

        @NotBlank
        @Size(min = 2, message = "내용은 2글자 이상이어야 합니다!")
        private String content; // 수정할 게시물 내용

        @NotNull(message = "값이 null이면 안됩니다.")
        private String categoryCode; // 수정할 게시물 카테고리코드

//        private List<String> imageFilesToDelete; // 제거할 이미지 파일들

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long postId; // 수정된 게시물 아이디
        private String title;
        private String nickname; // 수정된 게시물의 작성자(닉네임)
        private String content;
        private String categoryCode;
        private LocalDateTime updatedAt;

        public static Dto fromEntity(Post updatedPost){
            return Dto.builder()
                    .postId(updatedPost.getId())
                    .title(updatedPost.getTitle())
                    .nickname(updatedPost.getProtector().getNickname())
                    .content(updatedPost.getContent())
                    .categoryCode(updatedPost.getPostCategory().getCategoryCode())
                    .updatedAt(updatedPost.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long postId; // 수정된 게시물 아이디
        private String title;
        private String nickname; // 수정된 게시물의 작성자(닉네임)
        private String content;
        private String categoryCode;
        private LocalDateTime updatedAt;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("게시물이 정상적으로 수정되었습니다!")
                    .postId(dto.getPostId())
                    .title(dto.getTitle())
                    .nickname(dto.getNickname())
                    .content(dto.getContent())
                    .categoryCode(dto.getCategoryCode())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }
    }
}
