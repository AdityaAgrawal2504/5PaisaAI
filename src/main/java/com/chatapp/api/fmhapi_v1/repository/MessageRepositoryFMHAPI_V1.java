package com.chatapp.api.fmhapi_v1.repository;

import com.chatapp.api.fmhapi_v1.model.entity.MessageEntityFMHAPI_V1;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing Message data from the database.
 */
@Repository
public interface MessageRepositoryFMHAPI_V1 extends JpaRepository<MessageEntityFMHAPI_V1, String> {

    /**
     * Finds messages in a conversation created before a given timestamp, ordered newest first.
     * @param conversationId The ID of the conversation.
     * @param cursorTimestamp The timestamp to fetch messages before.
     * @param pageable The pagination information (limit).
     * @return A list of messages.
     */
    @Query("SELECT m FROM MessageEntityFMHAPI_V1 m WHERE m.conversationId = :conversationId AND m.createdAt < :cursorTimestamp ORDER BY m.createdAt DESC")
    List<MessageEntityFMHAPI_V1> findByConversationIdAndCreatedAtBefore(
            @Param("conversationId") UUID conversationId,
            @Param("cursorTimestamp") OffsetDateTime cursorTimestamp,
            Pageable pageable
    );

    /**
     * Finds the latest messages in a conversation, ordered newest first.
     * @param conversationId The ID of the conversation.
     * @param pageable The pagination information (limit).
     * @return A list of messages.
     */
    List<MessageEntityFMHAPI_V1> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    /**
     * Checks if a user is a participant in a conversation.
     * @param conversationId The ID of the conversation.
     * @param userId The ID of the user.
     * @return True if the user is a participant, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ConversationEntityFMHAPI_V1 c JOIN c.participants p WHERE c.id = :conversationId AND p.id = :userId")
    boolean isUserParticipantInConversation(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);
    
    /**
     * Finds a single message by its ID.
     * @param id The unique ID of the message.
     * @return An Optional containing the message if found.
     */
    @Override
    Optional<MessageEntityFMHAPI_V1> findById(String id);
}
