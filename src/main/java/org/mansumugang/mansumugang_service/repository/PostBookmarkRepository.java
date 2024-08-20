package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostBookmark;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    Long countByPostId(Long postId);

    PostBookmark findByProtectorAndPost(Protector protector, Post post);
}
