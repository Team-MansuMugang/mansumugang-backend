package org.mansumugang.mansumugang_service.dto.community.postInteraction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PostInteraction {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostLikeResponse{
        private String message;

        public static PostLikeResponse createNewResponse(String responseMessage){
            return PostLikeResponse.builder()
                    .message(responseMessage)
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class PostBookmarkResponse{
        private String message;

        public static PostBookmarkResponse createNewResponse(String message){
            return PostBookmarkResponse.builder()
                    .message(message)
                    .build();
        }
    }

}
