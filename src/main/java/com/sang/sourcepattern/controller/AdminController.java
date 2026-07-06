package com.sang.sourcepattern.controller;

import com.sang.sourcepattern.dto.request.SendNotificationRequest;
import com.sang.sourcepattern.dto.response.DailyBookingResponse;
import com.sang.sourcepattern.dto.response.MonthlyRevenueResponse;
import com.sang.sourcepattern.dto.response.ApiResponse;
import com.sang.sourcepattern.dto.response.NotificationBroadcastResponse;
import com.sang.sourcepattern.dto.response.PageResponse;
import com.sang.sourcepattern.dto.response.ShopResponse;
import com.sang.sourcepattern.dto.response.TransactionResponse;
import com.sang.sourcepattern.dto.response.WithdrawalRequestResponse;
import com.sang.sourcepattern.entity.Transaction;
import com.sang.sourcepattern.enums.ShopStatus;
import com.sang.sourcepattern.service.AdminService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin")
public class AdminController {

    AdminService adminService;

    // ─── Dashboard ───────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ApiResponse.<Map<String, Object>>builder()
                .result(adminService.getDashboard(startDate, endDate))
                .build();
    }

    // ─── Dashboard charts ─────────────────────────────────────────────────────

    @GetMapping("/dashboard/revenue-monthly")
    public ApiResponse<List<MonthlyRevenueResponse>> getMonthlyRevenue(
            @RequestParam(required = false) Integer year) {
        return ApiResponse.<List<MonthlyRevenueResponse>>builder()
                .result(adminService.getMonthlyRevenue(year))
                .build();
    }

    @GetMapping("/dashboard/bookings-weekly")
    public ApiResponse<List<DailyBookingResponse>> getWeeklyBookings() {
        return ApiResponse.<List<DailyBookingResponse>>builder()
                .result(adminService.getWeeklyBookings())
                .build();
    }

    // ─── Shops Management ───────────────────────────────────────────────────

    @GetMapping("/shops")
    public ApiResponse<PageResponse<ShopResponse>> getShops(
            @RequestParam(required = false) ShopStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ShopResponse>>builder()
                .result(adminService.getShops(status, page, size))
                .build();
    }

    @PutMapping("/shops/{id}/status")
    public ApiResponse<ShopResponse> updateShopStatus(
            @PathVariable int id,
            @RequestParam ShopStatus status) {
        return ApiResponse.<ShopResponse>builder()
                .result(adminService.updateShopStatus(id, status))
                .message("Shop status updated successfully")
                .build();
    }

    // ─── Notifications ───────────────────────────────────────────────────────    

    @GetMapping("/notifications")
    public ApiResponse<PageResponse<NotificationBroadcastResponse>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page) {
        return ApiResponse.<PageResponse<NotificationBroadcastResponse>>builder()
                .result(adminService.getAllNotifications(page))
                .build();
    }

    @PostMapping("/notifications")
    public ApiResponse<Void> sendNotification(@RequestBody @Valid SendNotificationRequest request) {
        adminService.sendNotification(request);
        return ApiResponse.<Void>builder()
                .message("Notifications sent successfully")
                .build();
    }

    @DeleteMapping("/notifications/{broadcastId}")
    public ApiResponse<Void> deleteNotification(@PathVariable String broadcastId) {
        int count = adminService.deleteNotification(broadcastId);
        return ApiResponse.<Void>builder()
                .message("Deleted " + count + " notification(s)")
                .build();
    }

    // ─── Geocode all shops ───────────────────────────────────────────────────

    @PostMapping("/shops/geocode-all")
    public ApiResponse<Map<String, Object>> geocodeAllShops() {
        Map<String, Object> result = adminService.geocodeAllShops();
        return ApiResponse.<Map<String, Object>>builder()
                .result(result)
                .message("Geocoded " + result.get("success") + " shops successfully")
                .build();
    }

    // ─── Finance Overview ────────────────────────────────────────────────────

    @GetMapping("/finance/overview")
    public ApiResponse<Map<String, Object>> getFinanceOverview() {
        return ApiResponse.<Map<String, Object>>builder()
                .code(1000)
                .result(adminService.getFinanceOverview())
                .build();
    }

    @GetMapping("/finance/transactions")
    public ApiResponse<Page<TransactionResponse>> getFinanceTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        return ApiResponse.<Page<TransactionResponse>>builder()
                .code(1000)
                .result(adminService.getFinanceTransactions(page, size, type))
                .build();
    }

    // ─── Withdrawals ─────────────────────────────────────────────────────────

    @GetMapping("/finance/withdrawals/shop")
    public ApiResponse<Page<WithdrawalRequestResponse>> getShopWithdrawals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ApiResponse.<Page<WithdrawalRequestResponse>>builder()
                .code(1000)
                .result(adminService.getShopWithdrawals(page, size, status))
                .build();
    }

    @PutMapping("/finance/withdrawals/shop/{id}/status")
    public ApiResponse<WithdrawalRequestResponse> updateShopWithdrawalStatus(
            @PathVariable int id,
            @RequestParam String status,
            @RequestParam(required = false) String adminNote) {
        return ApiResponse.<WithdrawalRequestResponse>builder()
                .code(1000)
                .result(adminService.updateShopWithdrawalStatus(id, status, adminNote))
                .build();
    }

    @GetMapping("/finance/withdrawals/user")
    public ApiResponse<Page<WithdrawalRequestResponse>> getUserWithdrawals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        return ApiResponse.<Page<WithdrawalRequestResponse>>builder()
                .code(1000)
                .result(adminService.getUserWithdrawals(page, size, status))
                .build();
    }

    @PutMapping("/finance/withdrawals/user/{id}/status")
    public ApiResponse<WithdrawalRequestResponse> updateUserWithdrawalStatus(
            @PathVariable int id,
            @RequestParam String status,
            @RequestParam(required = false) String adminNote) {
        return ApiResponse.<WithdrawalRequestResponse>builder()
                .code(1000)
                .result(adminService.updateUserWithdrawalStatus(id, status, adminNote))
                .build();
    }
}
