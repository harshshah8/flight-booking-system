package com.fbs.booking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "booking")
public class BookingConfig {

    private Timeout timeout = new Timeout();

    @Data
    public static class Timeout {
        private int seatReservationMinutes = 5;
    }
}