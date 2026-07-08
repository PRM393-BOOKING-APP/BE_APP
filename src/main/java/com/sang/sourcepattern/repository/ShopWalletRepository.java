package com.sang.sourcepattern.repository;

import com.sang.sourcepattern.entity.ShopWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopWalletRepository extends JpaRepository<ShopWallet, Integer> {
    Optional<ShopWallet> findByShopId(int shopId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(w.availableBalance), 0) FROM ShopWallet w")
    java.math.BigDecimal sumTotalAvailableBalance();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(w.totalEarned), 0) FROM ShopWallet w")
    java.math.BigDecimal sumTotalEarned();
}
