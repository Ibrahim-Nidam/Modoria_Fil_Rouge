package com.modoria.catalog.domain.repository;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(attributePaths = { "category" })
    Page<Product> findAll(Pageable pageable);

    @EntityGraph(attributePaths = { "category" })
    Page<Product> findBySeason(Season season, Pageable pageable);

    long countByCategoryId(Long categoryId);

    @Query("""
            select p.category.id as categoryId, count(p) as productCount
            from Product p
            where p.category.id in :categoryIds
            group by p.category.id
            """)
    List<CategoryProductCountProjection> countProductsByCategoryIds(@Param("categoryIds") Collection<Long> categoryIds);
}
