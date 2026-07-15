package com.sang.sourcepattern.scheduler;

import com.sang.sourcepattern.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueBookingScheduler {

    private final BookingService bookingService;

    /**
     * Chạy mỗi phút (60,000 ms) để quét và hủy các lịch hẹn quá hạn.
     */
    @Scheduled(fixedRate = 60000)
    public void scheduleCancelOverdueBookings() {
        log.debug("Running scheduled task to cancel overdue bookings...");
        try {
            bookingService.cancelOverdueBookings();
        } catch (Exception e) {
            log.error("Error occurred while auto-canceling overdue bookings: ", e);
        }
    }
}
