package com.modoria.chat.application.service;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.chat.domain.model.SupportSession;
import com.modoria.chat.domain.repository.SupportSessionRepository;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import com.modoria.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportSessionService {

    private final SupportSessionRepository supportSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SupportSession getOrCreateSession(Long customerId) {
        return supportSessionRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    User customer = userRepository.findById(customerId)
                            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
                    SupportSession session = SupportSession.builder()
                            .customer(customer)
                            .status(SupportSessionStatus.BOT_HANDLING)
                            .build();
                    return supportSessionRepository.save(session);
                });
    }

    @Transactional
    public void switchToAgent(Long customerId) {
        SupportSession session = getOrCreateSession(customerId);
        session.setStatus(SupportSessionStatus.ACTIVE_AGENT);
        supportSessionRepository.save(session);
    }

    @Transactional
    public void closeSession(Long customerId) {
        supportSessionRepository.findByCustomerId(customerId)
                .ifPresent(session -> {
                    session.setStatus(SupportSessionStatus.CLOSED);
                    supportSessionRepository.save(session);
                });
    }

    public boolean isBotHandling(Long customerId) {
        Optional<SupportSession> session = supportSessionRepository.findByCustomerId(customerId);
        return session.map(s -> s.getStatus() == SupportSessionStatus.BOT_HANDLING).orElse(true);
    }
}
