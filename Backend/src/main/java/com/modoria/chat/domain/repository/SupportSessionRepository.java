package com.modoria.chat.domain.repository;

import com.modoria.chat.domain.model.SupportSession;
import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface SupportSessionRepository extends JpaRepository<SupportSession, Long> {
    Optional<SupportSession> findByCustomer(User customer);

    Optional<SupportSession> findByCustomerId(Long customerId);

    Optional<SupportSession> findFirstByCustomerIdAndStatusNotOrderByCreatedAtDesc(Long customerId, SupportSessionStatus status);

    List<SupportSession> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<SupportSession> findByAgentIdOrderByCreatedAtDesc(Long agentId);

    List<SupportSession> findByAgentIdAndStatusInOrderByCreatedAtDesc(Long agentId, List<SupportSessionStatus> statuses);

    java.util.List<SupportSession> findByStatus(com.modoria.chat.domain.enums.SupportSessionStatus status);

    long countByStatus(SupportSessionStatus status);

    long countByCreatedAtGreaterThanEqual(LocalDateTime from);

    long countByAgentIsNullAndStatusIn(List<SupportSessionStatus> statuses);

    List<SupportSession> findAllByOrderByCreatedAtDesc();
}
