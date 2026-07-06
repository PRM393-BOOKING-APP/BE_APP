package com.sang.sourcepattern.service;

import com.sang.sourcepattern.dto.request.SendNotificationRequest;
import com.sang.sourcepattern.dto.response.DailyBookingResponse;
import com.sang.sourcepattern.dto.response.MonthlyRevenueResponse;
import com.sang.sourcepattern.dto.response.NotificationBroadcastResponse;
import com.sang.sourcepattern.dto.response.PageResponse;
import com.sang.sourcepattern.dto.response.ShopResponse;
import com.sang.sourcepattern.dto.response.TransactionResponse;
import com.sang.sourcepattern.dto.response.WithdrawalRequestResponse;
import com.sang.sourcepattern.entity.Transaction;
import com.sang.sourcepattern.enums.ShopStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {
    
    // Dashboard
    Map<String, Object> getDashboard(LocalDate startDate, LocalDate endDate);
    List<MonthlyRevenueResponse> getMonthlyRevenue(Integer year);
    List<DailyBookingResponse> getWeeklyBookings();

    // Shops
    PageResponse<ShopResponse> getShops(ShopStatus status, int page, int size);
    ShopResponse updateShopStatus(int id, ShopStatus status);
    Map<String, Object> geocodeAllShops();

    // Notifications
    PageResponse<NotificationBroadcastResponse> getAllNotifications(int page);
    void sendNotification(SendNotificationRequest request);
    int deleteNotification(String broadcastId);

    // Finance & Withdrawals
    Map<String, Object> getFinanceOverview();
    Page<TransactionResponse> getFinanceTransactions(int page, int size, String type);
    
    Page<WithdrawalRequestResponse> getShopWithdrawals(int page, int size, String status);
    WithdrawalRequestResponse updateShopWithdrawalStatus(int id, String status, String adminNote);
    
    Page<WithdrawalRequestResponse> getUserWithdrawals(int page, int size, String status);
    WithdrawalRequestResponse updateUserWithdrawalStatus(int id, String status, String adminNote);
}
