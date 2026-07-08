package com.sang.sourcepattern.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.sang.sourcepattern.entity.Notification;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopBroadcastNotificationRequest {

    @NotBlank(message = "Title is required")
    String title;

    @NotBlank(message = "Content is required")
    String content;

    /**
     * Kiểu gửi:
     * - SINGLE             : gửi cho 1 user cụ thể (cần userId)
     * - ALL_CUSTOMERS      : tất cả khách hàng
     * - UPCOMING_BOOKINGS  : khách có lịch sắp tới
     * - COMPLETED_BOOKINGS : khách đã hoàn thành dịch vụ
     */
    @NotNull(message = "Target type is required")
    TargetType targetType;

    /** Chỉ cần khi targetType = SINGLE */
    Integer userId;

    /** Tùy chọn gửi theo Email khi targetType = SINGLE */
    String targetEmail;

    /** Loại thông báo, mặc định là GENERAL nếu không truyền */
    @Builder.Default
    Notification.NotificationType notificationType = Notification.NotificationType.GENERAL;

    /** Tên loại thông báo tuỳ chỉnh (khi notificationType = CUSTOM) */
    String customTypeName;

    public enum TargetType {
        SINGLE, ALL_CUSTOMERS, UPCOMING_BOOKINGS, COMPLETED_BOOKINGS
    }
}
