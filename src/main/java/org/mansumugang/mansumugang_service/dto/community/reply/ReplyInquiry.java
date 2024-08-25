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
        private Long replyId; // 대댓글 고유번호

        private Long commentId; // 댓글 고유번호

        private String profileImageName; // 대댓글 작성자 프로필 이미지 파일

        private String creator; // 대댓글 작성자(닉네임)

        private String content; // 대댓글 내용

        private LocalDateTime createdAt; // 대댓글 작성시간

        private LocalDateTime updatedAt; // 대댓글 업데이트 시간

        private LocalDateTime deletedAt; // 대댓글 삭제 시간

        public static ReplyElement fromEntity(Reply reply){
            return ReplyElement.builder()
                    .replyId(reply.getId())
                    .commentId(reply.getComment().getId())
                    .profileImageName(reply.getProtector().getProfileImageName() != null && reply.getDeletedAt() == null ? reply.getProtector().getProfileImageName() : null)
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
