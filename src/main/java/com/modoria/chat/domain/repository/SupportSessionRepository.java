package com.modoria.chat.domain.repository;

import com.modoria.chat.domain.model.SupportSession;
import com.modoria.identity.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupportSessionRepository extends JpaRepository<SupportSession, Long> {
    Optional<SupportSession> findByCustomer(User customer);

    Optional<SupportSession> findByCustomerId(Long customerId);
}
