package com.modoria.catalog.domain.repository;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySeason(Season season);
}
