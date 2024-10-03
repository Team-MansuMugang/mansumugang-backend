package org.mansumugang.mansumugang_service.dto.community.reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.community.Reply;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReplyInquiry {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    private static class ReplyElement{
        private Long replyId;

        private Long commentId;

        private String profileImageName;

        private String creator;

        private String content;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private LocalDateTime deletedAt;

        public static ReplyElement fromEntity(Reply reply){
            return ReplyElement.builder()
                    .replyId(reply.getId())
                    .commentId(reply.getComment().getId())
                    .profileImageName(reply.getProtector() != null && reply.getProtector().getProfileImageName() != null && reply.getDeletedAt() == null ? reply.getProtector().getProfileImageName() : null)
                    .creator(reply.getDeletedAt() == null ? reply.getProtector().getNickname() : "알 수 없음")
                    .content(reply.getContent())
                    .createdAt(reply.getCreatedAt())
                    .updatedAt(reply.getUpdatedAt())
                    .deletedAt(reply.getDeletedAt())
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Response{

        private String imageApiUrl;
        private List<ReplyElement> replies;

        public static Response fromPage(Page<Reply> page, String imageApiUrl){
            return Response.builder()
                    .imageApiUrl(imageApiUrl)
                    .replies(page.map(ReplyElement::fromEntity).toList())

                    .build();
        }

    }
}
