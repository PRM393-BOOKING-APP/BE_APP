package com.sang.sourcepattern.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShopRegistrationRequest {
    @NotBlank(message = "SHOP_NAME_REQUIRED")
    String shopName;

    @NotBlank(message = "SHOP_TYPE_REQUIRED")
    String shopType;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "EMAIL_REQUIRED")
    String email;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^(03|05|07|08|09)[1-9]{8}$", message = "INVALID_PHONE")
    String phone;

    @NotBlank(message = "ADDRESS_REQUIRED")
    String address;

    @NotBlank(message = "CITY_REQUIRED")
    String city;

    @Size(min = 10, message = "DESCRIPTION_TOO_SHORT")
    String description;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,16}$", message = "INVALID_PASSWORD")
    String password;

    String licenseImageUrl;

    String licenseNumber;

    String logoUrl;

    String bannerUrl;
}
