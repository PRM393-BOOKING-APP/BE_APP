package com.sang.sourcepattern.service.impl;

import com.sang.sourcepattern.dto.request.CareLogRequest;
import com.sang.sourcepattern.dto.response.CareLogResponse;
import com.sang.sourcepattern.entity.Booking;
import com.sang.sourcepattern.entity.CareLog;
import com.sang.sourcepattern.entity.Staff;
import com.sang.sourcepattern.entity.User;
import com.sang.sourcepattern.exception.AppException;
import com.sang.sourcepattern.exception.ErrorCode;
import com.sang.sourcepattern.repository.BookingRepository;
import com.sang.sourcepattern.repository.CareLogRepository;
import com.sang.sourcepattern.repository.StaffRepository;
import com.sang.sourcepattern.repository.UserRepository;
import com.sang.sourcepattern.repository.ShopRepository;
import com.sang.sourcepattern.entity.Shop;
import com.sang.sourcepattern.service.CareLogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CareLogServiceImpl implements CareLogService {

    CareLogRepository careLogRepository;
    BookingRepository bookingRepository;
    StaffRepository staffRepository;
    UserRepository userRepository;
    ShopRepository shopRepository;

    @Override
    public CareLogResponse addLog(int bookingId, CareLogRequest request, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Staff staff = staffRepository.findByUser(user)
                .orElseGet(() -> {
                    Shop shop = shopRepository.findByOwnerId(user.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
                    Staff newStaff = Staff.builder()
                            .shop(shop)
                            .user(user)
                            .fullName(user.getFullName())
                            .role("SHOP_OWNER")
                            .phone(user.getPhone())
                            .isActive(true)
                            .build();
                    return staffRepository.save(newStaff);
                });

        CareLog careLog = CareLog.builder()
                .booking(booking)
                .staff(staff)
                .type(request.getType())
                .note(request.getNote())
                .imageUrl(request.getImageUrl())
                .build();

        careLog = careLogRepository.save(careLog);

        return mapToResponse(careLog);
    }

    @Override
    public List<CareLogResponse> getLogsByBooking(int bookingId) {
        return careLogRepository.findByBookingIdOrderByTimestampDesc(bookingId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CareLogResponse mapToResponse(CareLog careLog) {
        String staffName = "Unknown";
        if (careLog.getStaff() != null) {
            if (careLog.getStaff().getFullName() != null) {
                staffName = careLog.getStaff().getFullName();
            } else if (careLog.getStaff().getUser() != null) {
                staffName = careLog.getStaff().getUser().getFullName();
            }
        }
        return CareLogResponse.builder()
                .id(careLog.getId())
                .bookingId(careLog.getBooking().getId())
                .staffName(staffName)
                .type(careLog.getType())
                .note(careLog.getNote())
                .timestamp(careLog.getTimestamp())
                .imageUrl(careLog.getImageUrl())
                .build();
    }
}
