package org.mansumugang.mansumugang_service.repository;

import org.mansumugang.mansumugang_service.domain.community.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
    Optional<PostCategory> findByCategoryCode(String categoryCode);
}
