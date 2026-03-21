package com.modoria.catalog.domain.repository;

import com.modoria.catalog.domain.model.Category;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndSeason(String name, Season season);

    boolean existsByNameAndSeasonAndIdNot(String name, Season season, Long id);

    Page<Category> findByDeletedFalse(Pageable pageable);

    Page<Category> findByDeletedFalseAndSeason(Season season, Pageable pageable);

    Page<Category> findBySeason(Season season, Pageable pageable);

    Optional<Category> findByIdAndDeletedFalse(Long id);
}
