package com.sang.sourcepattern.controller;

import com.sang.sourcepattern.dto.request.VoucherCreationRequest;
import com.sang.sourcepattern.dto.response.ApiResponse;
import com.sang.sourcepattern.entity.Notification;
import com.sang.sourcepattern.entity.Voucher;
import com.sang.sourcepattern.entity.MembershipTier;
import com.sang.sourcepattern.exception.AppException;
import com.sang.sourcepattern.exception.ErrorCode;
import com.sang.sourcepattern.repository.MembershipTierRepository;
import com.sang.sourcepattern.repository.NotificationRepository;
import com.sang.sourcepattern.repository.UserVoucherRepository;
import com.sang.sourcepattern.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VoucherController {

    VoucherRepository voucherRepository;
    MembershipTierRepository membershipTierRepository;
    UserVoucherRepository userVoucherRepository;
    NotificationRepository notificationRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Voucher>> getAllVouchers() {
        return ApiResponse.<List<Voucher>>builder()
                .result(voucherRepository.findAll())
                .build();
    }

    @GetMapping("/public")
    public ApiResponse<List<Voucher>> getPublicVouchers() {
        return ApiResponse.<List<Voucher>>builder()
                .result(voucherRepository.findAll())
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Voucher> createVoucher(@RequestBody VoucherCreationRequest request) {
        MembershipTier targetTier = membershipTierRepository.findByName(request.getTargetTierName())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .targetTier(targetTier)
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minOrderValue(request.getMinOrderValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .validDays(request.getValidDays())
                .issueQuantity(request.getIssueQuantity() != null ? request.getIssueQuantity() : 1)
                .build();

        return ApiResponse.<Voucher>builder()
                .result(voucherRepository.save(voucher))
                .message("Voucher created successfully")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Voucher> updateVoucher(@PathVariable Integer id, @RequestBody VoucherCreationRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        MembershipTier targetTier = membershipTierRepository.findByName(request.getTargetTierName())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        voucher.setCode(request.getCode());
        voucher.setTargetTier(targetTier);
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setValidDays(request.getValidDays());
        voucher.setIssueQuantity(request.getIssueQuantity() != null ? request.getIssueQuantity() : 1);

        return ApiResponse.<Voucher>builder()
                .result(voucherRepository.save(voucher))
                .message("Voucher updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiResponse<Void> deleteVoucher(@PathVariable Integer id) {
        voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        long issuedCount = userVoucherRepository.findByVoucherId(id).size();

        if (issuedCount > 0) {
            // Already issued to users — cannot hard-delete, must use deactivate
            return ApiResponse.<Void>builder()
                    .code(400)
                    .message(String.format(
                        "Không thể xóa: voucher đã được phát cho %d user. " +
                        "Dùng 'Vô hiệu hóa' để ngăn sử dụng tiếp.",
                        issuedCount
                    ))
                    .build();
        }

        // Safe: never issued to anyone
        voucherRepository.deleteById(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa voucher template thành công")
                .build();
    }

    /**
     * Deactivate: ngăn phát thêm + voucher trong ví user không apply được nữa.
     * Không xóa record nào — data toàn vẹn, lịch sử booking không bị ảnh hưởng.
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ApiResponse<Void> deactivateVoucher(@PathVariable Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        if (!voucher.isActive()) {
            return ApiResponse.<Void>builder()
                    .message("Voucher này đã được vô hiệu hóa trước đó")
                    .build();
        }

        voucher.setActive(false);
        voucherRepository.save(voucher);

        // Báo cho từng user đang giữ voucher này (chưa dùng) biết lý do voucher không còn
        // dùng được nữa, thay vì để họ tự phát hiện khi mở "Ưu đãi của tôi" mà không rõ vì sao.
        var affectedHolders = userVoucherRepository.findByVoucherIdAndIsUsedFalse(id);
        for (var uv : affectedHolders) {
            notificationRepository.save(Notification.builder()
                    .user(uv.getUser())
                    .title("Voucher đã ngừng áp dụng")
                    .content(String.format(
                            "Voucher %s bạn đang giữ đã bị quản trị viên thu hồi và không thể sử dụng nữa. " +
                            "Vào mục 'Ưu đãi của tôi' để xem các ưu đãi khác hiện có.",
                            voucher.getCode()))
                    .notificationType(Notification.NotificationType.PROMOTION)
                    .build());
        }

        long affectedUsers = affectedHolders.size();
        return ApiResponse.<Void>builder()
                .message(affectedUsers > 0
                    ? String.format("Đã vô hiệu hóa. %d user đang có voucher này sẽ không dùng được nữa và đã được thông báo.", affectedUsers)
                    : "Đã vô hiệu hóa. Không có user nào bị ảnh hưởng.")
                .build();
    }

    /**
     * Reactivate: kích hoạt lại voucher template đã bị vô hiệu hóa.
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> activateVoucher(@PathVariable Integer id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        voucher.setActive(true);
        voucherRepository.save(voucher);
        return ApiResponse.<Void>builder()
                .message("Đã kích hoạt lại voucher thành công")
                .build();
    }
}
