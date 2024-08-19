package org.mansumugang.mansumugang_service.dto.community.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Comment;

import java.time.LocalDateTime;

public class CommentSave {

    @Getter
    @Setter
    @Builder
    public static class Request{

        @Valid
        @NotNull(message = "값이 널이면 안됩니다.")
        private  Long postId;

        @Valid
        @NotBlank(message = "댓글은 한글자 이상이어야합니다.")
        private String content;

    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long id; // 게시물 아이디
        private String nickname; // 댓글 작성자 닉네임
        private String content; // 댓글 내용
        private LocalDateTime createdAt; // 댓글 작성시간
        private LocalDateTime updatedAt; // 댓글 업데이트 시간

        public static Dto fromEntity(Comment savedComment){
            return Dto.builder()
                    .id(savedComment.getPost().getId())
                    .nickname(savedComment.getProtector().getNickname())
                    .content(savedComment.getContent())
                    .createdAt(savedComment.getCreatedAt())
                    .updatedAt(savedComment.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long id; // 게시물 아이디
        private String nickname; // 댓글 작성자 닉네임
        private String content; // 댓글 내용
        private LocalDateTime createdAt; // 댓글 작성시간
        private LocalDateTime updatedAt; // 댓글 업데이트 시간

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("댓글이 저장되었습니다.")
                    .id(dto.getId())
                    .nickname(dto.getNickname())
                    .content(dto.getContent())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .build();
        }

    }
}
