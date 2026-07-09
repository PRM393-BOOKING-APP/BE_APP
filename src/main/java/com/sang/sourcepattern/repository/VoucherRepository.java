package com.sang.sourcepattern.repository;

import com.sang.sourcepattern.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    List<Voucher> findByTargetTierName(String targetTierName);
    Optional<Voucher> findByCode(String code);
}
