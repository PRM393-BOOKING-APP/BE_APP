package com.sang.sourcepattern.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    Long id;
    String code;
    String targetTierName;
    Double requiredSpending;
    String discountType;
    Double discountValue;
    Integer validDays;
    // Xem ghi chú trong UserVoucherResponse — không ép tên, Jackson sẽ serialize
    // field này thành "used" thay vì "isUsed" (getter Lombok isUsed() bị Jackson bóc "is").
    @JsonProperty("isUsed")
    Boolean isUsed;
}
