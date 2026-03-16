package com.modoria.order.domain.repository;

import com.modoria.order.domain.model.Order;
import com.modoria.order.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByStatus(OrderStatus status);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status AND o.createdAt >= :from")
    BigDecimal sumTotalAmountByStatusAndCreatedAtGreaterThanEqual(
            @Param("status") OrderStatus status,
            @Param("from") LocalDateTime from);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.items i " +
            "WHERE o.user.id = :userId AND i.product.id = :productId AND o.status = 'COMPLETED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}
