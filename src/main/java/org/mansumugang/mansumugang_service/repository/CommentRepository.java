package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Long countByPostId(Long postId);

    Page<Comment> findAllByPost(Post post, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND ((c.id > :commentId AND c.createdAt = :createdAt) OR c.createdAt > :createdAt)")
    Page<Comment> getCommentsByCursor(@Param("post") Post post,
                                      @Param("commentId") Long commentId,
                                      @Param("createdAt") LocalDateTime createdAt,
                                      Pageable replyPageable
    );
}
