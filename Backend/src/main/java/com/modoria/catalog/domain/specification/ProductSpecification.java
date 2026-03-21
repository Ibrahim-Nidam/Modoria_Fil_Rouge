package com.modoria.catalog.domain.specification;

import com.modoria.catalog.domain.model.Product;
import com.modoria.catalog.domain.model.Season;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> withKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern));
        };
    }

    public static Specification<Product> withPriceRange(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) {
                return criteriaBuilder.conjunction();
            }
            if (min != null && max != null) {
                return criteriaBuilder.between(root.get("price"), min, max);
            }
            if (min != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), min);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), max);
        };
    }

    public static Specification<Product> withCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> withSeason(Season season) {
        return (root, query, criteriaBuilder) -> {
            if (season == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("season"), season);
        };
    }

    public static Specification<Product> withDeletedFilter(boolean includeDeleted) {
        return (root, query, criteriaBuilder) -> {
            if (includeDeleted) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(
                    criteriaBuilder.isFalse(root.get("deleted")),
                    criteriaBuilder.isFalse(root.get("category").get("deleted"))
            );
        };
    }
}
