package com.sang.sourcepattern.service.impl;

import com.sang.sourcepattern.dto.request.WithdrawalRequestCreate;
import com.sang.sourcepattern.dto.response.PayOSCreateResponse;
import com.sang.sourcepattern.dto.response.PayOSPaymentInfoResponse;
import com.sang.sourcepattern.dto.response.ShopWalletResponse;
import com.sang.sourcepattern.dto.response.WithdrawalRequestResponse;
import com.sang.sourcepattern.entity.*;
import com.sang.sourcepattern.exception.AppException;
import com.sang.sourcepattern.exception.ErrorCode;
import com.sang.sourcepattern.repository.*;import com.sang.sourcepattern.service.PayOSService;
import com.sang.sourcepattern.service.WalletService;import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletServiceImpl implements WalletService {

    ShopWalletRepository        walletRepository;
    WithdrawalRequestRepository withdrawalRepository;
    BookingRepository           bookingRepository;
    PaymentRepository           paymentRepository;
    TransactionRepository       transactionRepository;
    ShopRepository              shopRepository;
    UserRepository              userRepository;
    NotificationRepository      notificationRepository;
    PayOSService                payOSService;
    com.sang.sourcepattern.service.CameraService cameraService;
    ServiceRepository           serviceRepository;
    org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    private void sendBookingUpdateEvent(Integer shopId, Integer bookingId) {
        if (shopId != null && messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/shop/" + shopId + "/notifications",
                java.util.Map.of("message", "BOOKING_UPDATED", "bookingId", bookingId));
        }
    }

    /** Phí admin: 10% */
    @NonFinal
    @Value("${wallet.admin-fee-rate:0.10}")
    BigDecimal adminFeeRate;

    @NonFinal
    @Value("${payos.return-url}")
    String returnUrl;

    @NonFinal
    @Value("${payos.cancel-url}")
    String cancelUrl;

    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** Format số tiền VND để hiển thị trong notification */
    private static String formatVND(BigDecimal amount) {
        return String.format("%,.0fđ", amount);
    }

    /** Null-safe BigDecimal — trả về ZERO nếu null (phòng khi DB có row cũ chưa có default) */
    private static BigDecimal safe(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private Shop resolveOwnerShop(String ownerEmail) {
        return shopRepository.findByOwnerEmail(ownerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
    }

    private ShopWallet getOrCreateWallet(Shop shop) {
        return walletRepository.findByShopId(shop.getId())
                .orElseGet(() -> {
                    ShopWallet w = ShopWallet.builder().shop(shop).build();
                    return walletRepository.save(w);
                });
    }

    private ShopWalletResponse toWalletResponse(ShopWallet w) {
        return ShopWalletResponse.builder()
                .id(w.getId())
                .shopId(w.getShop().getId())
                .frozenBalance(safe(w.getFrozenBalance()))
                .availableBalance(safe(w.getAvailableBalance()))
                .totalEarned(safe(w.getTotalEarned()))
                .totalWithdrawn(safe(w.getTotalWithdrawn()))
                .updatedAt(w.getUpdatedAt() != null ? w.getUpdatedAt().format(FMT) : "")
                .build();
    }

    private WithdrawalRequestResponse toWithdrawalResponse(WithdrawalRequest r) {
        return WithdrawalRequestResponse.builder()
                .id(r.getId())
                .shopId(r.getShop().getId())
                .shopName(r.getShop().getShopName())
                .amount(r.getAmount())
                .bankName(r.getBankName())
                .bankAccount(r.getBankAccount())
                .accountHolder(r.getAccountHolder())
                .note(r.getNote())
                .status(r.getStatus())
                .adminNote(r.getAdminNote())
                .payosOrderCode(r.getPayosOrderCode())
                .checkoutUrl(r.getCheckoutUrl())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().format(FMT) : "")
                .processedAt(r.getProcessedAt() != null ? r.getProcessedAt().format(FMT) : null)
                .type("WITHDRAWAL")
                .build();
    }

    // ─── Queries ──────────────────────────────────────────────────────────────

    @Override
    public ShopWalletResponse getMyWallet(String ownerEmail) {
        Shop shop = resolveOwnerShop(ownerEmail);
        ShopWallet wallet = getOrCreateWallet(shop);
        return toWalletResponse(wallet);
    }

    @Override
    public ShopWalletResponse getWalletByShopId(int shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
        ShopWallet wallet = getOrCreateWallet(shop);
        return toWalletResponse(wallet);
    }

    @Override
    public BigDecimal getCommissionRateForService(com.sang.sourcepattern.entity.Service s) {
        if (s == null || s.getCategory() == null) {
            return adminFeeRate;
        }
        String category = s.getCategory().toUpperCase();
        if (category.contains("GROOMING") || category.contains("SPA")) {
            return new BigDecimal("0.18");
        } else if ((category.contains("BOARDING") || category.contains("HOTEL")) && s.isCameraEnabled()) {
            return new BigDecimal("0.25");
        } else if (category.contains("BOARDING") || category.contains("HOTEL")) {
            // Boarding/Hotel không có camera: 15%
            return new BigDecimal("0.15");
        } else if (category.contains("CLINIC")) {
            return new BigDecimal("0.10");
        }
        return adminFeeRate;
    }

    @Override
    public BigDecimal calculateAdminCommission(List<Integer> serviceIds) {
        if (serviceIds == null || serviceIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (Integer id : serviceIds) {
            com.sang.sourcepattern.entity.Service s = serviceRepository.findById(id).orElse(null);
            if (s != null && s.getPrice() != null) {
                BigDecimal rate = getCommissionRateForService(s);
                totalCommission = totalCommission.add(s.getPrice().multiply(rate));
            }
        }
        return totalCommission;
    }

    private BigDecimal calculateAdminCommissionForBooking(Booking booking) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        if (booking.getServices() == null || booking.getServices().isEmpty()) {
            return totalCommission;
        }
        for (com.sang.sourcepattern.entity.Service s : booking.getServices()) {
            if (s.getPrice() != null) {
                BigDecimal rate = getCommissionRateForService(s);
                totalCommission = totalCommission.add(s.getPrice().multiply(rate));
            }
        }
        return totalCommission;
    }

    @Override
    public BigDecimal getAdminBalance() {
        // Tổng phí admin từ tất cả booking đã thanh toán thành công (real-time, không chờ COMPLETED)
        List<Booking> paidBookings = bookingRepository.findPaidBookingsWithServices();
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (Booking booking : paidBookings) {
            totalCommission = totalCommission.add(calculateAdminCommissionForBooking(booking));
        }
        return totalCommission.setScale(0, RoundingMode.DOWN);
    }

    @Override
    public BigDecimal getAdminFeeRate() {
        return adminFeeRate;
    }

    // ─── Booking lifecycle hooks ───────────────────────────────────────────────

    @Override
    @Transactional
    public void onPaymentSuccess(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            return;
        }

        String paymentMethod = payment.getMethod();
        boolean isCashDeposit = "CASH_DEPOSIT".equals(paymentMethod);
        boolean isPureCash = "CASH".equals(paymentMethod);
        boolean isOnlinePayment = !isCashDeposit && !isPureCash; // PAYOS, MOCK

        if (!isOnlinePayment) {
            // Với CASH_DEPOSIT, Shop nhận tiền mặt tại quán, nên không cộng ví ảo.
            log.info("Payment success for booking {} but method is {}. Not crediting shop virtual wallet.", bookingId, paymentMethod);
            return;
        }

        BigDecimal fullPrice = (booking.getServices() != null && !booking.getServices().isEmpty())
                ? booking.getServices().stream().map(s -> s.getPrice() != null ? s.getPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        BigDecimal adminCommission = calculateAdminCommissionForBooking(booking);
        BigDecimal shopShare = fullPrice.subtract(adminCommission).setScale(0, RoundingMode.DOWN);

        ShopWallet wallet = getOrCreateWallet(booking.getShop());
        
        // Khách trả 100% qua hệ thống, Admin giữ tiền. Admin cộng lại shopShare cho ví Shop ngay lập tức.
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).add(shopShare));
        wallet.setTotalEarned(safe(wallet.getTotalEarned()).add(shopShare));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        if (!transactionRepository.existsByBookingIdAndType(bookingId, "WALLET_CREDIT")) {
            transactionRepository.save(Transaction.builder()
                    .booking(booking)
                    .shop(booking.getShop())
                    .type("WALLET_CREDIT")
                    .amount(shopShare)
                    .paymentMethod(paymentMethod)
                    .status("SUCCESS")
                    .description(String.format(
                            "Cộng tiền vào ví (ngay lập tức) cho đơn đặt lịch #%d (phần shop nhận được: %s VNĐ)",
                            bookingId, fullPrice.toPlainString()))
                    .completedAt(LocalDateTime.now())
                    .build());
        }
        
        log.info("Credited {} immediately to Shop {} wallet for online payment booking {}", shopShare, booking.getShop().getShopName(), bookingId);
    }

    @Override
    @Transactional
    public void onBookingCompleted(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);

        BigDecimal fullPrice = (booking.getServices() != null && !booking.getServices().isEmpty())
                ? booking.getServices().stream().map(s -> s.getPrice() != null ? s.getPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        BigDecimal adminCommission = calculateAdminCommissionForBooking(booking);
        BigDecimal shopShare = fullPrice.subtract(adminCommission).setScale(0, RoundingMode.DOWN);

        String paymentMethod;

        if (payment != null && "CASH_DEPOSIT".equals(payment.getMethod())
                && "SUCCESS".equals(payment.getStatus())) {
            // ── Cash-deposit booking: cọc đã thu qua PayOS ──────────────
            // Admin đã nhận (deposit = adminCommission). Shop nhận phần còn lại từ tiền mặt thu tại chỗ.
            // Ghi thêm 1 transaction BOOKING_PAYMENT CASH cho phần còn lại.
            paymentMethod = "CASH_DEPOSIT";

            transactionRepository.save(Transaction.builder()
                    .booking(booking)
                    .shop(booking.getShop())
                    .type("BOOKING_PAYMENT")
                    .amount(shopShare)
                    .paymentMethod("CASH")
                    .status("SUCCESS")
                    .description(String.format(
                            "Cash collected at venue for booking #%d (shop share of %s VND)",
                            bookingId, fullPrice.toPlainString()))
                    .completedAt(LocalDateTime.now())
                    .build());

        } else if (payment == null || !"SUCCESS".equals(payment.getStatus())) {
            // ── Legacy cash booking (không có cọc) ───────────────────────────
            paymentMethod = "CASH";

            if (payment != null) {
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);
            }

            transactionRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                    .stream().findFirst().ifPresent(t -> {
                        t.setStatus("SUCCESS");
                        t.setCompletedAt(LocalDateTime.now());
                        t.setNote("Cash collected on completion");
                        transactionRepository.save(t);
                    });
        } else {
            // ── PayOS booking ─────────────────────────────────────────────────
            paymentMethod = payment.getMethod();
        }

        // Cộng TRỰC TIẾP vào available + totalEarned
        ShopWallet wallet = getOrCreateWallet(booking.getShop());
        
        boolean isPureCash = "CASH".equals(paymentMethod);
        boolean isCashDeposit = "CASH_DEPOSIT".equals(paymentMethod);
        boolean isOnlinePayment = !isPureCash && !isCashDeposit; // PAYOS, MOCK, etc.
        boolean isShopCreated = "SHOP".equalsIgnoreCase(booking.getCreatedBy());

        if (isPureCash) {
            // Shop nhận 100% tiền mặt từ khách nên Shop nợ Admin phần hoa hồng.
            // Trừ phần adminCommission vào số dư ví của Shop (áp dụng cho mọi đơn CASH dù ai tạo).
            wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).subtract(adminCommission));
        }
        
        // Với CASH_DEPOSIT: Khách đã trả cọc qua PayOS (đủ phần adminCommission theo tỷ lệ dịch vụ), Admin đã nhận.
        // Shop nhận phần còn lại bằng tiền mặt từ khách. Nên availableBalance không đổi.
        
        if (isPureCash || isCashDeposit) {
            wallet.setTotalEarned(safe(wallet.getTotalEarned()).add(shopShare));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
        } else {
            // Đối với isOnlinePayment, đã cộng totalEarned lúc onPaymentSuccess
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
        }

        if (isOnlinePayment) {
            // Transaction WALLET_CREDIT is now created in onPaymentSuccess
            log.info("Booking completed {} — wallet already credited at payment step.", bookingId);
        } else if (isPureCash) {
            if (transactionRepository.existsByBookingIdAndType(bookingId, "COMMISSION_FEE")) {
                log.warn("Commission fee transaction already exists for booking {}, skipping.", bookingId);
            } else {
                // Ghi Transaction COMMISSION_FEE (Trừ tiền hoa hồng theo tỷ lệ từng dịch vụ)
                transactionRepository.save(Transaction.builder()
                        .booking(booking)
                        .shop(booking.getShop())
                        .type("COMMISSION_FEE")
                        .amount(adminCommission)
                        .paymentMethod("SYSTEM")
                        .status("SUCCESS")
                        .description(String.format("Deducted commission fee for cash booking #%d (rate varies by service: 10%%-25%%)", bookingId))
                        .completedAt(LocalDateTime.now())
                        .build());

                log.info("Commission fee deducted for CASH booking {} — adminCommission={}", bookingId, adminCommission);
            }
        } else {
            log.info("Cash deposit booking completed {} — no wallet balance change. Admin kept deposit, Shop received cash.", bookingId);
        }
    }

    @Override
    @Transactional
    public void onBookingCancelled(int bookingId) {
        // Booking bị huỷ → không làm gì với ví (tiền chưa bao giờ vào ví)
        log.info("Booking {} CANCELLED — no wallet impact", bookingId);
    }

    @Transactional
    public void onBookingConfirmed(int bookingId) {
        log.warn("onBookingConfirmed called for booking {} — DEPRECATED, no action taken", bookingId);
    }

    // ─── Withdrawal ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public WithdrawalRequestResponse createWithdrawalRequest(WithdrawalRequestCreate request, String ownerEmail) {
        Shop shop = resolveOwnerShop(ownerEmail);
        ShopWallet wallet = getOrCreateWallet(shop);

        // Kiểm tra min withdrawal
        if (request.getAmount().compareTo(new BigDecimal("200000")) < 0) {
            throw new AppException(ErrorCode.MIN_WITHDRAWAL_AMOUNT_REQUIRED);
        }

        // Kiểm tra số dư khả dụng
        if (safe(wallet.getAvailableBalance()).compareTo(request.getAmount()) < 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Kiểm tra không có yêu cầu đang chờ
        BigDecimal pending = withdrawalRepository.sumPendingByShop(shop.getId());
        if (pending.compareTo(BigDecimal.ZERO) > 0) {
            throw new AppException(ErrorCode.PENDING_WITHDRAWAL_EXISTS);
        }

        // Tạm giữ số tiền (trừ khỏi available, chờ admin duyệt)
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).subtract(request.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WithdrawalRequest wr = WithdrawalRequest.builder()
                .shop(shop)
                .amount(request.getAmount())
                .bankName(request.getBankName())
                .bankAccount(request.getBankAccount())
                .accountHolder(request.getAccountHolder())
                .note(request.getNote())
                .status("PENDING")
                .build();

        wr = withdrawalRepository.save(wr);
        log.info("Withdrawal request {} created by shop {} — amount={}", wr.getId(), shop.getShopName(), request.getAmount());
        return toWithdrawalResponse(wr);
    }

    @Override
    public List<WithdrawalRequestResponse> getMyWithdrawalRequests(String ownerEmail) {
        Shop shop = resolveOwnerShop(ownerEmail);
        return withdrawalRepository.findByShopIdOrderByCreatedAtDesc(shop.getId())
                .stream().map(this::toWithdrawalResponse).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalRequestResponse> getAllWithdrawalRequests() {
        return withdrawalRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toWithdrawalResponse).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalRequestResponse> getWithdrawalRequestsByStatus(String status) {
        return withdrawalRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase())
                .stream().map(this::toWithdrawalResponse).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalRequestResponse> getWaitingRefundsAsWithdrawals() {
        return bookingRepository.findByStatusWithServices("WAITING_REFUND")
                .stream()
                .map(b -> {
                    BigDecimal refundAmount = b.getRefundAmount() != null ? b.getRefundAmount() : BigDecimal.ZERO;
                    Payment payment = paymentRepository.findByBookingId(b.getId()).orElse(null);
                    // Luôn cap refundAmount theo số tiền khách đã thực tế thanh toán (payment.amount)
                    // Không dùng payment.status == SUCCESS vì lúc booking WAITING_REFUND thì payment đã đổi status
                    if (payment != null && payment.getAmount() != null) {
                        if (refundAmount.compareTo(payment.getAmount()) > 0) {
                            refundAmount = payment.getAmount();
                        }
                    }
                    
                    return WithdrawalRequestResponse.builder()
                            .id(b.getId()) // use booking id
                            .shopId(b.getShop().getId())
                            .shopName("Khách hàng: " + (b.getUser() != null ? b.getUser().getFullName() : "Unknown"))
                            .amount(refundAmount)
                            .bankName(b.getBankName())
                            .bankAccount(b.getBankAccount())
                            .accountHolder(b.getAccountHolder())
                            .note("Lý do huỷ: " + (b.getCancellationReason() != null ? b.getCancellationReason() : (b.getNote() != null ? b.getNote() : "Không có lý do")))
                            .status("PENDING") // Map WAITING_REFUND to PENDING so UI shows it in pending tab
                            .createdAt(b.getUpdatedAt() != null ? b.getUpdatedAt().format(FMT) : "")
                            .type("REFUND")
                            .userId(b.getUser() != null ? b.getUser().getId() : null)
                            .userEmail(b.getUser() != null ? b.getUser().getEmail() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse approveWithdrawal(int requestId, String adminNote) {
        // Kept for backward-compat — delegates to initiatePayoutPayOS
        return initiatePayoutPayOS(requestId, adminNote);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse initiatePayoutPayOS(int requestId, String adminNote) {
        WithdrawalRequest wr = withdrawalRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        if (!"PENDING".equals(wr.getStatus())) {
            throw new AppException(ErrorCode.WITHDRAWAL_ALREADY_PROCESSED);
        }

        long orderCode = ThreadLocalRandom.current().nextLong(10_000_000L, 99_999_999L);
        int amountVnd = wr.getAmount().intValue();
        if (amountVnd < 2000) amountVnd = 2000;
        if (amountVnd % 1000 != 0) amountVnd = ((amountVnd / 1000) + 1) * 1000;

        String description = "Rut" + (requestId % 100000);

        // cancelUrl riêng cho withdrawal — redirect về trang admin withdrawals
        // kèm withdrawalId để FE biết cần regenerate link
        String withdrawalCancelUrl = cancelUrl.replace("/payment/result", "/admin/withdrawals")
                + "?cancelled=true&withdrawalId=" + requestId;
        String withdrawalReturnUrl = returnUrl.replace("/payment/result", "/admin/withdrawals")
                + "?paid=true&orderCode=" + orderCode;

        PayOSCreateResponse payosResponse;
        try {
            payosResponse = payOSService.createPaymentLink(
                    orderCode, amountVnd, description, withdrawalReturnUrl, withdrawalCancelUrl);
        } catch (Exception e) {
            log.error("PayOS createPaymentLink for withdrawal {} failed: {}", requestId, e.getMessage(), e);
            throw new AppException(ErrorCode.PAYOS_ERROR);
        }

        if (payosResponse == null || !payosResponse.isSuccess() || payosResponse.getData() == null) {
            log.error("PayOS error for withdrawal {}: code={}", requestId,
                    payosResponse != null ? payosResponse.getCode() : "null");
            throw new AppException(ErrorCode.PAYOS_ERROR);
        }

        wr.setStatus("PAYING");
        wr.setAdminNote(adminNote);
        wr.setPayosOrderCode(orderCode);
        wr.setCheckoutUrl(payosResponse.getData().getCheckoutUrl());
        withdrawalRepository.save(wr);

        log.info("Withdrawal {} → PAYING — orderCode={} checkoutUrl={}",
                requestId, orderCode, payosResponse.getData().getCheckoutUrl());
        return toWithdrawalResponse(wr);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse regeneratePayoutLink(int requestId) {
        WithdrawalRequest wr = withdrawalRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        // Chỉ cho phép regenerate khi đang PAYING (link cũ đã bị huỷ/hết hạn)
        if (!"PAYING".equals(wr.getStatus())) {
            throw new AppException(ErrorCode.WITHDRAWAL_ALREADY_PROCESSED);
        }

        // Tạo orderCode mới
        long newOrderCode = ThreadLocalRandom.current().nextLong(10_000_000L, 99_999_999L);
        int amountVnd = wr.getAmount().intValue();
        if (amountVnd < 2000) amountVnd = 2000;
        if (amountVnd % 1000 != 0) amountVnd = ((amountVnd / 1000) + 1) * 1000;

        String description = "Rut" + (requestId % 100000);

        // cancelUrl riêng cho withdrawal
        String withdrawalCancelUrl = cancelUrl.replace("/payment/result", "/admin/withdrawals")
                + "?cancelled=true&withdrawalId=" + requestId;
        String withdrawalReturnUrl = returnUrl.replace("/payment/result", "/admin/withdrawals")
                + "?paid=true&orderCode=" + newOrderCode;

        PayOSCreateResponse payosResponse;
        try {
            payosResponse = payOSService.createPaymentLink(
                    newOrderCode, amountVnd, description, withdrawalReturnUrl, withdrawalCancelUrl);
        } catch (Exception e) {
            log.error("PayOS regenerate link for withdrawal {} failed: {}", requestId, e.getMessage(), e);
            throw new AppException(ErrorCode.PAYOS_ERROR);
        }

        if (payosResponse == null || !payosResponse.isSuccess() || payosResponse.getData() == null) {
            log.error("PayOS error on regenerate for withdrawal {}: code={}", requestId,
                    payosResponse != null ? payosResponse.getCode() : "null");
            throw new AppException(ErrorCode.PAYOS_ERROR);
        }

        // Cập nhật orderCode + checkoutUrl mới, giữ nguyên status PAYING
        wr.setPayosOrderCode(newOrderCode);
        wr.setCheckoutUrl(payosResponse.getData().getCheckoutUrl());
        withdrawalRepository.save(wr);

        log.info("Withdrawal {} — regenerated PayOS link: newOrderCode={} checkoutUrl={}",
                requestId, newOrderCode, payosResponse.getData().getCheckoutUrl());
        return toWithdrawalResponse(wr);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse confirmPayout(long orderCode) {
        WithdrawalRequest wr = withdrawalRepository.findByPayosOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        if (!"PAYING".equals(wr.getStatus())) {
            // Idempotent — đã xử lý rồi
            return toWithdrawalResponse(wr);
        }

        // Xác nhận với PayOS
        PayOSPaymentInfoResponse info;
        try {
            info = payOSService.getPaymentInfo(orderCode);
        } catch (Exception e) {
            log.error("PayOS getPaymentInfo for withdrawal orderCode={} failed: {}", orderCode, e.getMessage());
            throw new AppException(ErrorCode.PAYOS_ERROR);
        }

        String payosStatus = info.getPaymentStatus();
        log.info("Payout confirmPayout — orderCode={} payosStatus={}", orderCode, payosStatus);

        if (!"PAID".equals(payosStatus)) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND); // reuse: payment not completed
        }

        // Đánh dấu APPROVED + cộng totalWithdrawn
        wr.setStatus("APPROVED");
        wr.setProcessedAt(LocalDateTime.now());
        withdrawalRepository.save(wr);

        ShopWallet wallet = getOrCreateWallet(wr.getShop());
        wallet.setTotalWithdrawn(safe(wallet.getTotalWithdrawn()).add(wr.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Ghi Transaction WITHDRAWAL SUCCESS
        transactionRepository.save(Transaction.builder()
                .shop(wr.getShop())
                .withdrawal(wr)
                .type("WITHDRAWAL")
                .amount(wr.getAmount())
                .paymentMethod("PAYOS")
                .status("SUCCESS")
                .payosOrderCode(orderCode)
                .description("Payout to " + wr.getAccountHolder()
                        + " (" + wr.getBankName() + " " + wr.getBankAccount() + ")")
                .completedAt(LocalDateTime.now())
                .build());

        log.info("Withdrawal {} APPROVED after PayOS payout — shop={} amount={}",
                wr.getId(), wr.getShop().getShopName(), wr.getAmount());
        return toWithdrawalResponse(wr);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse confirmManualWithdrawal(int requestId) {
        WithdrawalRequest wr = withdrawalRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        if (!"PENDING".equals(wr.getStatus()) && !"PAYING".equals(wr.getStatus())) {
            throw new AppException(ErrorCode.WITHDRAWAL_ALREADY_PROCESSED);
        }

        wr.setStatus("APPROVED");
        wr.setProcessedAt(LocalDateTime.now());
        withdrawalRepository.save(wr);

        ShopWallet wallet = getOrCreateWallet(wr.getShop());
        wallet.setTotalWithdrawn(safe(wallet.getTotalWithdrawn()).add(wr.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .shop(wr.getShop())
                .withdrawal(wr)
                .type("WITHDRAWAL")
                .amount(wr.getAmount())
                .paymentMethod("MANUAL")
                .status("SUCCESS")
                .description("Manual payout to " + wr.getAccountHolder()
                        + " (" + wr.getBankName() + " " + wr.getBankAccount() + ")")
                .completedAt(LocalDateTime.now())
                .build());

        log.info("Withdrawal {} APPROVED manually — shop={} amount={}",
                wr.getId(), wr.getShop().getShopName(), wr.getAmount());
        return toWithdrawalResponse(wr);
    }

    @Override
    @Transactional
    public void confirmRefundForBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (!"WAITING_REFUND".equals(booking.getStatus()) && !"CANCELLED".equals(booking.getStatus())) {
            throw new AppException(ErrorCode.BOOKING_CANCELLED);
        }

        BigDecimal refundAmount = booking.getRefundAmount() != null ? booking.getRefundAmount() : BigDecimal.ZERO;

        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        // Cap refundAmount theo số tiền thực tế đã thanh toán bất kể trạng thái payment
        // (vì lúc booking WAITING_REFUND thì payment.status đã là WAITING_REFUND, không còn SUCCESS)
        if (payment != null && payment.getAmount() != null) {
            if (refundAmount.compareTo(payment.getAmount()) > 0) {
                refundAmount = payment.getAmount();
                booking.setRefundAmount(refundAmount);
                bookingRepository.save(booking);
            }
        }

        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        ShopWallet wallet = getOrCreateWallet(booking.getShop());

        // Từ bản cập nhật: Shop được cộng tiền ngay lập tức khi thanh toán 100% thành công.
        // Do đó khi hoàn tiền, cần phải trừ lại số tiền đã cộng cho Shop.
        if (payment != null && "SUCCESS".equals(payment.getStatus())) {
            String method = payment.getMethod();
            if (!"CASH_DEPOSIT".equals(method) && !"CASH".equals(method)) {
                // Đây là thanh toán 100% online
                BigDecimal fullPrice = (booking.getServices() != null && !booking.getServices().isEmpty())
                        ? booking.getServices().stream().map(s -> s.getPrice() != null ? s.getPrice() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add)
                        : BigDecimal.ZERO;
                BigDecimal adminCommission = calculateAdminCommissionForBooking(booking);
                BigDecimal shopShare = fullPrice.subtract(adminCommission).setScale(0, RoundingMode.DOWN);

                BigDecimal paidAmount = payment.getAmount();
                BigDecimal deductionAmount = shopShare;
                
                if (paidAmount != null && paidAmount.compareTo(BigDecimal.ZERO) > 0) {
                    deductionAmount = shopShare.multiply(refundAmount).divide(paidAmount, 0, RoundingMode.HALF_UP);
                }

                deductRefundedAmount(bookingId, deductionAmount);
            }
        }

        // Ghi Transaction REFUND
        transactionRepository.save(Transaction.builder()
                .shop(booking.getShop())
                .type("REFUND")
                .amount(refundAmount)
                .status("SUCCESS")
                .description("Refund for booking #" + bookingId + " to " + (booking.getUser() != null ? booking.getUser().getFullName() : "customer"))
                .completedAt(LocalDateTime.now())
                .build());

        // Update booking status to CANCELLED once refund is processed
        if ("WAITING_REFUND".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
            cameraService.stopStream(bookingId);
            sendBookingUpdateEvent(booking.getShop().getId(), bookingId);
        }

        log.info("Refund for booking {} processed — shop={} amount={}", bookingId, booking.getShop().getShopName(), refundAmount);
    }

    @Override
    @Transactional
    public void creditShopPenalty(int bookingId, BigDecimal penaltyAmount, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (penaltyAmount == null || penaltyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        ShopWallet wallet = getOrCreateWallet(booking.getShop());
        
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).add(penaltyAmount));
        wallet.setTotalEarned(safe(wallet.getTotalEarned()).add(penaltyAmount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .shop(booking.getShop())
                .type("WALLET_CREDIT")
                .amount(penaltyAmount)
                .description(String.format("Tiền bồi thường do khách hàng %s đơn #%d", reason, bookingId))
                .completedAt(LocalDateTime.now())
                .build());

        log.info("Credited NO_SHOW penalty to Shop {} for booking {} - Amount: {}", 
                booking.getShop().getShopName(), bookingId, penaltyAmount);
    }

    @Override
    @Transactional
    public void deductRefundedAmount(int bookingId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        // ── Xác định phương thức thanh toán để quyết định có trừ ví không ──────
        Payment payment = paymentRepository.findByBookingId(bookingId).orElse(null);
        String paymentMethod = payment != null ? payment.getMethod() : null;

        // CASH_DEPOSIT: khách chỉ trả tiền cọc qua PayOS, phần còn lại thu tiền mặt tại quầy.
        // Ví shop KHÔNG được cộng tiền khi booking thành công (không có WALLET_CREDIT),
        // nên khi hoàn tiền KHÔNG được trừ ví shop — chỉ hoàn đúng phần cọc cho khách từ phía admin.
        boolean isCashDeposit = "CASH_DEPOSIT".equals(paymentMethod)
                || "MOCK_CASH_DEPOSIT".equals(paymentMethod);

        if (isCashDeposit) {
            // Ví shop chưa bao giờ được cộng phần này → bỏ qua khấu trừ ví.
            // Chỉ đánh dấu các transaction liên quan sang REFUNDED để ghi nhận.
            transactionRepository.findByBookingIdOrderByCreatedAtDesc(bookingId).stream()
                    .filter(t -> "WALLET_CREDIT".equals(t.getType()) && "WAITING_REFUND".equals(t.getStatus()))
                    .forEach(t -> {
                        t.setStatus("REFUNDED");
                        transactionRepository.save(t);
                    });

            transactionRepository.save(Transaction.builder()
                    .shop(booking.getShop())
                    .booking(booking)
                    .type("REFUND_DEDUCTION")
                    .amount(amount)
                    .status("SKIPPED")
                    .description(String.format(
                            "Đơn đặt cọc tại quầy #%d — hoàn đúng phần cọc cho khách, ví shop không bị trừ vì chưa được cộng",
                            bookingId))
                    .completedAt(LocalDateTime.now())
                    .build());

            log.info("CASH_DEPOSIT booking {} — wallet deduction SKIPPED (shop was never credited). Refund amount: {}",
                    bookingId, amount);
            return;
        }

        // ── Thanh toán 100% online (PAYOS, v.v.) ────────────────────────────────
        // Ví shop đã được cộng shopShare ngay khi thanh toán thành công → trừ lại theo tỷ lệ hoàn.
        ShopWallet wallet = getOrCreateWallet(booking.getShop());
        
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).subtract(amount));
        wallet.setTotalEarned(safe(wallet.getTotalEarned()).subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Cập nhật WALLET_CREDIT cũ của booking này sang REFUNDED
        transactionRepository.findByBookingIdOrderByCreatedAtDesc(bookingId).stream()
                .filter(t -> "WALLET_CREDIT".equals(t.getType()) && "WAITING_REFUND".equals(t.getStatus()))
                .forEach(t -> {
                    t.setStatus("REFUNDED");
                    transactionRepository.save(t);
                });

        transactionRepository.save(Transaction.builder()
                .shop(booking.getShop())
                .booking(booking)
                .type("REFUND_DEDUCTION")
                .amount(amount)
                .status("SUCCESS")
                .description(String.format("Khấu trừ số dư do đơn hàng bị hủy/hoàn tiền 100%% online #%d", bookingId))
                .completedAt(LocalDateTime.now())
                .build());

        log.info("Deducted REFUNDED AMOUNT from Shop {} for booking {} - Amount: {}", 
                booking.getShop().getShopName(), bookingId, amount);
    }

    @Override
    @Transactional
    public void deductShopPenalty(int shopId, BigDecimal amount, int bookingId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));

        ShopWallet wallet = getOrCreateWallet(shop);
        
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).subtract(amount));
        wallet.setTotalEarned(safe(wallet.getTotalEarned()).subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        transactionRepository.save(Transaction.builder()
                .shop(shop)
                .type("PENALTY")
                .amount(amount)
                .status("SUCCESS")
                .description(String.format("Phí phạt mất cọc do Shop tự hủy đơn #%d", bookingId))
                .completedAt(LocalDateTime.now())
                .build());

        log.info("Deducted PENALTY from Shop {} for booking {} - Amount: {}", 
                shop.getShopName(), bookingId, amount);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse rejectWithdrawal(int requestId, String adminNote) {
        WithdrawalRequest wr = withdrawalRepository.findById(requestId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAWAL_NOT_FOUND));

        if (!"PENDING".equals(wr.getStatus()) && !"PAYING".equals(wr.getStatus())) {
            throw new AppException(ErrorCode.WITHDRAWAL_ALREADY_PROCESSED);
        }

        wr.setStatus("REJECTED");
        wr.setAdminNote(adminNote);
        wr.setProcessedAt(LocalDateTime.now());
        withdrawalRepository.save(wr);

        // Hoàn lại tiền vào available
        ShopWallet wallet = getOrCreateWallet(wr.getShop());
        wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).add(wr.getAmount()));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        log.info("Withdrawal {} REJECTED — shop={} amount refunded", requestId, wr.getShop().getShopName());
        return toWithdrawalResponse(wr);
    }

    @Override
    @Transactional
    public void expireStalePayouts() {
        // Tìm tất cả PAYING đã quá 24h
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<WithdrawalRequest> stale = withdrawalRepository.findStalePayouts(cutoff);

        if (stale.isEmpty()) {
            log.debug("expireStalePayouts: no stale payouts found");
            return;
        }

        log.info("expireStalePayouts: found {} stale PAYING withdrawal(s)", stale.size());

        for (WithdrawalRequest wr : stale) {
            try {
                wr.setStatus("EXPIRED");
                wr.setAdminNote("Tự động hết hạn sau 24h không xác nhận thanh toán.");
                wr.setProcessedAt(LocalDateTime.now());
                withdrawalRepository.save(wr);

                // Hoàn tiền về availableBalance của shop
                ShopWallet wallet = getOrCreateWallet(wr.getShop());
                wallet.setAvailableBalance(safe(wallet.getAvailableBalance()).add(wr.getAmount()));
                wallet.setUpdatedAt(LocalDateTime.now());
                walletRepository.save(wallet);

                // Gửi thông báo cho shop owner
                User shopOwner = wr.getShop().getOwner();
                if (shopOwner != null) {
                    Notification notif = Notification.builder()
                            .user(shopOwner)
                            .title("Yêu cầu rút tiền hết hạn")
                            .content(String.format(
                                    "Yêu cầu rút %s của bạn (#{%d}) đã hết hạn sau 24h Admin chưa xác nhận. " +
                                    "Số tiền đã được hoàn lại vào ví. Bạn có thể tạo yêu cầu mới.",
                                    formatVND(wr.getAmount()), wr.getId()))
                            .build();
                    notificationRepository.save(notif);
                }

                log.info("Withdrawal {} EXPIRED — shop={} amount={} refunded",
                        wr.getId(), wr.getShop().getShopName(), wr.getAmount());
            } catch (Exception e) {
                log.error("Failed to expire withdrawal {}: {}", wr.getId(), e.getMessage(), e);
            }
        }
    }
}
