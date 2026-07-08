package com.sang.sourcepattern.service.impl;

import com.sang.sourcepattern.dto.request.SendNotificationRequest;
import com.sang.sourcepattern.dto.response.*;
import com.sang.sourcepattern.entity.*;
import com.sang.sourcepattern.enums.ShopStatus;
import com.sang.sourcepattern.exception.AppException;
import com.sang.sourcepattern.exception.ErrorCode;
import com.sang.sourcepattern.mapper.ShopMapper;
import com.sang.sourcepattern.repository.*;
import com.sang.sourcepattern.service.AdminService;
import com.sang.sourcepattern.service.GoongMapService;
import com.sang.sourcepattern.dto.response.goong.LatLong;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminServiceImpl implements AdminService {

    PaymentRepository paymentRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ShopRepository shopRepository;
    NotificationRepository notificationRepository;
    MessageRepository messageRepository;
    TransactionRepository transactionRepository;
    WithdrawalRequestRepository withdrawalRequestRepository;
    WalletServiceImpl walletService;

    GoongMapService goongMapService;
    ShopMapper shopMapper;

    @Override
    public Map<String, Object> getDashboard(LocalDate startDate, LocalDate endDate) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime periodEnd = (endDate != null) ? endDate.atTime(23, 59, 59) : now;
        LocalDateTime periodStart = (startDate != null) ? startDate.atStartOfDay() : periodEnd.minusDays(30);

        long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(periodStart.toLocalDate(), periodEnd.toLocalDate()) + 1;
        if (daysDiff <= 0) daysDiff = 1;

        LocalDateTime prevStart = periodStart.minusDays(daysDiff);
        LocalDateTime prevEnd = periodStart.minusSeconds(1);

        // Core stats
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        long totalUsers = userRepository.count();
        long totalShops = shopRepository.count();
        long totalBookings = bookingRepository.count();
        long pendingShops = shopRepository.findAll().stream()
                .filter(s -> s.getStatus() == ShopStatus.PENDING).count();
        long unreadMessages = messageRepository
                .countByShopIdAndChannelTypeAndIsReadFalseAndSenderRoleNot(0, "ADMIN_SUPPORT", "ADMIN");

        // Period stats
        BigDecimal periodRevenue = bookingRepository.sumRevenueBetween(periodStart, periodEnd);
        if (periodRevenue == null) periodRevenue = BigDecimal.ZERO;
        long periodUsers = userRepository.countUsersBetween(periodStart, periodEnd);
        long periodBookings = bookingRepository.countBookingsBetween(periodStart, periodEnd);

        // Calculate Revenue Trend (Current vs Prev period)
        BigDecimal revPrev = bookingRepository.sumRevenueBetween(prevStart, prevEnd);
        double revTrendVal = 0.0;
        if (revPrev != null && revPrev.compareTo(BigDecimal.ZERO) > 0 && periodRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revTrendVal = periodRevenue.subtract(revPrev).doubleValue() / revPrev.doubleValue() * 100;
        } else if ((revPrev == null || revPrev.compareTo(BigDecimal.ZERO) == 0) && periodRevenue.compareTo(BigDecimal.ZERO) > 0) {
            revTrendVal = 100.0;
        }
        String totalRevenueTrend = String.format("%s%.1f%%", revTrendVal >= 0 ? "+" : "", revTrendVal);
        Boolean totalRevenueTrendUp = revTrendVal >= 0;

        // Calculate Users Trend
        long usersPrev = userRepository.countUsersBetween(prevStart, prevEnd);
        double usersTrendVal = 0.0;
        if (usersPrev > 0) {
            usersTrendVal = (double) (periodUsers - usersPrev) / usersPrev * 100;
        } else if (usersPrev == 0 && periodUsers > 0) {
            usersTrendVal = 100.0;
        }
        String totalUsersTrend = String.format("%s%.1f%%", usersTrendVal >= 0 ? "+" : "", usersTrendVal);
        Boolean totalUsersTrendUp = usersTrendVal >= 0;

        // Calculate Bookings Trend
        long bookingsPrev = bookingRepository.countBookingsBetween(prevStart, prevEnd);
        double bookingsTrendVal = 0.0;
        if (bookingsPrev > 0) {
            bookingsTrendVal = (double) (periodBookings - bookingsPrev) / bookingsPrev * 100;
        } else if (bookingsPrev == 0 && periodBookings > 0) {
            bookingsTrendVal = 100.0;
        }
        String totalBookingsTrend = String.format("%s%.1f%%", bookingsTrendVal >= 0 ? "+" : "", bookingsTrendVal);
        Boolean totalBookingsTrendUp = bookingsTrendVal >= 0;

        long periodShops = shopRepository.countShopsBetween(periodStart, periodEnd);
        long periodPendingShops = shopRepository.countShopsByStatusBetween(ShopStatus.PENDING, periodStart, periodEnd);
        long periodMessages = messageRepository.countMessagesBetween(periodStart, periodEnd);
        
        long totalMessages = messageRepository.count();

        // Calculate Shops Trend
        long shopsPrev = shopRepository.countShopsBetween(prevStart, prevEnd);
        double shopsTrendVal = 0.0;
        if (shopsPrev > 0) { shopsTrendVal = (double) (periodShops - shopsPrev) / shopsPrev * 100; }
        else if (shopsPrev == 0 && periodShops > 0) { shopsTrendVal = 100.0; }
        String totalShopsTrend = String.format("%s%.1f%%", shopsTrendVal >= 0 ? "+" : "", shopsTrendVal);
        Boolean totalShopsTrendUp = shopsTrendVal >= 0;

        // Calculate Pending Shops Trend
        long pendingShopsPrev = shopRepository.countShopsByStatusBetween(ShopStatus.PENDING, prevStart, prevEnd);
        double pendingShopsTrendVal = 0.0;
        if (pendingShopsPrev > 0) { pendingShopsTrendVal = (double) (periodPendingShops - pendingShopsPrev) / pendingShopsPrev * 100; }
        else if (pendingShopsPrev == 0 && periodPendingShops > 0) { pendingShopsTrendVal = 100.0; }
        String pendingShopsTrend = String.format("%s%.1f%%", pendingShopsTrendVal >= 0 ? "+" : "", pendingShopsTrendVal);
        Boolean pendingShopsTrendUp = pendingShopsTrendVal >= 0;

        // Calculate Messages Trend
        long messagesPrev = messageRepository.countMessagesBetween(prevStart, prevEnd);
        double messagesTrendVal = 0.0;
        if (messagesPrev > 0) { messagesTrendVal = (double) (periodMessages - messagesPrev) / messagesPrev * 100; }
        else if (messagesPrev == 0 && periodMessages > 0) { messagesTrendVal = 100.0; }
        String totalMessagesTrend = String.format("%s%.1f%%", messagesTrendVal >= 0 ? "+" : "", messagesTrendVal);
        Boolean totalMessagesTrendUp = messagesTrendVal >= 0;

        // Calculate Sparklines (Last 8 days)
        List<Double> totalRevenueSparkData = new ArrayList<>();
        List<Long> totalUsersSparkData = new ArrayList<>();
        List<Long> totalBookingsSparkData = new ArrayList<>();
        List<Long> totalShopsSparkData = new ArrayList<>();
        List<Long> pendingShopsSparkData = new ArrayList<>();
        List<Long> totalMessagesSparkData = new ArrayList<>();

        for (int i = 7; i >= 0; i--) {
            LocalDateTime dayStart = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime dayEnd = LocalDate.now().minusDays(i).atTime(23, 59, 59);
            
            BigDecimal dayRev = bookingRepository.sumRevenueBetween(dayStart, dayEnd);
            totalRevenueSparkData.add(dayRev != null ? dayRev.doubleValue() : 0.0);
            
            totalUsersSparkData.add(userRepository.countUsersBetween(dayStart, dayEnd));
            totalBookingsSparkData.add(bookingRepository.countBookingsBetween(dayStart, dayEnd));
            totalShopsSparkData.add(shopRepository.countShopsBetween(dayStart, dayEnd));
            pendingShopsSparkData.add(shopRepository.countShopsByStatusBetween(ShopStatus.PENDING, dayStart, dayEnd));
            totalMessagesSparkData.add(messageRepository.countMessagesBetween(dayStart, dayEnd));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalRevenue", totalRevenue);
        result.put("periodRevenue", periodRevenue);
        result.put("totalRevenueTrend", totalRevenueTrend);
        result.put("totalRevenueTrendUp", totalRevenueTrendUp);
        result.put("totalRevenueSparkData", totalRevenueSparkData);

        result.put("totalUsers", totalUsers);
        result.put("periodUsers", periodUsers);
        result.put("totalUsersTrend", totalUsersTrend);
        result.put("totalUsersTrendUp", totalUsersTrendUp);
        result.put("totalUsersSparkData", totalUsersSparkData);

        result.put("totalShops", totalShops);
        result.put("totalShopsTrend", totalShopsTrend);
        result.put("totalShopsTrendUp", totalShopsTrendUp);
        result.put("totalShopsSparkData", totalShopsSparkData);

        result.put("totalBookings", totalBookings);
        result.put("periodBookings", periodBookings);
        result.put("totalBookingsTrend", totalBookingsTrend);
        result.put("totalBookingsTrendUp", totalBookingsTrendUp);
        result.put("totalBookingsSparkData", totalBookingsSparkData);

        result.put("pendingShops", pendingShops);
        result.put("pendingShopsTrend", pendingShopsTrend);
        result.put("pendingShopsTrendUp", pendingShopsTrendUp);
        result.put("pendingShopsSparkData", pendingShopsSparkData);

        result.put("unreadMessages", unreadMessages);
        result.put("totalMessages", totalMessages);
        result.put("totalMessagesTrend", totalMessagesTrend);
        result.put("totalMessagesTrendUp", totalMessagesTrendUp);
        result.put("totalMessagesSparkData", totalMessagesSparkData);

        return result;
    }

    @Override
    public List<MonthlyRevenueResponse> getMonthlyRevenue(Integer year) {
        int targetYear = (year != null) ? year : Year.now().getValue();

        Map<Integer, BigDecimal> revenueMap = new HashMap<>();
        for (Object[] row : bookingRepository.adminCommissionByMonth(targetYear)) {
            int month = ((Number) row[0]).intValue();
            BigDecimal revenue = new BigDecimal(row[1].toString());
            revenueMap.put(month, revenue);
        }

        List<MonthlyRevenueResponse> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(MonthlyRevenueResponse.builder()
                    .month(m)
                    .revenue(revenueMap.getOrDefault(m, BigDecimal.ZERO))
                    .build());
        }

        return result;
    }

    @Override
    public List<DailyBookingResponse> getWeeklyBookings() {
        LocalDate today = LocalDate.now();
        LocalDateTime from = today.minusDays(6).atStartOfDay();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : bookingRepository.bookingCountByDate(from)) {
            String date = row[0].toString().substring(0, 10);
            long count = ((Number) row[1]).longValue();
            countMap.put(date, count);
        }

        List<DailyBookingResponse> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String date = today.minusDays(i).format(fmt);
            result.add(DailyBookingResponse.builder()
                    .date(date)
                    .count(countMap.getOrDefault(date, 0L))
                    .build());
        }

        return result;
    }

    @Override
    public PageResponse<ShopResponse> getShops(ShopStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Shop> shopPage;
        
        if (status != null) {
            shopPage = shopRepository.findByStatus(status, pageable);
        } else {
            shopPage = shopRepository.findAll(pageable);
        }
        
        List<ShopResponse> content = shopPage.getContent().stream()
                .map(shopMapper::toShopResponse)
                .collect(Collectors.toList());
                
        return PageResponse.<ShopResponse>builder()
                .content(content)
                .page(shopPage.getNumber())
                .size(shopPage.getSize())
                .totalElements(shopPage.getTotalElements())
                .totalPages(shopPage.getTotalPages())
                .last(shopPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public ShopResponse updateShopStatus(int id, ShopStatus status) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
                
        shop.setStatus(status);
        if (status == ShopStatus.APPROVED) {
            shop.setVerified(true);
        } else if (status == ShopStatus.REJECTED) {
            shop.setVerified(false);
        }
        
        shopRepository.save(shop);
        return shopMapper.toShopResponse(shop);
    }

    @Override
    @Transactional
    public Map<String, Object> geocodeAllShops() {
        List<Shop> shops = shopRepository.findAll();
        int success = 0;
        int failed = 0;

        for (Shop shop : shops) {
            if (shop.getAddress() != null && !shop.getAddress().isEmpty()) {
                LatLong location = goongMapService.geocodeAddress(shop.getAddress());
                
                if (location != null) {
                    shop.setLatitude(location.getLatitude());
                    shop.setLongitude(location.getLongitude());
                    shopRepository.save(shop);
                    success++;
                } else {
                    failed++;
                }
            } else {
                failed++;
            }
        }

        return Map.of(
                "total", shops.size(),
                "success", success,
                "failed", failed
        );
    }

    @Override
    public PageResponse<NotificationBroadcastResponse> getAllNotifications(int page) {
        Page<Notification> pageResult = notificationRepository
                .findDistinctBroadcasts(PageRequest.of(page, 10));

        List<NotificationBroadcastResponse> content = pageResult.getContent().stream()
                .map(n -> {
                    String bId = n.getBroadcastId();
                    String targetStr = "ALL";
                    if (bId != null && bId.startsWith("SINGLE_")) {
                        targetStr = "SINGLE:" + (n.getUser() != null && n.getUser().getEmail() != null ? n.getUser().getEmail() : String.valueOf(n.getUser().getId()));
                    } else if (bId != null && bId.startsWith("ALL_USERS_")) {
                        targetStr = "ALL_USERS";
                    } else if (bId != null && bId.startsWith("ALL_SHOPS_")) {
                        targetStr = "ALL_SHOPS";
                    } else if (bId != null && bId.startsWith("ALL_")) {
                        targetStr = "ALL";
                    } else {
                        // fallback cho dữ liệu cũ không có prefix
                        long totalSent = notificationRepository.countByBroadcastId(n.getBroadcastId());
                        if (totalSent == 1 && n.getUser() != null) {
                            targetStr = "SINGLE:" + (n.getUser().getEmail() != null ? n.getUser().getEmail() : String.valueOf(n.getUser().getId()));
                        }
                    }
                    
                    long totalSent = notificationRepository.countByBroadcastId(n.getBroadcastId());
                    
                    return NotificationBroadcastResponse.builder()
                        .broadcastId(n.getBroadcastId())
                        .title(n.getTitle())
                        .content(n.getContent())
                        .targetType(targetStr)
                        .notificationType(n.getNotificationType() != null ? n.getNotificationType().name() : null)
                        .totalSent(totalSent)
                        .totalRead(notificationRepository.countByBroadcastIdAndIsReadTrue(n.getBroadcastId()))
                        .createdAt(n.getCreatedAt())
                        .build();
                })
                .collect(Collectors.toList());

        return PageResponse.<NotificationBroadcastResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    @Override
    @Transactional
    public void sendNotification(SendNotificationRequest request) {
        List<User> targets = switch (request.getTargetType()) {
            case SINGLE -> {
                User user;
                if (request.getUserId() != null) {
                    user = userRepository.findById(request.getUserId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                } else if (request.getTargetEmail() != null && !request.getTargetEmail().isEmpty()) {
                    user = userRepository.findByEmail(request.getTargetEmail())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                } else {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                }
                yield List.of(user);
            }
            case ALL_USERS -> userRepository.findByRoleName("USER");
            case ALL_SHOPS -> userRepository.findByRoleName("SHOP_OWNER");
            case ALL -> userRepository.findAll();
        };

        String broadcastId = request.getTargetType().name() + "_" + UUID.randomUUID().toString();

        List<Notification> notifications = targets.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .broadcastId(broadcastId)
                        .notificationType(request.getNotificationType() != null
                                ? Notification.NotificationType.valueOf(request.getNotificationType().name())
                                : Notification.NotificationType.GENERAL)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public int deleteNotification(String broadcastId) {
        List<Notification> group = notificationRepository.findByBroadcastId(broadcastId);
        int size = group.size();
        notificationRepository.deleteAll(group);
        return size;
    }

    @Override
    public Map<String, Object> getFinanceOverview() {
        BigDecimal totalRevenue = transactionRepository.sumTotalRevenue();
        BigDecimal todayRevenue = transactionRepository.sumTotalRevenueToday();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (todayRevenue == null) todayRevenue = BigDecimal.ZERO;
        
        BigDecimal commission = totalRevenue.multiply(new BigDecimal("0.10"));
        
        BigDecimal totalWithdrawn = withdrawalRequestRepository.sumTotalApprovedWithdrawals();
        if (totalWithdrawn == null) totalWithdrawn = BigDecimal.ZERO;
        
        BigDecimal systemBalance = totalRevenue.subtract(commission).subtract(totalWithdrawn);
        
        long pendingShopWithdrawals = withdrawalRequestRepository.countByTypeAndStatus("SHOP", "PENDING");
        long pendingUserWithdrawals = withdrawalRequestRepository.countByTypeAndStatus("USER", "PENDING");
        
        Map<String, Object> result = new HashMap<>();
        result.put("systemBalance", systemBalance);
        result.put("todayRevenue", todayRevenue);
        result.put("commission", commission);
        result.put("pendingShopWithdrawals", pendingShopWithdrawals);
        result.put("pendingUserWithdrawals", pendingUserWithdrawals);
        
        return result;
    }

    @Override
    public Page<TransactionResponse> getFinanceTransactions(int page, int size, String type) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions;
        if (type != null && !type.isEmpty()) {
            transactions = transactionRepository.findAllByTypeOrderByCreatedAtDesc(type, pageable);
        } else {
            transactions = transactionRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return transactions.map(t -> {
            String shopName = null;
            if (t.getShop() != null) {
                shopName = t.getShop().getShopName();
            } else if (t.getUser() != null) {
                shopName = t.getUser().getEmail();
            } else if (t.getBooking() != null && t.getBooking().getShop() != null) {
                shopName = t.getBooking().getShop().getShopName();
            }
            
            return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType())
                .amount(t.getAmount())
                .paymentMethod(t.getPaymentMethod())
                .status(t.getStatus())
                .payosOrderCode(t.getPayosOrderCode())
                .gatewayTransactionId(t.getGatewayTransactionId())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .bookingId(t.getBooking() != null ? t.getBooking().getId() : null)
                .shopName(shopName)
                .build();
        });
    }

    private WithdrawalRequestResponse mapToWithdrawalResponse(WithdrawalRequest w) {
        return WithdrawalRequestResponse.builder()
                .id(w.getId())
                .shopId(w.getShop() != null ? w.getShop().getId() : 0)
                .shopName(w.getShop() != null ? w.getShop().getShopName() : "")
                .userId(w.getUser() != null ? w.getUser().getId() : null)
                .userEmail(w.getUser() != null ? w.getUser().getEmail() : "")
                .type(w.getType())
                .amount(w.getAmount())
                .bankName(w.getBankName())
                .bankAccount(w.getBankAccount())
                .accountHolder(w.getAccountHolder())
                .note(w.getNote())
                .status(w.getStatus())
                .adminNote(w.getAdminNote())
                .createdAt(w.getCreatedAt() != null ? w.getCreatedAt().toString() : null)
                .bookingId(w.getBooking() != null ? w.getBooking().getId() : null)
                .build();
    }

    @Override
    public Page<WithdrawalRequestResponse> getShopWithdrawals(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WithdrawalRequest> requests;
        if (status != null && !status.isEmpty()) {
            requests = withdrawalRequestRepository.findByTypeAndStatusOrderByCreatedAtDesc("SHOP", status, pageable);
        } else {
            requests = withdrawalRequestRepository.findByTypeOrderByCreatedAtDesc("SHOP", pageable);
        }
        return requests.map(this::mapToWithdrawalResponse);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse updateShopWithdrawalStatus(int id, String status, String adminNote) {
        WithdrawalRequest request = withdrawalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));
        
        if ("APPROVED".equals(status)) {
            BigDecimal totalRevenue = transactionRepository.sumTotalRevenue();
            if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
            BigDecimal commission = totalRevenue.multiply(new BigDecimal("0.10"));
            
            BigDecimal totalWithdrawn = withdrawalRequestRepository.sumTotalApprovedWithdrawals();
            if (totalWithdrawn == null) totalWithdrawn = BigDecimal.ZERO;
            
            BigDecimal systemBalance = totalRevenue.subtract(commission).subtract(totalWithdrawn);
            
            if (systemBalance.compareTo(request.getAmount()) < 0) {
                throw new RuntimeException("Số dư hệ thống không đủ để duyệt yêu cầu này!");
            }
        }
        
        request.setStatus(status);
        if (adminNote != null) {
            request.setAdminNote(adminNote);
        }
        withdrawalRequestRepository.save(request);

        if ("APPROVED".equals(status)) {
            Transaction txn = Transaction.builder()
                .type("WITHDRAWAL")
                .status("SUCCESS")
                .amount(request.getAmount())
                .shop(request.getShop())
                .withdrawal(request)
                .paymentMethod("TRANSFER")
                .description("Rút tiền Shop")
                .build();
            transactionRepository.save(txn);
        }
        
        if ("APPROVED".equals(status) || "REJECTED".equals(status)) {
            User owner = request.getShop() != null ? request.getShop().getOwner() : null;
            if (owner != null) {
                String title = "APPROVED".equals(status) ? "Yêu cầu rút tiền được duyệt" : "Yêu cầu rút tiền bị từ chối";
                String content = "APPROVED".equals(status) 
                    ? "Yêu cầu rút " + String.format("%,.0f đ", request.getAmount()) + " của shop đã được admin duyệt."
                    : "Yêu cầu rút " + String.format("%,.0f đ", request.getAmount()) + " của shop đã bị từ chối.";
                
                if (adminNote != null && !adminNote.trim().isEmpty()) {
                    content += " Lời nhắn từ Admin: " + adminNote;
                }
                
                Notification notification = Notification.builder()
                        .user(owner)
                        .title(title)
                        .content(content)
                        .notificationType(Notification.NotificationType.SYSTEM)
                        .build();
                notificationRepository.save(notification);
            }
        }
        
        return mapToWithdrawalResponse(request);
    }

    @Override
    public Page<WithdrawalRequestResponse> getUserWithdrawals(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<WithdrawalRequest> requests;
        if (status != null && !status.isEmpty()) {
            requests = withdrawalRequestRepository.findByTypeAndStatusOrderByCreatedAtDesc("USER", status, pageable);
        } else {
            requests = withdrawalRequestRepository.findByTypeOrderByCreatedAtDesc("USER", pageable);
        }
        return requests.map(this::mapToWithdrawalResponse);
    }

    @Override
    @Transactional
    public WithdrawalRequestResponse updateUserWithdrawalStatus(int id, String status, String adminNote) {
        WithdrawalRequest request = withdrawalRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Withdrawal request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Yêu cầu này đã được xử lý rồi!");
        }

        request.setStatus(status);
        if (adminNote != null) {
            request.setAdminNote(adminNote);
        }
        request.setProcessedAt(LocalDateTime.now());
        withdrawalRequestRepository.save(request);

        if ("APPROVED".equals(status) && request.getBooking() != null) {
            Booking booking = request.getBooking();
            int bookingId = booking.getId();

            // Tính toán tiền phạt (penalty) dựa trên giờ hủy
            java.math.BigDecimal totalPrice = (booking.getServices() != null && !booking.getServices().isEmpty())
                    ? booking.getServices().stream()
                        .map(s -> s.getPrice() != null ? s.getPrice() : java.math.BigDecimal.ZERO)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                    : java.math.BigDecimal.ZERO;

            long hoursToAppointment = 0;
            if (booking.getAppointmentDatetime() != null && booking.getCancellationRequestedAt() != null) {
                hoursToAppointment = java.time.Duration.between(
                        booking.getCancellationRequestedAt(), booking.getAppointmentDatetime()).toHours();
            }

            if (hoursToAppointment < 5 && totalPrice.compareTo(java.math.BigDecimal.ZERO) > 0) {
                // Hủy muộn: cộng 50% tiền bồi thường vào ví Shop
                java.math.BigDecimal penalty = totalPrice.multiply(new java.math.BigDecimal("0.5"));
                walletService.creditShopPenalty(bookingId, penalty, "hủy muộn");
            }

            // Chuyển Booking sang CANCELLED
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            // Cập nhật Payment
            paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
                p.setStatus("CANCELLED");
                paymentRepository.save(p);
            });

            // Ghi Transaction hoàn tiền
            Transaction refundTxn = Transaction.builder()
                    .type("REFUND")
                    .status("SUCCESS")
                    .amount(request.getAmount())
                    .user(request.getUser())
                    .withdrawal(request)
                    .paymentMethod("TRANSFER")
                    .description(String.format("Admin hoàn tiền đơn hủy #%d cho %s",
                            bookingId,
                            request.getUser() != null ? request.getUser().getFullName() : "khách hàng"))
                    .completedAt(LocalDateTime.now())
                    .build();
            transactionRepository.save(refundTxn);
        }

        // Thông báo User
        if ("APPROVED".equals(status) || "REJECTED".equals(status)) {
            User user = request.getUser();
            if (user != null) {
                String amountStr = String.format("%,.0f đ", request.getAmount());
                String title = "APPROVED".equals(status) ? "Yêu cầu hoàn tiền được duyệt" : "Yêu cầu hoàn tiền bị từ chối";
                String content = "APPROVED".equals(status)
                        ? "Yêu cầu hoàn " + amountStr + " của bạn đã được Admin duyệt. Tiền sẽ chuyển về tài khoản của bạn."
                        : "Yêu cầu hoàn " + amountStr + " của bạn đã bị từ chối.";
                if (adminNote != null && !adminNote.trim().isEmpty()) {
                    content += " Lời nhắn từ Admin: " + adminNote;
                }
                Notification notification = Notification.builder()
                        .user(user)
                        .title(title)
                        .content(content)
                        .notificationType(Notification.NotificationType.SYSTEM)
                        .build();
                notificationRepository.save(notification);
            }
        }
        return mapToWithdrawalResponse(request);
    }
}
