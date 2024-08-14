package org.mansumugang.mansumugang_service.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.PostCategory;

@AllArgsConstructor
@Getter
public enum PostCategoryCode {

    // 추후 추가 예정.

    QUESTION("질문"),
    PROMOTION("홍보"),
    ETC("기타");

    private String categoryName;

    public static PostCategoryCode FromEntity(PostCategory postCategory){
        PostCategoryCode postCategoryCode = ETC;

        String categoryCode = postCategory.getCategoryCode();
        for (PostCategoryCode _postCategoryCode : PostCategoryCode.values()) {
            if(_postCategoryCode.name().equals(categoryCode)){
                postCategoryCode = _postCategoryCode;
            }
        }

        return postCategoryCode;
    }
}
