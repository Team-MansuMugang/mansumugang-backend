package org.mansumugang.mansumugang_service.dto.community.post;

import jakarta.validation.Valid;
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


public class PostSave {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @Valid
        @NotNull
        @Size(min = 2, max = 20, message = "제목이 너무 짧거나 깁니다.")
        private String title;

        @Valid
        @NotNull
        private String content;

        @Valid
        @NotNull
        private String categoryCode;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Dto{

        private String title;
        private String categoryCode;
        private String username;
        private String nickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Dto fromEntity(Post post){

            return Dto.builder()
                    .title(post.getTitle())
                    .categoryCode(post.getPostCategory().getCategoryCode())
                    .username(post.getProtector().getUsername())
                    .nickname(post.getProtector().getNickname())
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .build();
        }

    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private String title; // 게시물 제목
        private String categoryCode; // 게시물 카테고리
        private String username; // 게시물 작성자 아이디
        private String nickname; // 게시물 작성자 닉네임
        private LocalDateTime createdAt; // 게시물 작성시간
        private LocalDateTime updatedAt; // 게시물 업데이트 시간

        public static Response createNewResponse(Dto dto){

            return Response.builder()
                    .message("게시물이 저장되었습니다.")
                    .title(dto.getTitle())
                    .categoryCode(dto.getCategoryCode())
                    .username(dto.getUsername())
                    .nickname(dto.getNickname())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }


    }

}
