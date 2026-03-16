package com.modoria.chat.domain.model;

import com.modoria.chat.domain.enums.SupportSessionStatus;
import com.modoria.identity.domain.model.User;
import com.modoria.order.domain.model.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private User resolvedBy;

    @Column(length = 150)
    private String subject;

    @Column(name = "initial_message", length = 4000)
    private String initialMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupportSessionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = SupportSessionStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
