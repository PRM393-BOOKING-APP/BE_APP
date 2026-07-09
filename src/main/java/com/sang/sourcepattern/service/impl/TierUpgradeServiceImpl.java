package com.sang.sourcepattern.service.impl;

import com.sang.sourcepattern.entity.*;
import com.sang.sourcepattern.repository.*;
import com.sang.sourcepattern.service.TierUpgradeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TierUpgradeServiceImpl implements TierUpgradeService {

    UserRepository userRepository;
    MembershipTierRepository membershipTierRepository;
    VoucherRepository voucherRepository;
    UserVoucherRepository userVoucherRepository;
    com.sang.sourcepattern.repository.NotificationRepository notificationRepository;
    com.sang.sourcepattern.service.SystemConfigService systemConfigService;

    @Override
    @Transactional
    public void processTierUpgrade(User user, double spendingDelta) {
        if (!systemConfigService.isVoucherServiceEnabled()) {
            log.info("Tier upgrade skipped for user {} because voucher/tier service is globally disabled.", user.getEmail());
            return;
        }

        // CỘNG DỒN thêm spendingDelta vào total_spending HIỆN CÓ, thay vì recompute lại từ đầu
        // bằng cách quét toàn bộ booking COMPLETED trong lịch sử. Lý do: total_spending có thể đã
        // được set từ trước (seed data, admin chỉnh tay, hoặc từ những lần cộng dồn trước) mà KHÔNG
        // nhất thiết khớp 1-1 với tổng giá trị các booking COMPLETED hiện có trong DB (ví dụ dữ liệu
        // test seed cố tình đặt total_spending cao hơn lịch sử booking để demo 1 hạng cụ thể). Nếu
        // recompute lại từ đầu mỗi lần có booking mới hoàn thành, total_spending có thể bất ngờ tụt
        // xuống đúng bằng tổng lịch sử booking COMPLETED thật — thấp hơn nhiều so với giá trị đang có
        // — gây hiểu nhầm "cộng tiền sai" dù bản chất là baseline ban đầu không khớp với lịch sử.
        double totalSpending = (user.getTotalSpending() != null ? user.getTotalSpending() : 0) + spendingDelta;
        user.setTotalSpending(totalSpending);

        // 2. Find the highest tier the user qualifies for
        List<MembershipTier> allTiers = membershipTierRepository.findAll();
        MembershipTier newTier = null;
        double maxQualifiedSpending = -1;

        for (MembershipTier tier : allTiers) {
            if (totalSpending >= tier.getRequiredSpending() && tier.getRequiredSpending() >= maxQualifiedSpending) {
                newTier = tier;
                maxQualifiedSpending = tier.getRequiredSpending();
            }
        }

        // 3. If tier changed, update and issue vouchers
        if (newTier != null) {
            // Chỉ coi là "lên hạng" khi hạng mới có ngưỡng chi tiêu CAO HƠN hạng hiện tại — chặn
            // mọi thay đổi hạng đi xuống (ví dụ do currentTier bị set tay/seed cao hơn spendingDelta
            // cộng dồn thực tế tại một thời điểm nào đó), tránh cấp nhầm voucher chào mừng hạng thấp hơn.
            double currentRequiredSpending = user.getCurrentTier() == null
                    ? -1
                    : user.getCurrentTier().getRequiredSpending();
            boolean isUpgraded = user.getCurrentTier() == null
                    || (user.getCurrentTier().getId() != newTier.getId()
                        && newTier.getRequiredSpending() > currentRequiredSpending);

            if (isUpgraded) {
                user.setCurrentTier(newTier);
                user.setJustUpgraded(true);
                log.info("User {} upgraded to tier {}", user.getEmail(), newTier.getName());

                // Issue vouchers for the new tier — chỉ cấp voucher đang ACTIVE, bỏ qua các
                // template đã bị admin vô hiệu hóa (ví dụ SILVER_REVOKED trong seed), nếu không
                // user sẽ nhận về 1 voucher không dùng được ngay từ đầu.
                List<Voucher> tierVouchers = voucherRepository.findByTargetTierName(newTier.getName())
                        .stream()
                        .filter(Voucher::isActive)
                        .toList();
                int totalIssued = 0;
                java.util.List<String> voucherCodes = new java.util.ArrayList<>();
                for (Voucher voucher : tierVouchers) {
                    for (int i = 0; i < voucher.getIssueQuantity(); i++) {
                        UserVoucher userVoucher = UserVoucher.builder()
                                .user(user)
                                .voucher(voucher)
                                .isUsed(false)
                                .expiresAt(java.time.LocalDateTime.now().plusDays(voucher.getValidDays() != null ? voucher.getValidDays() : 30))
                                .build();
                        userVoucherRepository.save(userVoucher);
                        totalIssued++;
                    }
                    voucherCodes.add(voucher.getIssueQuantity() + "x " + voucher.getCode());
                    log.info("Issued {}x voucher {} to user {}", voucher.getIssueQuantity(), voucher.getCode(), user.getEmail());
                }

                // Notify user about tier upgrade + vouchers.
                // Dùng tên hạng tiếng Việt (khớp với nhãn hiển thị trên app) thay vì để
                // nguyên chuỗi enum tiếng Anh (BRONZE/SILVER/GOLD/PLATINUM) trong nội dung thông báo.
                String tierLabelVi = toVietnameseTierName(newTier.getName());
                String voucherSummary = voucherCodes.isEmpty()
                    ? ""
                    : " Bạn nhận được: " + String.join(", ", voucherCodes) + ".";
                Notification notif = Notification.builder()
                    .user(user)
                    .title("🎉 Chúc mừng! Bạn lên hạng " + tierLabelVi)
                    .content(String.format(
                        "Tài khoản của bạn vừa được nâng lên hạng %s.%s Vào mục 'Ưu đãi của tôi' để xem chi tiết.",
                        tierLabelVi,
                        voucherSummary
                    ))
                    .notificationType(Notification.NotificationType.PROMOTION)
                    .build();
                notificationRepository.save(notif);
            }
        }

        userRepository.save(user);
    }

    /**
     * Map tên hạng lưu trong DB (tiếng Anh) sang nhãn tiếng Việt hiển thị cho khách hàng.
     * Phải khớp với mapping phía app (my_vouchers_screen / profile_screen / admin_voucher_screen).
     */
    private String toVietnameseTierName(String tierName) {
        if (tierName == null) return "";
        return switch (tierName.toUpperCase()) {
            case "BRONZE" -> "Đồng";
            case "SILVER" -> "Bạc";
            case "GOLD" -> "Vàng";
            case "PLATINUM" -> "Bạch Kim";
            default -> tierName;
        };
    }
}
