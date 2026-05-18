package org.nurfet.bookingsystem.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nurfet.bookingsystem.service.BookingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupScheduler {

    private final BookingService service;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void cleanupExpiredBookings() {

        try {
            int count = service.markExpiredBookings();

            if (count > 0) {
                log.info("Всего помечено истекших бронирований: {}", count);
            }
        } catch (Exception e) {
            log.error("Ошибка при очистке истекших бронирований: {}", e.getMessage());
        }
    }
}