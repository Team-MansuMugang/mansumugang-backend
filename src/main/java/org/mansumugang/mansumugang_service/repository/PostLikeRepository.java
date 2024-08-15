package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostLike;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Long countByPostId(Long postId);

    PostLike findByProtectorAndPost(Protector protector, Post post);


}
