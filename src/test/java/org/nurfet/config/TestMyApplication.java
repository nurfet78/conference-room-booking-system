package org.nurfet.config;

import org.nurfet.bookingsystem.BookingSystemApplication;
import org.springframework.boot.SpringApplication;

public class TestMyApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(BookingSystemApplication::main)
                .with(ContainersConfig.class)
                .withAdditionalProfiles("dev")
                .run(args);
    }
}