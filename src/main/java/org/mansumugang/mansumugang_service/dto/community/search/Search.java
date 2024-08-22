package org.mansumugang.mansumugang_service.dto.community.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.dto.community.post.PostUtil;
import org.springframework.data.domain.Page;

import java.util.List;

public class Search {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Response{
        private PostUtil.PostListMetaData metaData;
        private List<PostUtil.PostSummaryData> posts;

        public static Response fromPage(Page<Post> postPage){
            return Response.builder()
                    .metaData(PostUtil.PostListMetaData.of(postPage.getTotalPages(), postPage.getNumber()))
                    .posts(postPage.map(PostUtil.PostSummaryData::fromEntity).toList())
                    .build();
        }
    }

}
