package com.sang.sourcepattern.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopResponse {
    int id;
    String shopName;
    String shopType;
    String email;
    String phone;
    String address;
    String city;
    Double latitude;
    Double longitude;
    String description;
    String licenseNumber;
    String licenseImageUrl;
    String logoUrl;
    String bannerUrl;
    String galleryUrls;
    java.util.List<StaffResponse> staffs;
    java.util.List<String> serviceNames;
    String openTime;
    String closeTime;
    String workingDays;
    String offDays;
    boolean isVerified;
    float ratingAvg;
    int ownerId;
    /** MANUAL | OPEN_POOL | AUTO */
    String assignmentMode;
    /** PENDING | APPROVED | REJECTED */
    String status;
    /** Số phút châm chước No-Show */
    int lateGracePeriod;

    /** Khoảng cách (km) tới vị trí người dùng — chỉ có giá trị khi search kèm lat/lng */
    Double distanceKm;
    /** Giá thấp nhất trong các dịch vụ đang hoạt động của shop */
    java.math.BigDecimal startingPrice;
}
