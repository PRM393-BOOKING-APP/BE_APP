package com.sang.sourcepattern.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sang.sourcepattern.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    Optional<User> findByFacebookId(String facebookId);
    Optional<User> findByZaloId(String zaloId);
    Optional<User> findByGoogleId(String googleId);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT b.user FROM Booking b WHERE b.shop.id = :shopId")
    List<User> findUsersByShopId(int shopId);

    @Query("SELECT DISTINCT b.user FROM Booking b WHERE b.shop.id = :shopId AND b.status IN ('CONFIRMED', 'WAITING_SHOP_APPROVAL', 'IN_PROGRESS')")
    List<User> findUpcomingUsersByShopId(@Param("shopId") int shopId);

    @Query("SELECT DISTINCT b.user FROM Booking b WHERE b.shop.id = :shopId AND b.status = 'COMPLETED'")
    List<User> findCompletedUsersByShopId(@Param("shopId") int shopId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.shop.id = :shopId AND b.user.id = :userId")
    boolean existsBookingByShopIdAndUserId(@Param("shopId") int shopId, @Param("userId") int userId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.shop.id = :shopId AND b.user.email = :email")
    boolean existsBookingByShopIdAndUserEmail(@Param("shopId") int shopId, @Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name IN ('USER', 'SHOP_OWNER', 'STAFF') AND EXISTS (" +
           "SELECT 1 FROM Message m WHERE m.shopId = :shopId " +
           "AND (m.channelType = 'CUSTOMER_CHAT' OR (m.shopId = 0 AND m.channelType = 'ADMIN_SUPPORT')) " +
           "AND (m.senderEmail = u.email OR m.recipientEmail = u.email))")
    List<User> findUsersByChatHistory(@Param("shopId") int shopId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    long countUsersBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
}

