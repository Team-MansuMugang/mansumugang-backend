package org.mansumugang.mansumugang_service.dto.community.postCategory;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PostCategoryInquiry {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Request{

        @NotNull
        private String categoryName;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{

    }

}
