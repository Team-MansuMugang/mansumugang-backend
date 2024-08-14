package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
