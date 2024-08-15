package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Comment;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Long countByPostId(Long postId);

    Comment findByProtectorAndPost(Protector protector, Post post);
}
