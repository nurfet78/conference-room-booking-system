package org.nurfet.bookingsystem.config;

import org.nurfet.bookingsystem.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingCleanupScheduler.class);

    private final BookingService bookingService;

    public BookingCleanupScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredBookings() {
        try {
            int count = bookingService.markExpiredBookings();
            if (count > 0) {
                log.info("Помечено истекших бронирований: {}", count);
            }
        } catch (Exception e) {
            log.error("Ошибка при очистке бронирований: {}", e.getMessage());
        }
    }
}
