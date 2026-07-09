package com.sang.sourcepattern.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    // Lombok sinh getter isUsed() cho field đã có tiền tố "is" — Jackson mặc định bóc
    // tiền tố "is" khi suy ra tên property JSON từ getter dạng "isXxx", nên nếu không
    // ép tên tường minh, JSON trả về sẽ là "used" thay vì "isUsed" và client đọc field
    // "isUsed" sẽ luôn nhận null/false bất kể giá trị thật (đã gây bug voucher đã dùng
    // vẫn hiện như chưa dùng trên app).
    @JsonProperty("isUsed")
    boolean isUsed;
    LocalDateTime expiresAt;
    LocalDateTime usedAt;
    /** false khi admin deactivate template — voucher vẫn trong ví nhưng không dùng được */
    boolean voucherActive;
}
