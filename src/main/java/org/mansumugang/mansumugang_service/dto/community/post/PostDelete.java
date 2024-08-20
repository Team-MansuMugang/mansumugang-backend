package org.mansumugang.mansumugang_service.dto.community.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Post;

public class PostDelete {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Dto{
        private Long deletedPostId;
        private String deletedPostTitle;

        public static Dto fromEntity(Long postId, String title){
            return Dto.builder()
                    .deletedPostId(postId)
                    .deletedPostTitle(title)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String message;
        private Long deletedPostId;
        private String deletedPostTitle;

        public static Response createNewResponse(Dto dto){
            return Response.builder()
                    .message("게시물이 정상적으로 삭제되었습니다!")
                    .deletedPostId(dto.getDeletedPostId())
                    .deletedPostTitle(dto.getDeletedPostTitle())
                    .build();
        }
    }
}
