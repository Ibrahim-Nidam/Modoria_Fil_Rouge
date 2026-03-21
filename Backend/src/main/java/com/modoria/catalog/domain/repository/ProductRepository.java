package com.modoria.catalog.domain.repository;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @EntityGraph(attributePaths = { "category", "images" })
    Page<Product> findByDeletedFalseAndCategoryDeletedFalse(Pageable pageable);

    @EntityGraph(attributePaths = { "category", "images" })
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "category", "images" })
    Page<Product> findByDeletedFalseAndCategoryDeletedFalseAndSeason(Season season, Pageable pageable);

    @EntityGraph(attributePaths = { "category", "images" })
    Page<Product> findBySeason(Season season, Pageable pageable);

    Optional<Product> findByIdAndDeletedFalseAndCategoryDeletedFalse(Long id);

    @EntityGraph(attributePaths = { "category", "images" })
    Optional<Product> findWithCategoryAndImagesByIdAndDeletedFalseAndCategoryDeletedFalse(Long id);

    @EntityGraph(attributePaths = { "category", "images" })
    Optional<Product> findWithCategoryAndImagesById(Long id);

    boolean existsByImageFolder(String imageFolder);

    List<Product> findByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    long countByCategoryIdAndDeletedFalse(Long categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Product p set p.deleted = true where p.category.id = :categoryId and p.deleted = false")
    int softDeleteByCategoryId(@Param("categoryId") Long categoryId);

    @Query("select p from Product p join fetch p.category c where p.id = :id and p.deleted = false and c.deleted = false")
    Optional<Product> findActiveByIdWithCategory(@Param("id") Long id);

    @Query("""
            select p.category.id as categoryId, count(p) as productCount
            from Product p
            where p.category.id in :categoryIds
            group by p.category.id
            """)
    List<CategoryProductCountProjection> countProductsByCategoryIds(@Param("categoryIds") Collection<Long> categoryIds);

    @Query("""
            select p.category.id as categoryId, count(p) as productCount
            from Product p
            where p.category.id in :categoryIds and p.deleted = false
            group by p.category.id
            """)
    List<CategoryProductCountProjection> countActiveProductsByCategoryIds(@Param("categoryIds") Collection<Long> categoryIds);
}
