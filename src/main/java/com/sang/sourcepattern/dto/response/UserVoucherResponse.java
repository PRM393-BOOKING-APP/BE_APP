package com.sang.sourcepattern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVoucherResponse {
    int id;
    String code;
    String discountType;
    Double discountValue;
    Double minOrderValue;
    Double maxDiscountAmount;
    String targetTierName;
    boolean isUsed;
    LocalDateTime expiresAt;
    LocalDateTime usedAt;
    /** false khi admin deactivate template — voucher vẫn trong ví nhưng không dùng được */
    boolean voucherActive;
}
