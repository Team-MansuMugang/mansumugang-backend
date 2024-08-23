package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByPostCategoryId(Long postCategoryId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p WHERE p.title LIKE %:content% OR p.content LIKE %:content%")
    Page<Post> findByTitleOrContentContaining(@Param("content") String searchContent, Pageable pageable);

    List<Post> findAllByProtectorId(Long protectorId);
}
