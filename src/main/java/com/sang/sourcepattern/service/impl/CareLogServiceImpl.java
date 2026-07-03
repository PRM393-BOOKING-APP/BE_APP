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

    @Override
    public CareLogResponse addLog(int bookingId, CareLogRequest request, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // A hired Staff member always wins. If the caller isn't Staff but is
        // the SHOP_OWNER of this booking's shop, allow it too — this project
        // has no separate Staff mobile app, so the shop owner does the work
        // a staff member would otherwise do.
        Staff staff = staffRepository.findByUser(user).orElse(null);
        if (staff == null) {
            boolean isShopOwner = booking.getShop() != null
                    && booking.getShop().getOwner() != null
                    && booking.getShop().getOwner().getId() == user.getId();
            if (!isShopOwner) {
                throw new AppException(ErrorCode.STAFF_NOT_FOUND);
            }
        }

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
        String staffName;
        if (careLog.getStaff() != null) {
            staffName = careLog.getStaff().getUser().getFullName();
        } else if (careLog.getBooking() != null && careLog.getBooking().getShop() != null) {
            staffName = careLog.getBooking().getShop().getShopName() + " (Chủ cửa hàng)";
        } else {
            staffName = null;
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
