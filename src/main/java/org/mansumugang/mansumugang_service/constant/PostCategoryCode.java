package org.mansumugang.mansumugang_service.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.PostCategory;

@AllArgsConstructor
@Getter
public enum PostCategoryCode {

    // 추후 추가 예정.

    FREE("자유"),
    HYPERTENSION("고혈압"),
    HYPOTENSION("저혈압"),
    DIABETES("당뇨"),
    DEMENTIA("치매"),
    CANCER("암"),
    ETC("기타 질병"),
    PROMOTION("홍보");

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
