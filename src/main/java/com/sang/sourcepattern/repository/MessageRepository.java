package com.sang.sourcepattern.repository;

import com.sang.sourcepattern.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByShopIdAndChannelTypeOrderByCreatedAtAsc(int shopId, String channelType);
    
    // For direct messages (1-1)
    List<Message> findByShopIdAndChannelTypeAndSenderEmailAndRecipientEmailOrderByCreatedAtAsc(
            int shopId, String channelType, String senderEmail, String recipientEmail);

    List<Message> findByShopIdAndChannelTypeAndRecipientEmailOrderByCreatedAtAsc(
            int shopId, String channelType, String recipientEmail);
            
    @Query("SELECT m FROM Message m WHERE m.shopId = :shopId AND m.channelType = :channelType AND (m.senderEmail = :email OR m.recipientEmail = :email) ORDER BY m.createdAt ASC")
    List<Message> findByShopIdAndChannelTypeAndParticipantEmailOrderByCreatedAtAsc(
            @Param("shopId") int shopId, @Param("channelType") String channelType, @Param("email") String email);
            
    long countByShopIdAndChannelTypeAndIsReadFalseAndSenderRoleNot(int shopId, String channelType, String senderRole);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT m.shopId FROM Message m WHERE m.senderEmail = :email OR m.recipientEmail = :email")
    List<Integer> findShopIdsByParticipantEmail(@org.springframework.data.repository.query.Param("email") String email);

    @Query(value = "SELECT * FROM message m WHERE m.shop_id = :shopId AND m.channel_type = :channelType AND (m.sender_email = :email OR m.recipient_email = :email) ORDER BY m.created_at DESC LIMIT 1", nativeQuery = true)
    Message findRealLastMessage(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("email") String email);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.shopId = :shopId AND m.channelType = :channelType AND m.recipientEmail = :email AND m.isRead = false AND m.senderRole <> 'USER'")
    long countUnreadForCustomer(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("email") String email);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.shopId = :shopId AND m.channelType = :channelType AND m.recipientEmail = :customerEmail AND m.isRead = false AND m.senderRole = 'USER'")
    long countUnreadForShopFromCustomer(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("customerEmail") String customerEmail);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.shopId = :shopId AND m.channelType = :channelType AND (m.senderEmail = :userEmail OR m.recipientEmail = :userEmail) AND m.isRead = false AND m.senderRole <> 'ADMIN'")
    long countUnreadForAdminFromUser(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("userEmail") String userEmail);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.shopId = :shopId AND m.channelType = :channelType AND m.senderRole <> :readerRole AND m.isRead = false")
    void markAllAsRead(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("readerRole") String readerRole);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.shopId = :shopId AND m.channelType = :channelType AND m.recipientEmail = :recipientEmail AND m.senderRole <> :readerRole AND m.isRead = false")
    void markRecipientAllAsRead(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("recipientEmail") String recipientEmail, @Param("readerRole") String readerRole);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.shopId = :shopId AND m.channelType = :channelType AND (m.senderEmail = :participantEmail OR m.recipientEmail = :participantEmail) AND m.senderRole <> :readerRole AND m.isRead = false")
    void markParticipantAllAsRead(@Param("shopId") int shopId, @Param("channelType") String channelType, @Param("participantEmail") String participantEmail, @Param("readerRole") String readerRole);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.createdAt BETWEEN :start AND :end")
    long countMessagesBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}
