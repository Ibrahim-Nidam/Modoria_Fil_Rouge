package com.modoria.chat.domain.repository;

import com.modoria.chat.domain.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

        List<ChatMessage> findBySupportSessionIdOrderByTimestampAsc(Long supportSessionId);

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
            "ORDER BY m.timestamp ASC")
    Page<ChatMessage> findChatHistory(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id,
            Pageable pageable);

    long countByReceiverIdAndStatus(Long receiverId, com.modoria.chat.domain.enums.MessageStatus status);
}
