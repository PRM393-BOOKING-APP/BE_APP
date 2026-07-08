package com.sang.sourcepattern.repository;

import com.sang.sourcepattern.entity.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Integer> {
    List<UserVoucher> findByUserId(Integer userId);
    List<UserVoucher> findByUserIdAndIsUsedFalse(Integer userId);
    List<UserVoucher> findByVoucherId(Integer voucherId);
    List<UserVoucher> findByVoucherIdAndIsUsedFalse(Integer voucherId);
    
    /**
     * Find all unused, not-expired vouchers for a user — includes inactive templates.
     * Frontend decides how to display inactive ones ("Không khả dụng").
     */
    @Query("SELECT uv FROM UserVoucher uv WHERE uv.user.id = :userId " +
           "AND uv.isUsed = false " +
           "AND (uv.expiresAt IS NULL OR uv.expiresAt > :now)")
    List<UserVoucher> findValidVouchersByUserId(@Param("userId") Integer userId,
                                                @Param("now") LocalDateTime now);

    /**
     * Find only active (usable) vouchers — for booking flow picker.
     */
    @Query("SELECT uv FROM UserVoucher uv WHERE uv.user.id = :userId " +
           "AND uv.isUsed = false " +
           "AND uv.voucher.isActive = true " +
           "AND (uv.expiresAt IS NULL OR uv.expiresAt > :now)")
    List<UserVoucher> findActiveVouchersByUserId(@Param("userId") Integer userId,
                                                 @Param("now") LocalDateTime now);
}
